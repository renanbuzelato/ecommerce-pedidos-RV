package com.ecommerce.pedidos.RV.modelo.pagamento;

import java.math.BigDecimal;

public abstract class FormaPagamento {
    private BigDecimal valor;

    protected FormaPagamento(BigDecimal valor) {
        setValor(valor);
    }

    public void setValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Valor do pagamento deve ser positivo");
        }
        this.valor = valor;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public abstract boolean processar();

    public String getResumo() {
        return String.format("%s no valor de R$ %s", getClass().getSimpleName(), valor);
    }
}