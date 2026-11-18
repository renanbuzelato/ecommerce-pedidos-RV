package com.ecommerce.pedidos.RV.excecao;

/**
 * Exceção não verificada (RuntimeException): o @Transactional faz rollback automático.
 * Na Aula 15 vira HTTP 404.
 */
public class PedidoNaoEncontradoException extends RuntimeException {
    public PedidoNaoEncontradoException(Long id) {
        super("Pedido " + id + " não encontrado.");
    }
}
