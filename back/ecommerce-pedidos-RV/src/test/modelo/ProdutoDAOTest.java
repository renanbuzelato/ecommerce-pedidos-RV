package com.ecommerce.pedidos.RV.repositorio;

import com.ecommerce.pedidos.RV.modelo.Produto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ProdutoDAOTest {

    @Test
    @DisplayName("Deve atualizar o preço de um produto existente")
    void deveAtualizarProduto() {
        ProdutoDAO dao = new ProdutoDAO();
        
        // Exemplo: testar a atualização de um produto com ID 1
        Produto produto = new Produto("Notebook Gamer", new BigDecimal("3500.00"), 8);
        produto.setId(1L);

        // Se o produto com ID 1 existir na base de dados, a atualização não lança exceção
        assertDoesNotThrow(() -> dao.atualizar(produto));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar excluir produto inexistente")
    void deveLancarExcecaoAoExcluirProdutoInexistente() {
        ProdutoDAO dao = new ProdutoDAO();
        
        // ID que não existe na tua base de dados
        assertThrows(RuntimeException.class, () -> dao.excluir(99999L));
    }
}