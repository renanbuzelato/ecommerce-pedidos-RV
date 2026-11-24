package com.ecommerce.pedidos.RV.controlador.dto;

import com.ecommerce.pedidos.RV.modelo.TipoPagamento;
import com.ecommerce.pedidos.RV.servico.ServicoPagamento.DadosPagamento;
import jakarta.validation.constraints.NotNull;

/** Corpo do POST /api/pagamentos. */
public record PagamentoEntrada(
        @NotNull(message = "Informe o pedido.") Long pedidoId,
        @NotNull(message = "Forma de pagamento inválida.") TipoPagamento forma,
        DadosPagamento dados) { }
