package com.ecommerce.pedidos.RV.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

/**
 * Superclasse da herança da Aula 05.
 * @MappedSuperclass: Pessoa não vira tabela; os campos dela são copiados
 * para a tabela de cada entidade filha (hoje, só Cliente é persistido).
 */
@MappedSuperclass
public abstract class Pessoa {

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(name = "cpf", nullable = false, length = 14, unique = true)
    private String documento;

    protected Pessoa() { }                   // exigido pelo JPA

    public Pessoa(String nome, String documento) {
        setNome(nome);
        setDocumento(documento);
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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        if (documento == null || documento.isBlank()) {
            throw new IllegalArgumentException("Documento é obrigatório");
        }
        this.documento = documento;
    }

    public abstract String getIdentificacao();
}
