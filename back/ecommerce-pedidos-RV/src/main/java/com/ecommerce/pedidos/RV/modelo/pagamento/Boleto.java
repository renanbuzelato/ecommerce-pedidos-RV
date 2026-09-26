package com.ecommerce.pedidos.RV.modelo.pagamento;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Boleto extends FormaPagamento implements ProcessadorPagamento {
    private LocalDate dataVencimento;

    public Boleto(BigDecimal valor, LocalDate dataVencimento) {
        super(valor);
        if (dataVencimento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Vencimento não pode ser no passado");
        }
        this.dataVencimento = dataVencimento;
    }

    @Override
    public boolean processar() {
        return processar(getValor());
    }

    @Override
    public boolean processar(BigDecimal valor) {
        System.out.println("Boleto gerado com vencimento para: " + dataVencimento);
        return false;
    }

    @Override
    public String getComprovante() {
        return "BOL-" + System.currentTimeMillis();
    }

    @Override
    public String getDescricao() {
        return "Boleto bancário com vencimento em " + dataVencimento;
    }
}