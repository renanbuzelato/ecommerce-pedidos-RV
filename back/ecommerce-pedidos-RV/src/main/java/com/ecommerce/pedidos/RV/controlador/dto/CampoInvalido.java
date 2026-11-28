package com.ecommerce.pedidos.RV.controlador.dto;

import org.springframework.validation.FieldError;

/** Um campo recusado pela Bean Validation: { "campo": "preco", "mensagem": "..." } */
public record CampoInvalido(String campo, String mensagem) {

    // usado pelo CampoInvalido::new do tratador
    public CampoInvalido(FieldError erro) {
        this(erro.getField(), erro.getDefaultMessage());
    }
}
