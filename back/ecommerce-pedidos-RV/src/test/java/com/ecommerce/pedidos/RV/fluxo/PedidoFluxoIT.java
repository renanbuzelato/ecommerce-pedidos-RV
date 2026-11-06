package com.ecommerce.pedidos.RV.fluxo;

import com.ecommerce.pedidos.RV.modelo.Pedido;
import com.ecommerce.pedidos.RV.modelo.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PedidoFluxoIT {

    @Test
    @DisplayName("Deve validar a estrutura basica do fluxo de pedido")
    void deveValidarFluxoDePedido() throws Exception {
        Produto notebook = new Produto("Notebook", 3000.0, 5);
        Pedido pedido = new Pedido();
        pedido.adicionarItem(notebook, 2);

        assertAll("Verificação de fluxo",
            () -> assertEquals(6000.0, pedido.getValorTotal(), 0.001),
            () -> assertEquals(3, notebook.getQuantidadeEmEstoque())
        );
    }
}