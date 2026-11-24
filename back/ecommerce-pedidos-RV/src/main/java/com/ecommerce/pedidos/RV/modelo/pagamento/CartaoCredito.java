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
        System.out.println("Autorizando cartão final " + getFinal() + " em " + parcelas + "x");
        // Simulação da operadora (Aula 15): cartões terminados em 0000 são recusados,
        // para a interface conseguir demonstrar o fluxo de pagamento recusado.
        return numeroCartao == null || !numeroCartao.endsWith("0000");
    }

    @Override
    public String getComprovante() {
        return "CARD-" + System.currentTimeMillis();
    }

    @Override
    public String getDescricao() {
        // Nunca expõe o número completo: só os 4 últimos dígitos
        return "Cartão final " + getFinal() + " (" + parcelas + "x)";
    }

    private String getFinal() {
        return numeroCartao == null || numeroCartao.length() < 4
                ? "????" : numeroCartao.substring(numeroCartao.length() - 4);
    }
}