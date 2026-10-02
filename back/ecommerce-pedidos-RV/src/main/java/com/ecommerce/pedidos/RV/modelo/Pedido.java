package com.ecommerce.pedidos.RV.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.ecommerce.pedidos.RV.modelo.pagamento.ProcessadorPagamento;

public class Pedido {
    private Cliente cliente;
    private List<ItemPedido> itens = new ArrayList<>();
    private String situacao = "ABERTO";
    private String comprovante;

    public Pedido(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente é obrigatório");
        }
        this.cliente = cliente;
    }

    public void adicionarItem(Produto produto, int quantidade) {
        if (produto == null || quantidade <= 0) {
            throw new IllegalArgumentException("Produto e quantidade válida são obrigatórios");
        }
        // Passa Produto, quantidade e o preço unitário extraído do produto
        BigDecimal preco = (produto.getPreco() != null) ? produto.getPreco() : BigDecimal.ZERO;
        itens.add(new ItemPedido(produto, quantidade, preco));
    }

    public void adicionarItem(Produto produto) {
        adicionarItem(produto, 1);
    }

    public BigDecimal calcularValorTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemPedido item : itens) {
            BigDecimal preco = (item.getProduto() != null && item.getProduto().getPreco() != null) 
                    ? item.getProduto().getPreco() 
                    : BigDecimal.ZERO;
            total = total.add(preco.multiply(new BigDecimal(item.getQuantidade())));
        }
        return total;
    }

    public boolean pagar(ProcessadorPagamento processador) {
        if (processador == null) {
            throw new IllegalArgumentException("Forma de pagamento é obrigatória");
        }
        if (itens.isEmpty()) {
            throw new IllegalStateException("Pedido sem itens não pode ser pago");
        }

        boolean aprovado = processador.processar(calcularValorTotal());
        if (aprovado) {
            this.situacao = "PAGO";
            this.comprovante = processador.getComprovante();
        }
        return aprovado;
    }

    public String getSituacao() {
        return situacao;
    }

    public String getComprovante() {
        return comprovante;
    }
}