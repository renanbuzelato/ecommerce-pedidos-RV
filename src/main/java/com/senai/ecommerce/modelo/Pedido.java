package com.senai.ecommerce.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pedido {
    private final String numero; // Imutavel
    private final Cliente cliente; // Imutavel
    private final List<ItemPedido> itens = new ArrayList<>();
    private String situacao;

    public Pedido(String numero, Cliente cliente) {
        if (numero == null || numero.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O numero do pedido nao pode ser nulo ou vazio.");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("Erro: O cliente associado ao pedido nao pode ser nulo.");
        }
        this.numero = numero.trim();
        this.cliente = cliente;
        this.situacao = "Aberto";
    }

    public String getNumero() { return numero; }
    public Cliente getCliente() { return cliente; }
    
    /**
     * Retorna uma visualizacao somente leitura da lista de itens, impedindo
     * vazamentos de encapsulamento e modificacoes por fora da classe Pedido.
     */
    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }

    public String getSituacao() { return situacao; }
    
    public void setSituacao(String situacao) {
        if (situacao == null || situacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: A situacao do pedido nao pode ser nula ou vazia.");
        }
        this.situacao = situacao.trim();
    }

    public void adicionarItem(ItemPedido item) {
        if (item == null) {
            throw new IllegalArgumentException("Erro: Item de pedido nao pode ser nulo.");
        }
        itens.add(item);
    }

    public BigDecimal calcularValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            total = total.add(item.calcularSubtotal());
        }
        return total;
    }

    @Override
    public String toString() {
        return String.format("Pedido %s - Cliente: %s - %d itens - Total: R$ %,.2f", 
                numero, cliente.getNome(), itens.size(), calcularValorTotal());
    }
}
