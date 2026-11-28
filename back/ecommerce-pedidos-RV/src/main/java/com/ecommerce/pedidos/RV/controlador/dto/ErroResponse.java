package com.ecommerce.pedidos.RV.controlador.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Corpo de erro ÚNICO da API (Aula 15, Momento 3, Passo 4):
 * { status, erro, mensagem, caminho, instante, campos? }
 * "campos" só aparece no 400 de validação.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErroResponse(int status, String erro, String mensagem,
                           String caminho, LocalDateTime instante,
                           List<CampoInvalido> campos) {

    // atalho para os erros sem lista de campos (404, 409...)
    public ErroResponse(int status, String erro, String mensagem, String caminho) {
        this(status, erro, mensagem, caminho, LocalDateTime.now(), null);
    }
}
