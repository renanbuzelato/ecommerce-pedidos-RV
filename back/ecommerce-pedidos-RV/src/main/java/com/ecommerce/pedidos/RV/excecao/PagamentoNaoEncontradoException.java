package com.ecommerce.pedidos.RV.excecao;

/** GET /api/pedidos/{id}/pagamento de um pedido que ainda não teve nenhuma tentativa de pagamento. */
public class PagamentoNaoEncontradoException extends RuntimeException {

    public PagamentoNaoEncontradoException(Long pedidoId) {
        super("O pedido " + pedidoId + " ainda não tem pagamento registrado.");
    }
}
