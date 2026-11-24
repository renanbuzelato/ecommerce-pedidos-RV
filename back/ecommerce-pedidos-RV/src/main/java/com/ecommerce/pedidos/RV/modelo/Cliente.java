package com.ecommerce.pedidos.RV.modelo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "cliente")
public class Cliente extends Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String email;

    // Aula 15: dados exigidos pela interface
    @Column(length = 20)
    private String telefone;

    // Composição: endereço não vive sem o cliente.
    // EAGER: a lista é pequena e o JSON do cliente sempre a inclui (com open-in-view=false,
    // uma coleção LAZY não inicializada daria LazyInitializationException ao serializar).
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("id ASC")
    private List<Endereco> enderecos = new ArrayList<>();

    protected Cliente() { }                  // exigido pelo JPA

    public Cliente(String nome, String cpf, String email) {
        super(nome, cpf); // validações de nome e documento ficam em Pessoa
        setEmail(email);
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new IllegalArgumentException("E-mail inválido");
        }
        this.email = email.trim().toLowerCase();
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public List<Endereco> getEnderecos() {
        return Collections.unmodifiableList(enderecos);
    }

    public void adicionarEndereco(Endereco endereco) {
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço não pode ser nulo.");
        }
        endereco.setCliente(this);
        enderecos.add(endereco);
    }

    /** Troca todos os endereços (orphanRemoval apaga os que saíram da lista). */
    public void substituirEnderecos(List<Endereco> novos) {
        enderecos.clear();
        novos.forEach(this::adicionarEndereco);
    }

    @Override
    public String getIdentificacao() {
        return getNome() + " (CPF " + getDocumento() + ")";
    }
}
