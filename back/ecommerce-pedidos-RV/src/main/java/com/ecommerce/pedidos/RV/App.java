package com.ecommerce.pedidos.RV;

import com.ecommerce.pedidos.RV.util.PedidoUtils;

/**
 * Classe de teste manual dos métodos de PedidoUtils.
 */
public class App {
    public static void main(String[] args) {
        String[] produtos = {"Teclado", "Monitor", "Mouse"};
        double[] precos = {150.00, 899.90, 79.50};
        int[] quantidades = {1, 2, 3};

        double subtotal = PedidoUtils.calcularSubtotal(precos, quantidades);

        System.out.println("Pedido: " + PedidoUtils.gerarNumeroDoPedido());
        System.out.println("Subtotal: R$ " + subtotal);
        System.out.println("Frete: R$ " + PedidoUtils.calcularFrete(4.2, subtotal));
        System.out.println("Desconto: R$ " + PedidoUtils.calcularDesconto(subtotal));

        System.out.println();
        System.out.println("--- Linha de recibo ---");
        for (int i = 0; i < produtos.length; i++) {
            System.out.println(PedidoUtils.formatarLinhaDoRecibo(produtos[i], precos[i], quantidades[i]));
        }
    }
}