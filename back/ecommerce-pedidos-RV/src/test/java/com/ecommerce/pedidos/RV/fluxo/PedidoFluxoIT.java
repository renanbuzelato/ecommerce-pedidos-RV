package com.ecommerce.pedidos.RV.fluxo;

import com.ecommerce.pedidos.RV.modelo.Cliente;
import com.ecommerce.pedidos.RV.modelo.Pedido;
import com.ecommerce.pedidos.RV.modelo.Produto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PedidoFluxoIT {

    private Cliente cliente;
    private Produto notebook;

    @BeforeEach
    void prepararCenario() {
        cliente = new Cliente("123", "Ana Silva", "ana@email.com");
        notebook = new Produto("PROD-01", "Notebook", new BigDecimal("3000.00"), 5);
    }

    @Test
    @DisplayName("Deve criar, somar e pagar um pedido, baixando o estoque")
    void deveExecutarFluxoCompletoDePedido() throws Exception {
        // Arrange
        Pedido pedido = new Pedido("PED-001", cliente);
        pedido.adicionarItem(notebook, 2);

        // Act - Simula o pagamento do pedido
        pedido.pagarCom("PIX");

        // Assert - O assertAll executa todas as verificações mesmo que alguma falhe
        assertAll(
            () -> assertEquals(0, new BigDecimal("6000.00").compareTo(pedido.calcularValorTotal())),
            () -> assertEquals(3, notebook.getEstoque()), // Verifica se o estoque baixou de 5 para 3
            () -> assertEquals(1, pedido.getItens().size())
        );
    }
}