package com.ecommerce.pedidos.RV.modelo.pagamento;

import java.math.BigDecimal;

public class CartaoCredito extends FormaPagamento implements ProcessadorPagamento {
    private String numeroCartao;
    private int parcelas;

    public CartaoCredito(BigDecimal valor, String numeroCartao, int parcelas) {
        super(valor);
        if (parcelas > 12) {
            throw new IllegalArgumentException("Cartão aceita no máximo 12 parcelas");
        }
        this.numeroCartao = numeroCartao;
        this.parcelas = parcelas;
    }

    @Override
    public boolean processar() {
        return processar(getValor());
    }

    @Override
    public boolean processar(BigDecimal valor) {
        System.out.println("Autorizando cartão " + numeroCartao + " em " + parcelas + "x");
        return true;
    }

    @Override
    public String getComprovante() {
        return "CARD-" + System.currentTimeMillis();
    }

    @Override
    public String getDescricao() {
        return "Cartão de Crédito " + numeroCartao + " (" + parcelas + "x)";
    }
}