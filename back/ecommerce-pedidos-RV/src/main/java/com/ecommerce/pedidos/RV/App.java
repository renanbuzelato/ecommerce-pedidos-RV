package com.ecommerce.pedidos.RV;

import java.math.BigDecimal;
import com.ecommerce.pedidos.RV.modelo.Cliente;
import com.ecommerce.pedidos.RV.modelo.Pedido;
import com.ecommerce.pedidos.RV.modelo.Produto;
import com.ecommerce.pedidos.RV.modelo.pagamento.FormaPagamento;
import com.ecommerce.pedidos.RV.modelo.pagamento.Pix;

public class App {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  AULA 07 - TESTES DE RELACIONAMENTOS     ");
        System.out.println("==========================================\n");

        // 1. Criar entidades base
        Cliente cliente = new Cliente("Maria Silva", "123.456.789-00", "maria@email.com");
        Produto produto1 = new Produto("PRD-001", "Teclado Mecânico", new BigDecimal("150.00"), 10);
        Produto produto2 = new Produto("PRD-002", "Mouse Óptico", new BigDecimal("80.00"), 5);

        // 2. Testar criação de Pedido válido
        Pedido pedido = new Pedido("PED-1001", cliente);
        pedido.adicionarItem(produto1, 2);
        pedido.adicionarItem(produto2, 1);

        System.out.println("Pedido criado para: " + pedido.getCliente().getNome());
        System.out.println("Total de itens: " + pedido.getItens().size());
        System.out.println("Valor Total do Pedido: R$ " + pedido.calcularValorTotal());

        // 3. Testar Pagamento
        FormaPagamento pix = new Pix(pedido.calcularValorTotal(), "maria@email.com");
        pedido.pagarCom(pix);
        System.out.println("Forma de Pagamento: " + pedido.getFormaPagamento().getResumo());

        System.out.println("\n--- TESTES DE INTEGRIDADE (EXCEÇÕES ESPERADAS) ---\n");

        // Teste 1: Pedido sem cliente
        try {
            new Pedido("PED-1002", null);
        } catch (IllegalArgumentException e) {
            System.out.println("[OK] Pedido sem cliente: " + e.getMessage());
        }

        // Teste 2: Item com produto null
        try {
            pedido.adicionarItem(null, 1);
        } catch (IllegalArgumentException e) {
            System.out.println("[OK] Item com produto null: " + e.getMessage());
        }

        // Teste 3: Item com quantidade zero
        try {
            pedido.adicionarItem(produto1, 0);
        } catch (IllegalArgumentException e) {
            System.out.println("[OK] Quantidade zero/negativa: " + e.getMessage());
        }

        // Teste 4: Pagar pedido sem itens
        try {
            Pedido pedidoVazio = new Pedido("PED-1003", cliente);
            pedidoVazio.pagarCom(pix);
        } catch (IllegalStateException e) {
            System.out.println("[OK] Pagar pedido vazio: " + e.getMessage());
        }

        // Teste 5: Tentar modificar a lista imutável diretamente
        try {
            pedido.getItens().clear();
        } catch (UnsupportedOperationException e) {
            System.out.println("[OK] getItens().clear() bloqueado com sucesso!");
        }

        System.out.println("\n==========================================");
    }
}