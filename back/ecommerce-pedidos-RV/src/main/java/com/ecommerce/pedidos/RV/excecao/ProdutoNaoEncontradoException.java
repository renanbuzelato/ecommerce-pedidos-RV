package com.ecommerce.pedidos.RV.excecao;

/**
 * Exceção não verificada (RuntimeException): o @Transactional faz rollback automático.
 * Na Aula 15 vira HTTP 404.
 */
public class ProdutoNaoEncontradoException extends RuntimeException {
    public ProdutoNaoEncontradoException(Long id) {
        super("Produto " + id + " não encontrado.");
    }
}
