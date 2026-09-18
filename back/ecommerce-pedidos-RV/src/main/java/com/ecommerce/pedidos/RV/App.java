package com.ecommerce.pedidos.RV;

import com.ecommerce.pedidos.RV.modelo.Cliente;
import com.ecommerce.pedidos.RV.modelo.Pedido;
import com.ecommerce.pedidos.RV.modelo.Produto;

import java.math.BigDecimal;

public class App {
    public static void main(String[] args) {
        System.out.println("=== TESTES DE INTEGRIDADE DO DOMÍNIO ===");

        // Instâncias base
        Cliente cliente = new Cliente("123", "João Silva", "joao@email.com");
        Produto produto = new Produto("1", "Notebook", new BigDecimal("3500.00"), 10);

        // 1. Pedido sem cliente deve falhar
        try {
            new Pedido("PED-001", null);
        } catch (IllegalArgumentException e) {
            System.out.println(" Sucesso: " + e.getMessage());
        }

        // 2. Tentar pagar pedido sem itens
        Pedido pedido = new Pedido("PED-001", cliente);
        try {
            pedido.pagarCom("PIX");
        } catch (IllegalStateException e) {
            System.out.println(" Sucesso: " + e.getMessage());
        }

        // 3. Adicionar item com quantidade negativa
        try {
            pedido.adicionarItem(produto, -1);
        } catch (IllegalArgumentException e) {
            System.out.println(" Sucesso: " + e.getMessage());
        }

        // 4. Adicionar item válido e tentar modificar lista imutável externa
        pedido.adicionarItem(produto, 2);
        try {
            pedido.getItens().clear();
        } catch (UnsupportedOperationException e) {
            System.out.println(" Sucesso: Proteção da lista de itens funcionando!");
        }

        // 5. Cálculo do Total
        System.out.println("Total do pedido: R$ " + pedido.calcularValorTotal());
    }
}