package com.ecommerce.pedidos.RV.modelo;

import com.ecommerce.pedidos.RV.excecao.EstoqueInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoTest {

    private Produto notebook;

    @BeforeEach
    void prepararCenario() {
        notebook = new Produto("PROD-01", "Notebook", new BigDecimal("3000.00"), 5);
    }

    @Test
    @DisplayName("Deve baixar o estoque quando há quantidade suficiente")
    void deveBaixarEstoqueQuandoHaQuantidadeSuficiente() throws Exception {
        notebook.baixarEstoque(2);
        assertEquals(3, notebook.getEstoque());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o estoque é insuficiente")
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        EstoqueInsuficienteException erro = assertThrows(
                EstoqueInsuficienteException.class,
                () -> notebook.baixarEstoque(50)
        );

        assertEquals(50, erro.getQuantidadeSolicitada());
        assertTrue(erro.getMessage().contains("Notebook"));
        assertEquals(5, notebook.getEstoque()); // Garante que o estoque não sofreu alteração
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -50})
    @DisplayName("Deve recusar quantidade não positiva na baixa de estoque")
    void deveRecusarQuantidadeNaoPositiva(int quantidade) {
        assertThrows(
                IllegalArgumentException.class,
                () -> notebook.baixarEstoque(quantidade)
        );
    }
}