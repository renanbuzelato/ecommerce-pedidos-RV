package com.ecommerce.pedidos.RV.modelo;

import java.math.BigDecimal;

public class ItemPedido {
    private final Produto produto;
    private final int quantidade;
    private final BigDecimal precoPraticado;

    public ItemPedido(Produto produto, int quantidade, BigDecimal precoPraticado) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto é obrigatório");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (precoPraticado == null || precoPraticado.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preço praticado deve ser positivo");
        }
        this.produto = produto;
        this.quantidade = quantidade;
        this.precoPraticado = precoPraticado;
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

    public BigDecimal calcularSubtotal() {
        return precoPraticado.multiply(BigDecimal.valueOf(quantidade));
    }
}