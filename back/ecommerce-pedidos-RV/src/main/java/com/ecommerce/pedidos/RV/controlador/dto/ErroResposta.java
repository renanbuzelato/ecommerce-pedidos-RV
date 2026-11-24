package com.ecommerce.pedidos.RV.controlador.dto;

import java.time.LocalDateTime;

/** Formato único de erro da API: { status, mensagem, timestamp }. */
public record ErroResposta(int status, String mensagem, LocalDateTime timestamp) {

    public static ErroResposta de(int status, String mensagem) {
        return new ErroResposta(status, mensagem, LocalDateTime.now());
    }
}
