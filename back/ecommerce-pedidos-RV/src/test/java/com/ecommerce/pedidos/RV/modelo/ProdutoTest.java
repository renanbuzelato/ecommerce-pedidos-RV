package com.ecommerce.pedidos.RV.modelo;

import com.ecommerce.pedidos.RV.excecao.EstoqueInsuficienteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/** Teste unitário puro do domínio: não sobe Spring nem banco. */
class ProdutoTest {

    private Produto notebook;

    @BeforeEach
    void prepararCenario() {
        notebook = new Produto("Notebook", new BigDecimal("3000.00"), 5);
    }

    @Test
    @DisplayName("Deve baixar o estoque quando há quantidade suficiente")
    void deveBaixarEstoqueQuandoHaQuantidadeSuficiente() throws Exception {
        notebook.baixarEstoque(2);
        assertEquals(3, notebook.getQuantidadeEmEstoque());
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
        assertEquals(5, notebook.getQuantidadeEmEstoque()); // estoque não sofreu alteração
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1, -50})
    @DisplayName("Deve recusar quantidade não positiva na baixa de estoque")
    void deveRecusarQuantidadeNaoPositiva(int quantidade) {
        assertThrows(IllegalArgumentException.class, () -> notebook.baixarEstoque(quantidade));
    }

    @Test
    @DisplayName("Deve recusar produto com preço zero ou negativo")
    void deveRecusarPrecoInvalido() {
        assertThrows(IllegalArgumentException.class,
                () -> new Produto("Mouse", BigDecimal.ZERO, 1));
        assertThrows(IllegalArgumentException.class,
                () -> new Produto("Mouse", new BigDecimal("-1"), 1));
    }

    @Test
    @DisplayName("Deve recusar produto sem nome")
    void deveRecusarProdutoSemNome() {
        assertThrows(IllegalArgumentException.class,
                () -> new Produto("  ", new BigDecimal("10"), 1));
    }

    @Test
    @DisplayName("Produto nasce ativo e pode ser desativado")
    void produtoNasceAtivo() {
        assertTrue(notebook.isAtivo());
        notebook.desativar();
        assertFalse(notebook.isAtivo());
    }
}
