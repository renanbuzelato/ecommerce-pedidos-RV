package com.ecommerce.pedidos.RV.controlador.dto;

import com.ecommerce.pedidos.RV.modelo.TipoPagamento;
import com.ecommerce.pedidos.RV.servico.ServicoPagamento.DadosPagamento;
import jakarta.validation.constraints.NotNull;

/** Corpo do POST /api/pedidos/{id}/pagamento — o pedido vem do endereço, não do corpo. */
public record PagamentoEntrada(
        @NotNull(message = "Forma de pagamento inválida.") TipoPagamento forma,
        DadosPagamento dados) { }
