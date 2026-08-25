package com.ecommerce.pedidos.RV.util;

import java.util.Random;

/**
 * Cálculos e formatações de apoio ao módulo de pedidos.
 * Classe utilitária: todos os métodos são estáticos.
 */
public class PedidoUtils {

    private static final double VALOR_POR_QUILO = 7.50;
    private static final double FRETE_MINIMO = 15.00;
    private static final double TAXA_DESCONTO = 0.10;
    private static final double DESCONTO_MAXIMO = 50.00;
    private static final double VALOR_FRETE_GRATIS = 300.00;

    private PedidoUtils() {
        // classe utilitária não deve ser instanciada
    }

    public static String gerarNumeroDoPedido() {
        Random sorteio = new Random();
        int sequencial = sorteio.nextInt(100000); // 0 a 99999
        return String.format("PED-2026-%05d", sequencial);
    }

    public static double calcularSubtotal(double[] precos, int[] quantidades) {
        double subtotal = 0.0;
        for (int i = 0; i < precos.length; i++) {
            subtotal += precos[i] * quantidades[i];
        }
        return subtotal;
    }

    public static double calcularFrete(double pesoEmQuilos, double subtotal) {
        if (subtotal >= VALOR_FRETE_GRATIS) {
            return 0.0;
        }
        double quilosCobrados = Math.ceil(pesoEmQuilos);
        double freteCalculado = quilosCobrados * VALOR_POR_QUILO;
        return Math.max(freteCalculado, FRETE_MINIMO);
    }

    public static double calcularDesconto(double subtotal) {
        double desconto = subtotal * TAXA_DESCONTO;
        return Math.min(desconto, DESCONTO_MAXIMO);
    }

    public static String formatarLinhaDoRecibo(String nome, double preco, int quantidade) {
        return String.format("%-20s R$ %8.2f x%3d", nome, preco, quantidade);
    }
}