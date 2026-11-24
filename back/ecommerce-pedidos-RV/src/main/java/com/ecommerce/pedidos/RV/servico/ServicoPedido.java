package com.ecommerce.pedidos.RV.servico;

import com.ecommerce.pedidos.RV.excecao.ECommerceException;
import com.ecommerce.pedidos.RV.excecao.EstoqueInsuficienteException;
import com.ecommerce.pedidos.RV.excecao.PagamentoRecusadoException;
import com.ecommerce.pedidos.RV.excecao.PedidoInvalidoException;
import com.ecommerce.pedidos.RV.excecao.PedidoNaoEncontradoException;
import com.ecommerce.pedidos.RV.modelo.Cliente;
import com.ecommerce.pedidos.RV.modelo.EnderecoEntrega;
import com.ecommerce.pedidos.RV.modelo.Pedido;
import com.ecommerce.pedidos.RV.modelo.Produto;
import com.ecommerce.pedidos.RV.modelo.RegistroPagamento;
import com.ecommerce.pedidos.RV.modelo.ResultadoPagamento;
import com.ecommerce.pedidos.RV.modelo.SituacaoPedido;
import com.ecommerce.pedidos.RV.modelo.TipoPagamento;
import com.ecommerce.pedidos.RV.modelo.pagamento.ProcessadorPagamento;
import com.ecommerce.pedidos.RV.repositorio.PedidoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Orquestra cliente + produto + pedido.
 *
 * ATENÇÃO: as exceções da Aula 09 (EstoqueInsuficienteException etc.) são
 * VERIFICADAS (extends Exception). Por padrão o @Transactional só faz rollback
 * com RuntimeException, então usamos rollbackFor = ECommerceException.class.
 */
@Service
public class ServicoPedido {

    private final PedidoRepository repositorio;
    private final ServicoCliente servicoCliente;
    private final ServicoProduto servicoProduto;
    private final CalculadoraFrete frete;

    public ServicoPedido(PedidoRepository repositorio,
                         ServicoCliente servicoCliente,
                         ServicoProduto servicoProduto,
                         CalculadoraFrete frete) {
        this.repositorio = repositorio;
        this.servicoCliente = servicoCliente;
        this.servicoProduto = servicoProduto;
        this.frete = frete;
    }

    @Transactional
    public Pedido abrir(Long clienteId) {
        Cliente cliente = servicoCliente.buscar(clienteId);
        return repositorio.save(new Pedido(cliente));
    }

    /**
     * Baixa o estoque e adiciona o item. Se faltar estoque, nada é gravado.
     * O INSERT do item sai pelo cascade do pedido; o UPDATE do estoque, pelo dirty checking.
     */
    @Transactional(rollbackFor = ECommerceException.class)
    public Pedido adicionarItem(Long pedidoId, Long produtoId, int quantidade) throws EstoqueInsuficienteException {
        Pedido pedido = buscarComItens(pedidoId);
        Produto produto = servicoProduto.buscar(produtoId);
        pedido.adicionarItem(produto, quantidade);
        return pedido;
    }

    @Transactional(rollbackFor = ECommerceException.class)
    public Pedido pagar(Long pedidoId, ProcessadorPagamento processador)
            throws PedidoInvalidoException, PagamentoRecusadoException {
        Pedido pedido = buscarComItens(pedidoId);
        pedido.pagar(processador);
        return pedido;
    }

    @Transactional(rollbackFor = ECommerceException.class)
    public Pedido cancelar(Long pedidoId) throws PedidoInvalidoException {
        Pedido pedido = buscarComItens(pedidoId);
        pedido.cancelar();   // devolve os itens ao estoque
        return pedido;
    }

    @Transactional(readOnly = true)
    public Pedido buscarComItens(Long id) {
        return repositorio.buscarComItens(id)
                .orElseThrow(() -> new PedidoNaoEncontradoException(id));
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorCliente(Long clienteId) {
        return repositorio.findByClienteId(clienteId);
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarPorSituacao(SituacaoPedido situacao) {
        return repositorio.findBySituacao(situacao);
    }

    @Transactional(readOnly = true)
    public BigDecimal calcularTotalComFrete(Long pedidoId) {
        BigDecimal subtotal = buscarComItens(pedidoId).calcularValorTotal();
        return subtotal.add(frete.calcular(subtotal));
    }

    /* ------------------------- Aula 15: operações usadas pela API REST ------------------------- */

    /** Item pedido pela interface: só o id do produto e a quantidade (o preço vem do banco). */
    public record ItemSolicitado(Long produtoId, int quantidade) { }

    /**
     * Cria o pedido a partir do carrinho, numa transação só: se faltar estoque em
     * QUALQUER item, nada é gravado (nem o pedido, nem as baixas de estoque anteriores).
     */
    @Transactional(rollbackFor = ECommerceException.class)
    public Pedido criar(Long clienteId, EnderecoEntrega endereco, List<ItemSolicitado> itens)
            throws EstoqueInsuficienteException, PedidoInvalidoException {
        if (itens == null || itens.isEmpty()) {
            throw new PedidoInvalidoException("Não é possível criar um pedido sem itens.");
        }
        if (endereco == null) {
            throw new PedidoInvalidoException("Informe o endereço de entrega.");
        }
        Cliente cliente = servicoCliente.buscar(clienteId);
        Pedido pedido = new Pedido(cliente);
        pedido.definirEnderecoEntrega(endereco);
        for (ItemSolicitado item : itens) {
            pedido.adicionarItem(servicoProduto.buscar(item.produtoId()), item.quantidade());
        }
        return repositorio.save(pedido);    // cascade grava os itens; dirty checking, o estoque
    }

    @Transactional(readOnly = true)
    public List<Pedido> listarTodos() {
        return repositorio.listarCompletos();
    }

    /** PATCH da situação: cada transição usa o método de negócio da entidade. */
    @Transactional(rollbackFor = ECommerceException.class)
    public Pedido alterarSituacao(Long pedidoId, SituacaoPedido nova) throws PedidoInvalidoException {
        Pedido pedido = buscarComItens(pedidoId);
        switch (nova) {
            case CANCELADO -> pedido.cancelar();
            case ENVIADO -> pedido.enviar();
            case ENTREGUE -> pedido.entregar();
            case PAGO -> pedido.confirmarCompensacaoBoleto();
            default -> throw new PedidoInvalidoException(
                    "Não é possível mudar o pedido de " + pedido.getSituacao() + " para " + nova + ".");
        }
        return pedido;
    }

    /** Resultado devolvido para a interface após uma tentativa de pagamento. */
    public record ResultadoProcessamento(Long pedidoId, TipoPagamento forma, ResultadoPagamento resultado,
                                         String comprovante, String descricao, String mensagem) { }

    /**
     * Processa o pagamento e REGISTRA o resultado, mesmo quando recusado: a recusa
     * não é erro do sistema, é uma resposta da operadora que o usuário precisa ver.
     * Boleto: o Boleto.processar() devolve false (ainda não compensado) -> PENDENTE.
     */
    @Transactional(rollbackFor = ECommerceException.class)
    public ResultadoProcessamento processarPagamento(Long pedidoId, TipoPagamento forma,
                                                     ProcessadorPagamento processador)
            throws PedidoInvalidoException {
        Pedido pedido = buscarComItens(pedidoId);
        ResultadoPagamento resultado;
        String mensagem;
        try {
            pedido.pagar(processador);
            resultado = ResultadoPagamento.APROVADO;
            mensagem = forma == TipoPagamento.PIX ? "Cobrança Pix confirmada." : "Pagamento autorizado pela operadora.";
        } catch (PagamentoRecusadoException e) {
            if (forma == TipoPagamento.BOLETO) {
                resultado = ResultadoPagamento.PENDENTE;
                mensagem = "Boleto gerado. O pedido será pago após a compensação (até 3 dias úteis).";
            } else {
                resultado = ResultadoPagamento.RECUSADO;
                mensagem = e.getMessage();
            }
        }
        String comprovante = processador.getComprovante();
        String descricao = processador.getDescricao();
        pedido.registrarPagamento(new RegistroPagamento(forma, resultado, comprovante, descricao));
        return new ResultadoProcessamento(pedidoId, forma, resultado, comprovante, descricao, mensagem);
    }
}
