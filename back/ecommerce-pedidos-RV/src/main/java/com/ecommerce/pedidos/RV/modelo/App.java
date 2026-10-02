package com.ecommerce.pedidos.RV.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.ecommerce.pedidos.RV.modelo.pagamento.*;

public class App {
    public static void main(String[] args) {
        // Instancia Cliente e Pedido
        Cliente cliente = new Cliente("Carlos", "12345678900", "carlos@email.com");
        Pedido pedido = new Pedido(cliente);
        
        // Instancia o produto utilizando o novo construtor conveniente
        Produto produto1 = new Produto("Notebook", new BigDecimal("3500.00"));
        pedido.adicionarItem(produto1);

        List<ProcessadorPagamento> formas = List.of(
            new Pix(new BigDecimal("3500.00"), "carlos@email.com"),
            new Boleto(new BigDecimal("3500.00"), LocalDate.now().plusDays(3)),
            new CartaoCredito(new BigDecimal("3500.00"), "**** 5678", 3),
            new Dinheiro(new BigDecimal("4000.00"))
        );

        System.out.println("=== EXECUÇÃO POLIMÓRFICA DO PROCESSAMENTO ===");
        for (ProcessadorPagamento forma : formas) {
            System.out.println("\n--- " + forma.getDescricao());
            boolean ok = forma.processar(new BigDecimal("3500.00"));
            System.out.println(ok ? "Aprovado! Comprovante: " + forma.getComprovante() : "Aguardando compensação");
        }
    }
}