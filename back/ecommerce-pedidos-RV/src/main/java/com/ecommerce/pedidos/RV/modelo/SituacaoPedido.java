package com.ecommerce.pedidos.RV.modelo;

/**
 * Persistido como texto (@Enumerated(EnumType.STRING)): reordenar os valores
 * aqui não estraga os dados já gravados, o que aconteceria com ORDINAL.
 */
public enum SituacaoPedido {
    ABERTO,
    PAGO,
    CANCELADO
}
