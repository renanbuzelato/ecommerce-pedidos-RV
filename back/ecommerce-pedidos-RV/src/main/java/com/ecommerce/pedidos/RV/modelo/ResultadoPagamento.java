package com.ecommerce.pedidos.RV.modelo;

/**
 * Resultado da simulação de pagamento.
 * APROVADO: pedido vira PAGO. RECUSADO: pedido continua ABERTO (pode tentar de novo).
 * PENDENTE: boleto gerado, aguardando compensação.
 */
public enum ResultadoPagamento {
    APROVADO,
    RECUSADO,
    PENDENTE
}
