package com.ecommerce.pedidos.RV.controlador.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.List;

/** Corpo do POST /api/pedidos: o carrinho da interface. */
public record PedidoEntrada(
        @NotNull(message = "Informe o cliente.") Long clienteId,
        @NotNull(message = "Informe o endereço de entrega.") @Valid EnderecoDTO enderecoEntrega,
        @NotEmpty(message = "Não é possível criar um pedido sem itens.") @Valid List<Item> itens) {

    public record Item(
            @NotNull(message = "Informe o produto.") Long produtoId,
            @Positive(message = "Quantidade deve ser positiva.") int quantidade) { }
}
