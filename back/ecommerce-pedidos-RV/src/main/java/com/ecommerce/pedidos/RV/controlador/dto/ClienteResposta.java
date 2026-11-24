package com.ecommerce.pedidos.RV.controlador.dto;

import com.ecommerce.pedidos.RV.modelo.Cliente;

import java.util.List;

public record ClienteResposta(Long id, String nome, String documento, String email,
                              String telefone, List<EnderecoDTO> enderecos) {

    public static ClienteResposta de(Cliente c) {
        return new ClienteResposta(c.getId(), c.getNome(), c.getDocumento(), c.getEmail(), c.getTelefone(),
                c.getEnderecos().stream().map(EnderecoDTO::de).toList());
    }
}
