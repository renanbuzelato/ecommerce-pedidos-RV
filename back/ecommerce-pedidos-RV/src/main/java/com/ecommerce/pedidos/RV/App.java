package com.ecommerce.pedidos.RV;

import java.math.BigDecimal;
import com.ecommerce.pedidos.RV.modelo.Cliente;
import com.ecommerce.pedidos.RV.modelo.Produto;

public class App {
    public static void main(String[] args) {
        // 1. Teste da classe Cliente e herança de Pessoa
        Cliente cliente = new Cliente("Maria Silva", "123.456.789-00", "maria@email.com");
        System.out.println("--- Teste Cliente ---");
        System.out.println(cliente.getIdentificacao());

        System.out.println("\n--- Teste Produto & Encapsulamento ---");
        
        // 2. Teste de produto válido
        Produto teclado = new Produto("TEC-001", "Teclado", new BigDecimal("150.00"), 8);
        System.out.println("Produto criado: " + teclado.getNome() + " - R$ " + teclado.getPreco());

        // 3. Teste do Passo 2 e 5 do Roteiro: Tentar colocar preço negativo (Deve FALHAR e entrar no catch)
        try {
            teclado.setPreco(new BigDecimal("-10.00"));
            System.out.println("FALHOU: aceitou preço negativo");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: recusou preço negativo -> " + e.getMessage());
        }

        // 4. Teste de estoque negativo
        try {
            teclado.baixarEstoque(500);
            System.out.println("FALHOU: baixou estoque além do disponível");
        } catch (IllegalArgumentException e) {
            System.out.println("OK: recusou estoque insuficiente -> " + e.getMessage());
        }
    }
}