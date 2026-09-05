package com.senai.ecommerce.modelo;

import java.math.BigDecimal;

public class Produto {
    private final String codigo; // SKU Imutavel - Sem setter publico
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private int quantidadeEmEstoque;
    private boolean ativo;

    public Produto(String codigo, String nome, BigDecimal preco, int quantidadeEmEstoque) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O codigo do produto nao pode ser nulo ou vazio.");
        }
        this.codigo = codigo.trim();
        setNome(nome);
        setPreco(preco);
        setQuantidadeEmEstoque(quantidadeEmEstoque);
        this.ativo = true;
    }

    public String getCodigo() { return codigo; }
    
    public String getNome() { return nome; }
    
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: O nome do produto nao pode ser nulo ou em branco.");
        }
        this.nome = nome.trim();
    }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getPreco() { return preco; }
    
    public void setPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Erro: O preco do produto nao pode ser negativo.");
        }
        this.preco = preco;
    }

    public int getQuantidadeEmEstoque() { return quantidadeEmEstoque; }
    
    public void setQuantidadeEmEstoque(int estoque) {
        if (estoque < 0) {
            throw new IllegalArgumentException("Erro: A quantidade em estoque nao pode ser negativa.");
        }
        this.quantidadeEmEstoque = estoque;
    }

    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    public boolean temEstoqueDisponivel(int quantidadeDesejada) {
        return ativo && quantidadeEmEstoque >= quantidadeDesejada;
    }

    public void baixarEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Erro: A quantidade a ser baixada deve ser positiva.");
        }
        if (quantidade > quantidadeEmEstoque) {
            throw new IllegalArgumentException(
                "Erro: Estoque insuficiente. Disponivel: " + quantidadeEmEstoque + ", Solicitado: " + quantidade);
        }
        this.quantidadeEmEstoque -= quantidade;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - R$ %,.2f (%d em estoque)", codigo, nome, preco, quantidadeEmEstoque);
    }
}
