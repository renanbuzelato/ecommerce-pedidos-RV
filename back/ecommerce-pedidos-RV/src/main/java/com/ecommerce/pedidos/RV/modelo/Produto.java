package com.ecommerce.modelo;

import java.math.BigDecimal;

public class Produto {
    private String codigo;
    private String nome;
    private BigDecimal preco;
    private int quantidadeEmEstoque;

    public Produto(String codigo, String nome, BigDecimal preco, int estoque) {
        setCodigoPrivate(codigo);
        setNome(nome);
        setPreco(preco);
        setQuantidadeEmEstoque(estoque);
    }

    private void setCodigoPrivate(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("Código é obrigatório");
        }
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        this.nome = nome;
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Preço não pode ser negativo");
        }
        this.preco = preco;
    }

    public int getQuantidadeEmEstoque() {
        return quantidadeEmEstoque;
    }

    public void setQuantidadeEmEstoque(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Estoque não pode ser negativo");
        }
        this.quantidadeEmEstoque = quantidade;
    }

    public void baixarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser positiva");
        }
        if (quantidade > this.quantidadeEmEstoque) {
            throw new IllegalArgumentException("Estoque insuficiente. Disponível: " + this.quantidadeEmEstoque);
        }
        this.quantidadeEmEstoque -= quantidade;
    }
}