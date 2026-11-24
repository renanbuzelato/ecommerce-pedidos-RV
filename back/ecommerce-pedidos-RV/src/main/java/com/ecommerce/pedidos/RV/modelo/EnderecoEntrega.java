package com.ecommerce.pedidos.RV.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

/**
 * "Foto" do endereço no momento da compra, gravada nas colunas entrega_* do pedido.
 * Mesmo raciocínio do preço congelado no ItemPedido: se o cliente mudar o endereço
 * depois, o pedido continua mostrando para onde foi enviado.
 */
@Embeddable
public class EnderecoEntrega {

    @Column(name = "entrega_cep", length = 9)
    private String cep;

    @Column(name = "entrega_logradouro", length = 120)
    private String logradouro;

    @Column(name = "entrega_numero", length = 20)
    private String numero;

    @Column(name = "entrega_complemento", length = 60)
    private String complemento;

    @Column(name = "entrega_bairro", length = 60)
    private String bairro;

    @Column(name = "entrega_cidade", length = 60)
    private String cidade;

    @Column(name = "entrega_uf", length = 2)
    private String uf;

    protected EnderecoEntrega() { }          // exigido pelo JPA

    public EnderecoEntrega(String cep, String logradouro, String numero, String complemento,
                           String bairro, String cidade, String uf) {
        if (cep == null || logradouro == null || numero == null || cidade == null || uf == null) {
            throw new IllegalArgumentException("Endereço de entrega incompleto.");
        }
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
    }

    public String getCep() { return cep; }
    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getUf() { return uf; }
}
