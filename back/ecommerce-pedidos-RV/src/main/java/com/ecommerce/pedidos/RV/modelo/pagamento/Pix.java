package com.ecommerce.pedidos.RV.modelo.pagamento;

import java.math.BigDecimal;

public class Pix extends FormaPagamento implements ProcessadorPagamento {
    private String chave;

    public Pix(BigDecimal valor, String chave) {
        super(valor);
        if (chave == null || chave.isBlank()) {
            throw new IllegalArgumentException("Chave Pix é obrigatória");
        }
        this.chave = chave;
    }

    @Override
    public boolean processar() {
        return processar(getValor());
    }

    @Override
    public boolean processar(BigDecimal valor) {
        System.out.println("Enviando cobrança Pix para a chave: " + chave);
        return true;
    }

    @Override
    public String getComprovante() {
        return "PIX-" + System.currentTimeMillis();
    }

    @Override
    public String getDescricao() {
        return "Pix chave: " + chave;
    }
}