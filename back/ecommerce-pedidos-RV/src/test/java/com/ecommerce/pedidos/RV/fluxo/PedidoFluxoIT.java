package com.ecommerce.pedidos.RV.fluxo;

import com.ecommerce.pedidos.RV.modelo.Cliente;
import com.ecommerce.pedidos.RV.modelo.Pedido;
import com.ecommerce.pedidos.RV.modelo.Produto;
import com.ecommerce.pedidos.RV.modelo.pagamento.Pix;
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
        cliente = new Cliente("Ana Silva", "ana@email.com", "123.456.789-00");
        notebook = new Produto("Notebook", 3000.00, 5);
    }

    @Test
    @DisplayName("Deve criar, somar e pagar um pedido, baixando o estoque")
    void deveExecutarFluxoCompletoDePedido() throws Exception {
        // Construtor sem cliente e adiciona o item
        Pedido pedido = new Pedido();
        pedido.adicionarItem(notebook, 2);

        // Paga via Pix
        pedido.pagar(new Pix(new BigDecimal("6000.00"), "ana@email.com"));

        // Comparação entre BigDecimals
        assertAll(
            () -> assertEquals(new BigDecimal("6000.00"), pedido.getValorTotal()),
            () -> assertEquals(3, notebook.getQuantidadeEmEstoque())
        );
    }
}