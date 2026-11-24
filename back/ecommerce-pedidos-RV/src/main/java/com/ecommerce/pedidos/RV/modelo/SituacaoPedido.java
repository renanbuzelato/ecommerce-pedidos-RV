package com.ecommerce.pedidos.RV.modelo;

/**
 * Persistido como texto (@Enumerated(EnumType.STRING)): reordenar os valores
 * aqui não estraga os dados já gravados, o que aconteceria com ORDINAL.
 *
 * Aula 15: ENVIADO e ENTREGUE completam o ciclo de vida exigido pela interface
 * (ABERTO -> PAGO -> ENVIADO -> ENTREGUE; ABERTO -> CANCELADO).
 */
public enum SituacaoPedido {
    ABERTO,
    PAGO,
    ENVIADO,
    ENTREGUE,
    CANCELADO
}
