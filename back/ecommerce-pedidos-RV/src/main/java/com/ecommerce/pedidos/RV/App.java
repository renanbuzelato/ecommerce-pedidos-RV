package com.ecommerce.pedidos.RV;

import com.ecommerce.pedidos.RV.modelo.Cliente;

public class App {
    public static void main(String[] args) {
        Cliente cliente = new Cliente("Maria Silva", "123.456.789-00", "maria@email.com");

        System.out.println(cliente);
        System.out.println(cliente.getIdentificacao());
    }
}