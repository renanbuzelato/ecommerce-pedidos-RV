package com.ecommerce.pedidos.RV.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {
    private final String numero;
    private final Cliente cliente;
    private final List<ItemPedido> itens = new ArrayList<>();
    private String formaPagamento;

    public Pedido(String numero, Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Pedido exige um cliente");
        }
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Número do pedido é obrigatório");
        }
        this.numero = numero;
        this.cliente = cliente;
    }

    public void adicionarItem(Produto produto, int quantidade) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto é obrigatório");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.itens.add(new ItemPedido(produto, quantidade, produto.getPreco()));
    }

    public void pagarCom(String formaPagamento) {
        if (itens.isEmpty()) {
            throw new IllegalStateException("Pedido sem itens não pode ser pago");
        }
        this.formaPagamento = formaPagamento;
    }

    public BigDecimal calcularValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            total = total.add(item.calcularSubtotal());
        }
        return total;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public String getNumero() {
        return numero;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }
}