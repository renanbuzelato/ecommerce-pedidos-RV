package com.ecommerce.pedidos.RV.modelo;

import com.ecommerce.pedidos.RV.excecao.EstoqueInsuficienteException;
import com.ecommerce.pedidos.RV.excecao.PagamentoRecusadoException;
import com.ecommerce.pedidos.RV.excecao.PedidoInvalidoException;
import com.ecommerce.pedidos.RV.modelo.pagamento.ProcessadorPagamento;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private List<ItemPedido> itens = new ArrayList<>();
    private boolean pago = false;

    public void adicionarItem(Produto produto, int quantidade) throws EstoqueInsuficienteException {
        if (produto == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva.");
        }
        if (pago) {
            throw new IllegalStateException("Não é possível alterar um pedido já pago.");
        }

        // Tenta baixar do estoque
        produto.baixarEstoque(quantidade);
        itens.add(new ItemPedido(produto, quantidade));
    }

    public double getValorTotal() {
        double total = 0.0;
        for (ItemPedido item : itens) {
            total += item.getPrecoTotal();
        }
        return total;
    }

    public void pagar(ProcessadorPagamento processador) throws PedidoInvalidoException, PagamentoRecusadoException {
        if (processador == null) {
            throw new IllegalArgumentException("Processador de pagamento não pode ser nulo.");
        }
        if (pago) {
            throw new PedidoInvalidoException("Este pedido já foi pago anteriormente.");
        }
        if (itens.isEmpty()) {
            throw new PedidoInvalidoException("Não é possível pagar um pedido sem itens.");
        }

        // Converte o valor total para BigDecimal para ser aceito pela interface ProcessadorPagamento
        boolean aprovado = processador.processar(BigDecimal.valueOf(getValorTotal()));
        if (!aprovado) {
            throw new PagamentoRecusadoException(processador.getClass().getSimpleName(), "Saldo/Transação recusada pela operadora.");
        }

        this.pago = true;
    }

    public boolean isPago() {
        return pago;
    }
}