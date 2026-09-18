package com.ecommerce.pedidos.RV.modelo.pagamento;

import java.math.BigDecimal;

public class Pix extends FormaPagamento {
    private String chave;

    public Pix(BigDecimal valor, String chave) {
        super(valor);
        setChave(chave);
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        if (chave == null || chave.isBlank()) {
            throw new IllegalArgumentException("Chave Pix é obrigatória");
        }
        this.chave = chave;
    }

    @Override
    public boolean processar() {
        System.out.println("Processando Pix para a chave: " + chave);
        return true;
    }

    @Override
    public String getResumo() {
        return super.getResumo() + " (Chave: " + chave + ")";
    }
}