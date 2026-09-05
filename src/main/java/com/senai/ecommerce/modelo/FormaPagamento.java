package com.senai.ecommerce.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa a abstracao basica de uma transacao financeira no sistema de pedidos.
 * Prepara o ecossistema para as classes concretas da Aula 06.
 */
public abstract class FormaPagamento {
    private BigDecimal valor;
    private LocalDateTime dataTransacao;

    public FormaPagamento(BigDecimal valor) {
        setValor(valor);
        this.dataTransacao = LocalDateTime.now();
    }

    public BigDecimal getValor() { return valor; }

    public void setValor(BigDecimal valor) {
        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Erro: O valor do pagamento deve ser maior que zero.");
        }
        this.valor = valor;
    }

    public LocalDateTime getDataTransacao() { return dataTransacao; }

    /**
     * Executa a operacao de processamento financeiro do pagamento.
     */
    public abstract boolean processar();
}
