package com.ecommerce.pedidos.RV.controlador.dto;

import com.ecommerce.pedidos.RV.modelo.SituacaoPedido;
import jakarta.validation.constraints.NotNull;

/** Corpo do PATCH /api/pedidos/{id}: { "situacao": "ENVIADO" } */
public record SituacaoEntrada(@NotNull(message = "Informe a nova situação.") SituacaoPedido situacao) { }
