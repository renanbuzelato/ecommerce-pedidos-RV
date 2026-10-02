package com.ecommerce.pedidos.RV.modelo.pagamento;

import java.math.BigDecimal;

public class Dinheiro implements ProcessadorPagamento {
    private BigDecimal valorRecebido;

    public Dinheiro(BigDecimal valorRecebido) {
        if (valorRecebido == null) {
            throw new IllegalArgumentException("Valor recebido é obrigatório");
        }
        this.valorRecebido = valorRecebido;
    }

    @Override
    public boolean processar(BigDecimal valor) {
        if (valorRecebido.compareTo(valor) < 0) {
            throw new IllegalArgumentException("Valor recebido menor que o valor total do pedido");
        }
        System.out.println("Pagamento em dinheiro recebido. Troco: R$ " + valorRecebido.subtract(valor));
        return true;
    }

    @Override
    public String getComprovante() {
        return "RECIBO-" + System.currentTimeMillis();
    }

    @Override
    public String getDescricao() {
        return "Dinheiro (Valor recebido: R$ " + valorRecebido + ")";
    }
}