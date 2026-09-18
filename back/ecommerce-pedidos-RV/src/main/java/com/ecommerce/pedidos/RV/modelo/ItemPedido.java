package com.ecommerce.modelo;

import java.math.BigDecimal;

public class ItemPedido {
    private final Produto produto;
    private final int quantidade;
    private final BigDecimal precoPraticado; // Preço congelado no momento do pedido

    public ItemPedido(Produto produto, int quantidade, BigDecimal precoPraticado) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto é obrigatório");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoPraticado = precoPraticado;
    }

    public BigDecimal calcularSubtotal() {
        return precoPraticado.multiply(BigDecimal.valueOf(quantidade));
    }

    public Produto getProduto() {
        return produto;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoPraticado() {
        return precoPraticado;
    }
}