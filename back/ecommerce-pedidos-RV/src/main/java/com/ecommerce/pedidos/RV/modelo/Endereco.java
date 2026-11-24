package com.ecommerce.pedidos.RV.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Endereço de um cliente (Aula 15). Composição: o endereço não existe sem o
 * cliente -> cascade ALL + orphanRemoval do lado de Cliente e ON DELETE CASCADE no banco.
 */
@Entity
@Table(name = "endereco")
public class Endereco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @Column(length = 40)
    private String apelido;

    @Column(nullable = false, length = 9)
    private String cep;

    @Column(nullable = false, length = 120)
    private String logradouro;

    @Column(nullable = false, length = 20)
    private String numero;

    @Column(length = 60)
    private String complemento;

    @Column(nullable = false, length = 60)
    private String bairro;

    @Column(nullable = false, length = 60)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    protected Endereco() { }                 // exigido pelo JPA

    public Endereco(String apelido, String cep, String logradouro, String numero, String complemento,
                    String bairro, String cidade, String uf) {
        if (cep == null || cep.replaceAll("\\D", "").length() != 8) {
            throw new IllegalArgumentException("CEP deve ter 8 dígitos.");
        }
        if (isVazio(logradouro) || isVazio(numero) || isVazio(bairro) || isVazio(cidade)) {
            throw new IllegalArgumentException("Logradouro, número, bairro e cidade são obrigatórios.");
        }
        if (uf == null || uf.trim().length() != 2) {
            throw new IllegalArgumentException("UF deve ter 2 letras.");
        }
        this.apelido = apelido;
        this.cep = cep;
        this.logradouro = logradouro.trim();
        this.numero = numero.trim();
        this.complemento = complemento;
        this.bairro = bairro.trim();
        this.cidade = cidade.trim();
        this.uf = uf.trim().toUpperCase();
    }

    private static boolean isVazio(String s) {
        return s == null || s.isBlank();
    }

    /** Só o Cliente liga o endereço a si mesmo (mantém os dois lados da relação coerentes). */
    void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Long getId() { return id; }
    public Cliente getCliente() { return cliente; }
    public String getApelido() { return apelido; }
    public String getCep() { return cep; }
    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
}
