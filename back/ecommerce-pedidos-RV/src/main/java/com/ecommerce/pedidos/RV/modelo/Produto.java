package com.ecommerce.pedidos.RV.modelo;

import com.ecommerce.pedidos.RV.excecao.EstoqueInsuficienteException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "quantidade", nullable = false)
    private int quantidadeEmEstoque;

    @Column(nullable = false)
    private boolean ativo = true;

    // Aula 15: campos exigidos pela interface (catálogo com filtro por categoria)
    @Column(length = 300)
    private String descricao;

    @Column(nullable = false, length = 60)
    private String categoria = "Geral";

    protected Produto() { }                  // exigido pelo JPA - ninguém deve usar para criar produto

    public Produto(String nome, BigDecimal preco, int quantidadeEmEstoque) {
        // Validações da Aula 09 continuam aqui: o JPA NÃO passa por este construtor
        setNome(nome);
        alterarPreco(preco);
        if (quantidadeEmEstoque < 0) {
            throw new IllegalArgumentException("A quantidade em estoque não pode ser negativa.");
        }
        this.quantidadeEmEstoque = quantidadeEmEstoque;
    }

    public void baixarEstoque(int quantidade) throws EstoqueInsuficienteException {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a baixar deve ser positiva.");
        }
        if (quantidade > this.quantidadeEmEstoque) {
            throw new EstoqueInsuficienteException(this, quantidade);
        }
        this.quantidadeEmEstoque -= quantidade;
    }

    public void reporEstoque(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a repor deve ser positiva.");
        }
        this.quantidadeEmEstoque += quantidade;
    }

    public void alterarPreco(BigDecimal novoPreco) {
        if (novoPreco == null || novoPreco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço do produto deve ser positivo.");
        }
        this.preco = novoPreco;
    }

    public void setNome(String nome) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("O nome do produto não pode ser vazio.");
        }
        this.nome = nome.trim();
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao == null ? null : descricao.trim();
    }

    public void setCategoria(String categoria) {
        if (categoria == null || categoria.isBlank()) {
            throw new IllegalArgumentException("A categoria do produto é obrigatória.");
        }
        this.categoria = categoria.trim();
    }

    /**
     * Alteração completa pela API (PUT). Um método da própria entidade, e não setters soltos:
     * é o encapsulamento da Aula 05, e as validações da Aula 09 continuam valendo na alteração.
     */
    public void alterarDados(String nome, BigDecimal preco, int quantidade) {
        setNome(nome);
        alterarPreco(preco);
        definirEstoque(quantidade);
    }

    /** Ajuste de inventário feito pela área administrativa (edição do produto). */
    public void definirEstoque(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("A quantidade em estoque não pode ser negativa.");
        }
        this.quantidadeEmEstoque = quantidade;
    }

    public void desativar() { this.ativo = false; }
    public void ativar()    { this.ativo = true; }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public BigDecimal getPreco() { return preco; }
    public int getQuantidadeEmEstoque() { return quantidadeEmEstoque; }
    public boolean isAtivo() { return ativo; }
    public String getDescricao() { return descricao; }
    public String getCategoria() { return categoria; }

    @Override
    public String toString() {
        return "Produto[id=" + id + ", nome=" + nome + ", preco=" + preco
                + ", estoque=" + quantidadeEmEstoque + ", ativo=" + ativo + "]";
    }
}
