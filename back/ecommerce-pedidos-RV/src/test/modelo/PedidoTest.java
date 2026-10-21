package com.ecommerce.pedidos.RV.modelo;

import com.ecommerce.pedidos.RV.excecao.EstoqueInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PedidoTest {

    private Cliente cliente;
    private Produto notebook;
    private Pedido pedido;

    @BeforeEach
    void prepararCenario() {
        cliente = new Cliente("123", "Ana Silva", "ana@email.com");
        notebook = new Produto("PROD-01", "Notebook", new BigDecimal("3000.00"), 5);
        pedido = new Pedido("PED-001", cliente);
    }

    @Test
    @DisplayName("Deve somar o total corretamente com dois itens")
    void deveSomarTotalComDoisItens() throws Exception {
        pedido.adicionarItem(notebook, 2);

        // Uso obrigatório de compareTo para evitar divergências de escala no BigDecimal
        assertEquals(0, new BigDecimal("6000.00").compareTo(pedido.calcularValorTotal()));
    }

    @Test
    @DisplayName("Deve recusar pagamento de pedido sem itens")
    void deveRecusarPagamentoDePedidoSemItens() {
        assertThrows(
                IllegalStateException.class,
                () -> pedido.pagarCom("PIX")
        );
    }

    @Test
    @DisplayName("Deve recusar pedido sem cliente no construtor")
    void deveRecusarPedidoSemCliente() {
        assertThrows(
                IllegalArgumentException.class,
                () -> new Pedido("PED-002", null)
        );
    }
}