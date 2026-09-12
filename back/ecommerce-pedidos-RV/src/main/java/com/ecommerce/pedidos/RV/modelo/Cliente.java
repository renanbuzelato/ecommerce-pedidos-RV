package com.ecommerce.modelo;

public class Cliente extends Pessoa {
    private String email;

    public Cliente(String nome, String cpf, String email) {
        super(nome, cpf); // Chama o construtor de Pessoa
        setEmail(email);
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getIdentificacao() {
        return getNome() + " (CPF " + getDocumento() + ")";
    }
}