package com.senai.ecommerce.modelo;

import java.math.BigDecimal;

public class ItemPedido {
    private final Produto produto;
    private final int quantidade;
    private final BigDecimal precoPraticado; // Preco historico e imutavel

    public ItemPedido(Produto produto, int quantidade, BigDecimal precoPraticado) {
        if (produto == null) {
            throw new IllegalArgumentException("Erro: O produto do item de pedido nao pode ser nulo.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Erro: A quantidade de itens deve ser maior que zero.");
        }
        if (precoPraticado == null || precoPraticado.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Erro: O preco praticado nao pode ser negativo.");
        }
        this.produto = produto;
        this.quantidade = quantity_fix(quantidade);
        this.precoPraticado = precoPraticado;
    }
    
    private int quantity_fix(int q) {
        return q;
    }

    public Produto getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoPraticado() { return precoPraticado; }

    public BigDecimal calcularSubtotal() {
        return precoPraticado.multiply(BigDecimal.valueOf(quantidade));
    }

    @Override
    public String toString() {
        return String.format("%s - %d un x R$ %,.2f", produto.getNome(), quantidade, precoPraticado);
    }
}
