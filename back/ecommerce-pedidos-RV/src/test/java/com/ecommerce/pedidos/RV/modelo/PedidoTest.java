package com.ecommerce.pedidos.RV.modelo;

import com.ecommerce.pedidos.RV.excecao.EstoqueInsuficienteException;
import com.ecommerce.pedidos.RV.excecao.PagamentoRecusadoException;
import com.ecommerce.pedidos.RV.excecao.PedidoInvalidoException;
import com.ecommerce.pedidos.RV.modelo.pagamento.ProcessadorPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Teste unitário puro do domínio: não sobe Spring nem banco. */
class PedidoTest {

    private Cliente cliente;
    private Produto notebook;
    private Pedido pedido;

    @BeforeEach
    void prepararCenario() {
        cliente = new Cliente("Ana Silva", "123.456.789-00", "ana@email.com");
        notebook = new Produto("Notebook", new BigDecimal("3000.00"), 5);
        pedido = new Pedido(cliente);
    }

    @Test
    @DisplayName("Pedido novo começa ABERTO")
    void pedidoNovoComecaAberto() {
        assertEquals(SituacaoPedido.ABERTO, pedido.getSituacao());
        assertFalse(pedido.isPago());
    }

    @Test
    @DisplayName("Deve somar o total corretamente")
    void deveSomarTotal() throws Exception {
        pedido.adicionarItem(notebook, 2);

        // compareTo evita divergência de escala no BigDecimal
        assertEquals(0, new BigDecimal("6000.00").compareTo(pedido.calcularValorTotal()));
        assertEquals(3, notebook.getQuantidadeEmEstoque());
    }

    @Test
    @DisplayName("Não deve adicionar item quando falta estoque")
    void naoDeveAdicionarItemSemEstoque() {
        assertThrows(EstoqueInsuficienteException.class, () -> pedido.adicionarItem(notebook, 50));
        assertTrue(pedido.getItens().isEmpty());
    }

    @Test
    @DisplayName("Deve recusar pagamento de pedido sem itens")
    void deveRecusarPagamentoDePedidoSemItens() {
        ProcessadorPagamento pix = mock(ProcessadorPagamento.class);
        assertThrows(PedidoInvalidoException.class, () -> pedido.pagar(pix));
    }

    @Test
    @DisplayName("Deve marcar como PAGO quando o processador aprova")
    void deveMarcarComoPago() throws Exception {
        ProcessadorPagamento aprovado = mock(ProcessadorPagamento.class);
        when(aprovado.processar(any())).thenReturn(true);

        pedido.adicionarItem(notebook, 1);
        pedido.pagar(aprovado);

        assertTrue(pedido.isPago());
    }

    @Test
    @DisplayName("Deve lançar PagamentoRecusadoException quando o processador recusa")
    void deveRecusarPagamento() throws Exception {
        ProcessadorPagamento recusado = mock(ProcessadorPagamento.class);
        when(recusado.processar(any())).thenReturn(false);

        pedido.adicionarItem(notebook, 1);

        assertThrows(PagamentoRecusadoException.class, () -> pedido.pagar(recusado));
        assertEquals(SituacaoPedido.ABERTO, pedido.getSituacao());
    }

    @Test
    @DisplayName("Cancelar devolve os itens ao estoque")
    void cancelarDevolveEstoque() throws Exception {
        pedido.adicionarItem(notebook, 2);
        pedido.cancelar();

        assertEquals(SituacaoPedido.CANCELADO, pedido.getSituacao());
        assertEquals(5, notebook.getQuantidadeEmEstoque());
    }

    @Test
    @DisplayName("Deve recusar pedido sem cliente no construtor")
    void deveRecusarPedidoSemCliente() {
        assertThrows(IllegalArgumentException.class, () -> new Pedido(null));
    }
}
