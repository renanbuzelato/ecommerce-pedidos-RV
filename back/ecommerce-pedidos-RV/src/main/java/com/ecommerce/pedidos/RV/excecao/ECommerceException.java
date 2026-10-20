package com.ecommerce.pedidos.RV.excecao;

public class ECommerceException extends Exception {
    public ECommerceException(String mensagem) {
        super(mensagem);
    }

    public ECommerceException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}