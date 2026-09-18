package com.ecommerce.pedidos.RV;

import java.math.BigDecimal;
import com.ecommerce.pedidos.RV.modelo.Cliente;
import com.ecommerce.pedidos.RV.modelo.Funcionario;
import com.ecommerce.pedidos.RV.modelo.Produto;
import com.ecommerce.pedidos.RV.modelo.pagamento.FormaPagamento;
import com.ecommerce.pedidos.RV.modelo.pagamento.Pix;

public class App {
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  AULA 06 - TESTES DE HERANÇA E DOMÍNIO   ");
        System.out.println("==========================================\n");

        // 1. TESTE DA HIERARQUIA DE PESSOA (Cliente e Funcionario)
        System.out.println("--- 1. Testando Pessoas (Cliente e Funcionário) ---");
        
        Cliente cliente = new Cliente("Maria Silva", "123.456.789-00", "maria@email.com");
        System.out.println("Cliente Identificação : " + cliente.getIdentificacao());
        System.out.println("Cliente Resumo        : " + cliente.getResumo());

        Funcionario funcionario = new Funcionario("Carlos Souza", "987.654.321-11", "F-2024", "Analista de Vendas");
        System.out.println("Funcionário Identif.  : " + funcionario.getIdentificacao());
        System.out.println("Funcionário Resumo    : " + funcionario.getResumo());

        System.out.println("\n------------------------------------------\n");

        // 2. TESTE DE VALIDAÇÃO E ENCAPSULAMENTO DE PRODUTO
        System.out.println("--- 2. Testando Validações em Produto ---");
        
        Produto teclado = new Produto("TEC-001", "Teclado Mecânico", new BigDecimal("150.00"), 8);
        System.out.println("Produto criado: " + teclado.getNome() + " | R$ " + teclado.getPreco());

        // Teste de exceção para preço negativo
        try {
            teclado.setPreco(new BigDecimal("-20.00"));
        } catch (IllegalArgumentException e) {
            System.out.println(" OK (Capturado): " + e.getMessage());
        }

        System.out.println("\n------------------------------------------\n");

        // 3. TESTE DA HIERARQUIA DE FORMAS DE PAGAMENTO
        System.out.println("--- 3. Testando Formas de Pagamento (Polimorfismo) ---");

        // Vetor utilizando o tipo genérico da classe mãe FormaPagamento
        FormaPagamento[] pagamentos = {
            new Pix(new BigDecimal("150.00"), "cliente@email.com")
        };

        for (FormaPagamento pagamento : pagamentos) {
            System.out.println("Resumo   : " + pagamento.getResumo());
            System.out.print("Execução : ");
            pagamento.processar();
            System.out.println();
        }

        // Teste de validação para chave Pix em branco
        try {
            new Pix(new BigDecimal("50.00"), "");
        } catch (IllegalArgumentException e) {
            System.out.println(" OK (Capturado Pix inválido): " + e.getMessage());
        }

        System.out.println("\n==========================================");
    }
}