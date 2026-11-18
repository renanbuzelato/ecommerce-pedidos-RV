package com.ecommerce.pedidos.RV.excecao;

/**
 * Exceção não verificada (RuntimeException): o @Transactional faz rollback automático.
 * Na Aula 15 vira HTTP 404.
 */
public class ClienteNaoEncontradoException extends RuntimeException {
    public ClienteNaoEncontradoException(Long id) {
        super("Cliente " + id + " não encontrado.");
    }
}
