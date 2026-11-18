package com.ecommerce.pedidos.RV.fluxo;

import com.ecommerce.pedidos.RV.modelo.Cliente;
import com.ecommerce.pedidos.RV.modelo.Pedido;
import com.ecommerce.pedidos.RV.modelo.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class PedidoFluxoIT {

    @Test
    @DisplayName("Deve validar a estrutura basica do fluxo de pedido")
    void deveValidarFluxoDePedido() throws Exception {
        Cliente cliente = new Cliente("Ana Silva", "123.456.789-00", "ana@email.com");
        Produto notebook = new Produto("Notebook", new BigDecimal("3000.00"), 5);
        Pedido pedido = new Pedido(cliente);
        pedido.adicionarItem(notebook, 2);

        assertAll("Verificação de fluxo",
            () -> assertEquals(0, new BigDecimal("6000.00").compareTo(pedido.calcularValorTotal())),
            () -> assertEquals(3, notebook.getQuantidadeEmEstoque())
        );
    }
}
