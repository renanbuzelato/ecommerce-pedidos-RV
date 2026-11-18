package com.ecommerce.pedidos.RV.servico;

import com.ecommerce.pedidos.RV.excecao.ECommerceException;
import com.ecommerce.pedidos.RV.excecao.EstoqueInsuficienteException;
import com.ecommerce.pedidos.RV.excecao.PagamentoRecusadoException;
import com.ecommerce.pedidos.RV.excecao.PedidoInvalidoException;
import com.ecommerce.pedidos.RV.excecao.PedidoNaoEncontradoException;
import com.ecommerce.pedidos.RV.modelo.Cliente;
import com.ecommerce.pedidos.RV.modelo.Pedido;
import com.ecommerce.pedidos.RV.modelo.Produto;
import com.ecommerce.pedidos.RV.modelo.SituacaoPedido;
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
}
