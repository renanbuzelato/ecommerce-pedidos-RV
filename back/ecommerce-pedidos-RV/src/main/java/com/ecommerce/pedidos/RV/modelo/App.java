package com.ecommerce.pedidos.RV.modelo;

import com.ecommerce.pedidos.RV.excecao.ECommerceException;
import com.ecommerce.pedidos.RV.excecao.EstoqueInsuficienteException;
import com.ecommerce.pedidos.RV.excecao.PagamentoRecusadoException;
import com.ecommerce.pedidos.RV.excecao.PedidoInvalidoException;
import com.ecommerce.pedidos.RV.modelo.pagamento.CartaoCredito;

public class App {
    public static void main(String[] args) {
        Produto notebook = new Produto("Notebook", 3500.00, 3);
        Pedido pedido = new Pedido();

        // Cenário 1: Tentar adicionar mais do que o estoque disponível (50 un)
        try {
            pedido.adicionarItem(notebook, 50);
            System.out.println("Item adicionado com sucesso.");
        } catch (EstoqueInsuficienteException e) {
            System.out.println("❌ Erro de negócio: " + e.getMessage());
            System.out.println("💡 Disponível no estoque: " + e.getProduto().getQuantidadeEmEstoque());
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Dado inválido: " + e.getMessage());
        }

        // Cenário 2: Adicionar quantidade válida e pagar com processador nulo
        try {
            pedido.adicionarItem(notebook, 2);
            System.out.println("✅ Item adicionado! Total: R$ " + pedido.getValorTotal());

            // Tentando pagar com null
            pedido.pagar(null);
        } catch (ECommerceException e) {
            System.out.println("❌ Erro de Negócio: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("⚠️ Parâmetro inválido capturado: " + e.getMessage());
        }
    }
}