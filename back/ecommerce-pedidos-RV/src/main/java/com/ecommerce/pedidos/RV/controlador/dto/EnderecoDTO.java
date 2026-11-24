package com.ecommerce.pedidos.RV.controlador.dto;

import com.ecommerce.pedidos.RV.modelo.Endereco;
import com.ecommerce.pedidos.RV.modelo.EnderecoEntrega;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/** Endereço usado tanto no cliente (entrada e saída) quanto na entrega do pedido. */
public record EnderecoDTO(
        Long id,
        @Size(max = 40) String apelido,
        @NotBlank(message = "Informe o CEP.") @Pattern(regexp = "\\d{5}-?\\d{3}", message = "CEP deve ter 8 dígitos.") String cep,
        @NotBlank(message = "Informe o logradouro.") @Size(max = 120) String logradouro,
        @NotBlank(message = "Informe o número.") @Size(max = 20) String numero,
        @Size(max = 60) String complemento,
        @NotBlank(message = "Informe o bairro.") @Size(max = 60) String bairro,
        @NotBlank(message = "Informe a cidade.") @Size(max = 60) String cidade,
        @NotBlank(message = "Informe a UF.") @Size(min = 2, max = 2, message = "UF deve ter 2 letras.") String uf) {

    public static EnderecoDTO de(Endereco e) {
        return new EnderecoDTO(e.getId(), e.getApelido(), e.getCep(), e.getLogradouro(), e.getNumero(),
                e.getComplemento(), e.getBairro(), e.getCidade(), e.getUf());
    }

    public static EnderecoDTO de(EnderecoEntrega e) {
        if (e == null) return null;
        return new EnderecoDTO(null, null, e.getCep(), e.getLogradouro(), e.getNumero(),
                e.getComplemento(), e.getBairro(), e.getCidade(), e.getUf());
    }

    public Endereco paraEndereco() {
        return new Endereco(apelido, cep, logradouro, numero, complemento, bairro, cidade, uf);
    }

    public EnderecoEntrega paraEntrega() {
        return new EnderecoEntrega(cep, logradouro, numero, complemento, bairro, cidade, uf);
    }
}
