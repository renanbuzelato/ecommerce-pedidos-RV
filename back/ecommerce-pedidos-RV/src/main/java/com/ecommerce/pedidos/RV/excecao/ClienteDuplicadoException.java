package com.ecommerce.pedidos.RV.excecao;

/**
 * Exceção não verificada (RuntimeException): o @Transactional faz rollback automático.
 * Na Aula 15 vira HTTP 409.
 */
public class ClienteDuplicadoException extends RuntimeException {
    public ClienteDuplicadoException(String campo, String valor) {
        super("Já existe um cliente com " + campo + " \"" + valor + "\".");
    }
}
