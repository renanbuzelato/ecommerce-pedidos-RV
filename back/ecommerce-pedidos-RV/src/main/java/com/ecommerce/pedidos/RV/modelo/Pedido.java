package com.ecommerce.pedidos.RV.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.ecommerce.pedidos.RV.modelo.pagamento.FormaPagamento;

public class Pedido {
    private final String numero;
    private final Cliente cliente; // Associação 1 para 1
    private final List<ItemPedido> itens = new ArrayList<>(); // Composição
    private FormaPagamento formaPagamento; // Associação 0..1 (opcional)

    public Pedido(String numero, Cliente cliente) {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Número do pedido é obrigatório");
        }
        if (cliente == null) {
            throw new IllegalArgumentException("Pedido exige um cliente"); // Validação 1 para 1
        }
        this.numero = numero;
        this.cliente = cliente;
    }

    // Composição: O próprio Pedido cria e adiciona o ItemPedido
    public void adicionarItem(Produto produto, int quantidade) {
        if (produto == null) {
            throw new IllegalArgumentException("Produto é obrigatório");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        
        // Exemplo simples de validação de quantidade
        itens.add(new ItemPedido(produto, quantidade, produto.getPreco()));
    }

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens); // Proteção da lista
    }

    public void pagarCom(FormaPagamento formaPagamento) {
        if (itens.isEmpty()) {
            throw new IllegalStateException("Pedido sem itens não pode ser pago"); // Validação 1..*
        }
        if (formaPagamento == null) {
            throw new IllegalArgumentException("Forma de pagamento não pode ser nula");
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

    public String getNumero() {
        return numero;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }
}