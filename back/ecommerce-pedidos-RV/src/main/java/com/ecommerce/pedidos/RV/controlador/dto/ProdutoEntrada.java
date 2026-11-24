package com.ecommerce.pedidos.RV.controlador.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Corpo do POST/PUT /api/produtos. Bean Validation barra o básico antes de chegar ao serviço. */
public record ProdutoEntrada(
        @NotBlank(message = "O nome do produto não pode ser vazio.") @Size(max = 120) String nome,
        @Size(max = 300) String descricao,
        @NotBlank(message = "A categoria do produto é obrigatória.") @Size(max = 60) String categoria,
        @NotNull(message = "Informe o preço.") @Positive(message = "O preço do produto deve ser positivo.") BigDecimal preco,
        @NotNull(message = "Informe o estoque.") @PositiveOrZero(message = "A quantidade em estoque não pode ser negativa.") Integer quantidadeEmEstoque,
        Boolean ativo) {

    public boolean ativoOuPadrao() {
        return ativo == null || ativo;
    }
}
