package com.senai.ecommerce.modelo;

/**
 * Representa um conceito abstrato de pessoa no sistema, contendo nome e documento.
 * Serve como classe mae para Cliente e outras futuras especializacoes.
 */
public abstract class Pessoa {
    private String nome;
    private String documento;

    public Pessoa(String nome, String documento) {
        setNome(nome);
        setDocumento(documento);
    }

    public String getNome() { return nome; }
    
    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: Nome nao pode ser nulo ou em branco.");
        }
        this.nome = nome.trim();
    }

    public String getDocumento() { return documento; }
    
    public void setDocumento(String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            throw new IllegalArgumentException("Erro: Documento nao pode ser nulo ou em branco.");
        }
        // Remove caracteres nao numericos para fins de padronizacao
        this.documento = documento.replaceAll("[^\d]", "");
        if (this.documento.isEmpty()) {
            throw new IllegalArgumentException("Erro: Documento deve conter digitos validos.");
        }
    }

    /**
     * Retorna a identificacao formatada e personalizada da pessoa de acordo com a sua classe concreta.
     */
    public abstract String getIdentificacao();
}
