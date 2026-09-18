package com.ecommerce.pedidos.RV.modelo;

public class Funcionario extends Pessoa {
    private String cargo;

    public Funcionario(String nome, String documento, String cargo) {
        super(nome, documento);
        this.cargo = cargo;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    @Override
    public String getIdentificacao() {
        return getNome() + " (" + cargo + ")";
    }
}