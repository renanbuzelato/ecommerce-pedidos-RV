package com.ecommerce.pedidos.RV.excecao;

/**
 * Exceção não verificada (RuntimeException): o @Transactional faz rollback automático.
 * Na Aula 15 vira HTTP 409.
 */
public class ProdutoDuplicadoException extends RuntimeException {
    public ProdutoDuplicadoException(String nome) {
        super("Já existe um produto com o nome \"" + nome + "\".");
    }
}
