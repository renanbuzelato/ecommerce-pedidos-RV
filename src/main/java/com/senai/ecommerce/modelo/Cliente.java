package com.senai.ecommerce.modelo;

public class Cliente extends Pessoa {
    private String email;
    private String telefone;
    private String endereco;

    public Cliente(String nome, String CPF, String email) {
        super(nome, CPF);
        setEmail(email);
    }

    public String getEmail() { return email; }
    
    public void setEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Erro: E-mail invalido. Deve conter '@'.");
        }
        this.email = email;
    }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }
    public String getEndereco() { return endereco; }
    public void setEndereco(String endereco) { this.endereco = endereco; }

    @Override
    public String getIdentificacao() {
        return getNome() + " (CPF " + getDocumento() + ")";
    }

    @Override
    public String toString() {
        return getIdentificacao() + " - " + email;
    }
}
