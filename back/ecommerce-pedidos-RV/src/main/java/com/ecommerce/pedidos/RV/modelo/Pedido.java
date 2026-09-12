package com.ecommerce.modelo;

import java.util.Collections;
import java.util.List;

public class Pedido {
    private List<ItemPedido> itens;

    // Construtor e outros atributos/métodos...

    public List<ItemPedido> getItens() {
        return Collections.unmodifiableList(itens);
    }
}