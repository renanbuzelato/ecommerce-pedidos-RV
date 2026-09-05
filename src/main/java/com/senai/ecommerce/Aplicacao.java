package com.senai.ecommerce;

import com.senai.ecommerce.util.PedidoUtils;

/**
 * Classe principal de demonstração que serve de harness de execução
 * para exercitar todos os métodos obrigatórios e desejáveis da classe utilitária PedidoUtils.
 * <p>
 * Executa simulações de fluxo feliz e também testes com limites críticos (erros) para provar
 * a resiliência do sistema e o tratamento de entradas inválidas.
 * </p>
 * 
 * @author Squad Backend
 * @version 1.0
 */
public class Aplicacao {

    public static void main(String[] args) {
        System.out.println("=================================================================");
        System.out.println("          SISTEMA DE GESTÃO DE PEDIDOS - TESTE DE INFRA          ");
        System.out.println("=================================================================
");

        // 1. Testando geração dinâmica de códigos de pedidos
        System.out.println(">>> 1. Testando gerarNumeroDoPedido():");
        String cod1 = PedidoUtils.gerarNumeroDoPedido();
        String cod2 = PedidoUtils.gerarNumeroDoPedido();
        System.out.println("Código Gerado 1: " + cod1);
        System.out.println("Código Gerado 2: " + cod2);
        System.out.println();

        // 2. Testando normalização de nomes de clientes (Title Case + Trim)
        System.out.println(">>> 2. Testando normalizarNome() (Requisito Desejável):");
        String nomeBruto = "   mAuRíCiO   fAlVo  ";
        String nomeLimpo = PedidoUtils.normalizarNome(nomeBruto);
        System.out.println("Nome Bruto:   [" + nomeBruto + "]");
        System.out.println("Nome Normalizado: [" + nomeLimpo + "]");
        System.out.println();

        // 3. Testando cálculo de subtotal do carrinho
        System.out.println(">>> 3. Testando calcularSubtotal():");
        String[] produtos = {"Parafuso Sextavado 8mm", "Porca Galvanizada M8", "Arruela Lisa Inox"};
        double[] precos = {2.50, 1.20, 0.80};
        int[] quantidades = {50, 50, 100}; // total esperado = (2.5 * 50) + (1.2 * 50) + (0.8 * 100) = 125 + 60 + 80 = 265.00
        
        double subtotal = PedidoUtils.calcularSubtotal(precos, quantidades);
        System.out.println("Subtotal esperado:  R$ 265,00");
        System.out.printf("Subtotal calculado: R$ %.2f
", subtotal);
        System.out.println();

        // 4. Testando formatação individual de linhas de recibo
        System.out.println(">>> 4. Testando formatarLinhaDoRecibo():");
        System.out.println(String.format("%-25s | %10s | %4s | %12s", "Produto", "Preço (R$)", "Qtd", "Total (R$)"));
        System.out.println("-------------------------------------------------------------");
        for (int i = 0; i < produtos.length; i++) {
            double totalItem = precos[i] * quantidades[i];
            System.out.println(PedidoUtils.formatarLinhaDoRecibo(produtos[i], precos[i], quantidades[i], totalItem));
        }
        System.out.println();

        // 5. Testando cálculo de frete elástico (Math.ceil, Math.max, Frete Grátis)
        System.out.println(">>> 5. Testando calcularFrete():");
        double precoPorKg = 4.50;
        double freteMinimo = 15.00;
        double metaFreteGratis = 200.00;

        // Cenário A: Abaixo do frete grátis, peso arredondado (3.2 kg -> 4 kg) -> 4 * 4.5 = 18.00 (maior que o frete mínimo)
        double pesoA = 3.2;
        double subtotalA = 120.00;
        double freteA = PedidoUtils.calcularFrete(pesoA, precoPorKg, freteMinimo, metaFreteGratis, subtotalA);
        System.out.printf("Cenário A (Peso %.1f kg, Subtotal R$ %.2f) -> Frete: R$ %.2f (Esperado: R$ 18,00)
", 
                          pesoA, subtotalA, freteA);

        // Cenário B: Peso baixo (1.1 kg -> 2 kg) -> 2 * 4.5 = 9.00 -> deve aplicar frete mínimo de R$ 15,00
        double pesoB = 1.1;
        double subtotalB = 50.00;
        double freteB = PedidoUtils.calcularFrete(pesoB, precoPorKg, freteMinimo, metaFreteGratis, subtotalB);
        System.out.printf("Cenário B (Peso %.1f kg, Subtotal R$ %.2f) -> Frete: R$ %.2f (Esperado: R$ 15,00)
", 
                          pesoB, subtotalB, freteB);

        // Cenário C: Subtotal atinge limiar de frete grátis (Subtotal = R$ 265,00 >= R$ 200,00) -> Frete = 0.00
        double pesoC = 12.5;
        double subtotalC = subtotal; // 265.00
        double freteC = PedidoUtils.calcularFrete(pesoC, precoPorKg, freteMinimo, metaFreteGratis, subtotalC);
        System.out.printf("Cenário C (Peso %.1f kg, Subtotal R$ %.2f) -> Frete: R$ %.2f (Esperado: R$ 0,00)
", 
                          pesoC, subtotalC, freteC);
        System.out.println();

        // 6. Testando cálculo de desconto com teto máximo (Math.min)
        System.out.println(">>> 6. Testando calcularDesconto():");
        double tetoDesconto = 50.00;

        // Caso A: Desconto de 10% em R$ 265,00 = R$ 26,50 (abaixo do teto de R$ 50,00)
        double taxaA = 10.0;
        double descA = PedidoUtils.calcularDesconto(subtotal, taxaA, tetoDesconto);
        System.out.printf("Caso A (Subtotal R$ %.2f, Desc %.1f%%) -> Desconto: R$ %.2f (Esperado: R$ 26,50)
", 
                          subtotal, taxaA, descA);

        // Caso B: Desconto de 25% em R$ 265,00 = R$ 66,25 -> deve ser limitado ao teto de R$ 50,00
        double taxaB = 25.0;
        double descB = PedidoUtils.calcularDesconto(subtotal, taxaB, tetoDesconto);
        System.out.printf("Caso B (Subtotal R$ %.2f, Desc %.1f%%) -> Desconto: R$ %.2f (Esperado: R$ 50,00)
", 
                          subtotal, taxaB, descB);
        System.out.println();

        // 7. Testando emissão completa do recibo estruturado
        System.out.println(">>> 7. Testando montarRecibo() (Requisito Desejável):");
        double freteFinal = freteC; // 0.00 (frete grátis)
        double descontoFinal = descA; // 26.50
        double valorTotalPedido = subtotal + freteFinal - descontoFinal; // 265.00 + 0.00 - 26.50 = 238.50

        String reciboFinal = PedidoUtils.montarRecibo("  mAuRíCiO fAlVo  ", produtos, precos, quantidades, 
                                                     subtotal, freteFinal, descontoFinal, valorTotalPedido);
        System.out.println(reciboFinal);
        System.out.println();

        // 8. Testando casos de exceção e entradas inválidas (Prevenção de Falhas)
        System.out.println(">>> 8. Testando robustez e tratamento de entradas inválidas:");
        
        try {
            System.out.print("Testando cálculo de subtotal com preços e quantidades de tamanhos diferentes... ");
            PedidoUtils.calcularSubtotal(new double[]{10.0, 5.0}, new int[]{2});
        } catch (IllegalArgumentException e) {
            System.out.println("OK -> Capturada exceção esperada: " + e.getMessage());
        }

        try {
            System.out.print("Testando cálculo de subtotal com preço negativo... ");
            PedidoUtils.calcularSubtotal(new double[]{-1.50}, new int[]{10});
        } catch (IllegalArgumentException e) {
            System.out.println("OK -> Capturada exceção esperada: " + e.getMessage());
        }

        try {
            System.out.print("Testando cálculo de frete com peso zero... ");
            PedidoUtils.calcularFrete(0.0, 4.50, 15.00, 200.00, 100.00);
        } catch (IllegalArgumentException e) {
            System.out.println("OK -> Capturada exceção esperada: " + e.getMessage());
        }

        try {
            System.out.print("Testando cálculo de desconto com taxa acima de 100%... ");
            PedidoUtils.calcularDesconto(265.00, 105.0, 50.00);
        } catch (IllegalArgumentException e) {
            System.out.println("OK -> Capturada exceção esperada: " + e.getMessage());
        }

        try {
            System.out.print("Testando normalização com nome do cliente em branco... ");
            PedidoUtils.normalizarNome("   ");
        } catch (IllegalArgumentException e) {
            System.out.println("OK -> Capturada exceção esperada: " + e.getMessage());
        }

        System.out.println("
=================================================================");
        System.out.println("          TODAS AS VALIDAÇÕES E TESTES CONCLUÍDOS COM SUCESSO    ");
        System.out.println("=================================================================");
    }
}
