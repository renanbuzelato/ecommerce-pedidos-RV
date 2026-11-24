package com.ecommerce.pedidos.RV.modelo;

import com.ecommerce.pedidos.RV.excecao.EstoqueInsuficienteException;
import com.ecommerce.pedidos.RV.excecao.PagamentoRecusadoException;
import com.ecommerce.pedidos.RV.excecao.PedidoInvalidoException;
import com.ecommerce.pedidos.RV.modelo.pagamento.ProcessadorPagamento;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Associação com RESTRICT: sem cascade. Apagar pedido não apaga cliente.
    // LAZY porque o padrão do @ManyToOne (EAGER) carregaria o cliente sempre (N+1).
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    // Composição: o item não existe sem o pedido -> cascade ALL + orphanRemoval.
    // @OneToMany já é LAZY por padrão (adequado).
    @OneToMany(mappedBy = "pedido",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    // @JdbcTypeCode(VARCHAR): o Hibernate 6 usaria o tipo ENUM nativo do MySQL;
    // forçamos VARCHAR para bater com o schema.sql (validate).
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private SituacaoPedido situacao;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao;

    // ---- Aula 15: dados que a interface exibe ----

    @Embedded
    private EnderecoEntrega enderecoEntrega;

    @Embedded
    private RegistroPagamento pagamento;

    // Linha do tempo do pedido. EAGER porque o detalhe do pedido sempre mostra o histórico;
    // carregado num SELECT separado (não entra no JOIN FETCH dos itens, evitando MultipleBagFetchException).
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "pedido_historico", joinColumns = @JoinColumn(name = "pedido_id"))
    @OrderBy("data ASC")
    private List<RegistroSituacao> historico = new ArrayList<>();

    protected Pedido() { }                   // exigido pelo JPA

    public Pedido(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Pedido precisa de um cliente.");
        }
        this.cliente = cliente;
        this.dataCriacao = LocalDateTime.now();
        mudarSituacao(SituacaoPedido.ABERTO);
    }

    /** Toda mudança de situação passa por aqui: assim o histórico nunca fica para trás. */
    private void mudarSituacao(SituacaoPedido nova) {
        this.situacao = nova;
        this.historico.add(new RegistroSituacao(nova, LocalDateTime.now()));
    }

    public void adicionarItem(Produto produto, int quantidade) throws EstoqueInsuficienteException {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
        if (situacao != SituacaoPedido.ABERTO) {
            throw new IllegalStateException("Só é possível alterar um pedido em aberto.");
        }
        if (!produto.isAtivo()) {
            throw new IllegalStateException("Produto " + produto.getNome() + " está inativo.");
        }

        // Tenta baixar do estoque (lança exceção sem alterar nada se não houver)
        produto.baixarEstoque(quantidade);
        itens.add(new ItemPedido(this, produto, quantidade));
    }

    public BigDecimal calcularValorTotal() {
        return itens.stream()
                .map(ItemPedido::getPrecoTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void pagar(ProcessadorPagamento processador) throws PedidoInvalidoException, PagamentoRecusadoException {
        if (processador == null) {
            throw new IllegalArgumentException("Processador de pagamento não pode ser nulo.");
        }
        if (situacao == SituacaoPedido.PAGO || situacao == SituacaoPedido.ENVIADO
                || situacao == SituacaoPedido.ENTREGUE) {
            throw new PedidoInvalidoException("Este pedido já foi pago anteriormente.");
        }
        if (situacao == SituacaoPedido.CANCELADO) {
            throw new PedidoInvalidoException("Não é possível pagar um pedido cancelado.");
        }
        if (itens.isEmpty()) {
            throw new PedidoInvalidoException("Não é possível pagar um pedido sem itens.");
        }

        boolean aprovado = processador.processar(calcularValorTotal());
        if (!aprovado) {
            throw new PagamentoRecusadoException(processador.getClass().getSimpleName(),
                    "Saldo/Transação recusada pela operadora.");
        }

        mudarSituacao(SituacaoPedido.PAGO);
    }

    /** Cancela o pedido e devolve os itens ao estoque. */
    public void cancelar() throws PedidoInvalidoException {
        if (situacao == SituacaoPedido.CANCELADO) {
            throw new PedidoInvalidoException("Pedido já está cancelado.");
        }
        if (situacao != SituacaoPedido.ABERTO) {
            throw new PedidoInvalidoException("Pedido pago não pode ser cancelado.");
        }
        for (ItemPedido item : itens) {
            item.getProduto().reporEstoque(item.getQuantidade());
        }
        mudarSituacao(SituacaoPedido.CANCELADO);
    }

    /** PAGO -> ENVIADO */
    public void enviar() throws PedidoInvalidoException {
        if (situacao != SituacaoPedido.PAGO) {
            throw new PedidoInvalidoException("Só é possível enviar um pedido pago.");
        }
        mudarSituacao(SituacaoPedido.ENVIADO);
    }

    /** ENVIADO -> ENTREGUE */
    public void entregar() throws PedidoInvalidoException {
        if (situacao != SituacaoPedido.ENVIADO) {
            throw new PedidoInvalidoException("Só é possível entregar um pedido enviado.");
        }
        mudarSituacao(SituacaoPedido.ENTREGUE);
    }

    /** Guarda o resultado da última tentativa de pagamento (aprovada, recusada ou pendente). */
    public void registrarPagamento(RegistroPagamento registro) {
        this.pagamento = registro;
    }

    /** Boleto pendente compensado: único caminho de ABERTO -> PAGO sem novo processamento. */
    public void confirmarCompensacaoBoleto() throws PedidoInvalidoException {
        boolean boletoPendente = pagamento != null
                && pagamento.getForma() == TipoPagamento.BOLETO
                && pagamento.getResultado() == ResultadoPagamento.PENDENTE;
        if (situacao != SituacaoPedido.ABERTO || !boletoPendente) {
            throw new PedidoInvalidoException("Pedido só fica pago após um pagamento aprovado.");
        }
        this.pagamento = pagamento.comResultado(ResultadoPagamento.APROVADO);
        mudarSituacao(SituacaoPedido.PAGO);
    }

    public void definirEnderecoEntrega(EnderecoEntrega endereco) {
        if (situacao != SituacaoPedido.ABERTO) {
            throw new IllegalStateException("Só é possível alterar um pedido em aberto.");
        }
        this.enderecoEntrega = endereco;
    }

    public boolean isPago() {
        return situacao == SituacaoPedido.PAGO;
    }

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public List<ItemPedido> getItens() { return Collections.unmodifiableList(itens); }
    public SituacaoPedido getSituacao() { return situacao; }
    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public EnderecoEntrega getEnderecoEntrega() { return enderecoEntrega; }
    public RegistroPagamento getPagamento() { return pagamento; }
    public List<RegistroSituacao> getHistorico() { return Collections.unmodifiableList(historico); }
}
