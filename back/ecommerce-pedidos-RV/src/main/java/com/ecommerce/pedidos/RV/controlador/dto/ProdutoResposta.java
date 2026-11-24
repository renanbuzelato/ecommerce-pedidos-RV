package com.ecommerce.pedidos.RV.controlador.dto;

import com.ecommerce.pedidos.RV.modelo.Produto;

import java.math.BigDecimal;

/** JSON de saída do produto. A entidade nunca sai direto pela API. */
public record ProdutoResposta(Long id, String nome, String descricao, String categoria,
                              BigDecimal preco, int quantidadeEmEstoque, boolean ativo) {

    public static ProdutoResposta de(Produto p) {
        return new ProdutoResposta(p.getId(), p.getNome(), p.getDescricao(), p.getCategoria(),
                p.getPreco(), p.getQuantidadeEmEstoque(), p.isAtivo());
    }
}
