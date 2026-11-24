package com.ecommerce.pedidos.RV.controlador.dto;

import com.ecommerce.pedidos.RV.modelo.Endereco;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

/** Corpo do POST/PUT /api/clientes. "documento" segue o nome do atributo em Pessoa (coluna cpf). */
public record ClienteEntrada(
        @NotBlank(message = "Nome é obrigatório") @Size(max = 120) String nome,
        @NotBlank(message = "CPF é obrigatório") @Size(max = 14) String documento,
        @NotBlank(message = "E-mail é obrigatório") @Email(message = "E-mail inválido") @Size(max = 120) String email,
        @Size(max = 20) String telefone,
        @Valid List<EnderecoDTO> enderecos) {

    public List<Endereco> enderecosComoEntidades() {
        return enderecos == null ? List.of() : enderecos.stream().map(EnderecoDTO::paraEndereco).toList();
    }
}
