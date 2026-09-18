package com.ecommerce.pedidos.RV.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {
    private final String numero;
    private final Cliente cliente; // Associação 1 para 1 (Obrigatória)
    private final List<ItemPedido> itens = new ArrayList<>(); // Composição
    private String formaPagamento; // Associação 0..1 (Opcional - evoluirá na Aula 08)

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

    // O próprio pedido cria e gerencia o ItemPedido (Composição)
    public void adicionarItem(Produto produto, int quantidade) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto é obrigatório");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        // Exemplo de regra de negócio para checar estoque (se houver o método na classe Produto)
        // if (!produto.temEstoqueDisponivel(quantidade)) { ... }

        itens.add(new ItemPedido(produto, quantidade, produto.getPreco()));
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
            total = total.add(item.calcularSubtotal()); // Importante: reatribuir 'total' em BigDecimal
        }
        return total;
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens); // Retorna lista imutável
    }

    public String getNumero() { return numero; }
    public Cliente getCliente() { return cliente; }
    public String getFormaPagamento() { return formaPagamento; }
}