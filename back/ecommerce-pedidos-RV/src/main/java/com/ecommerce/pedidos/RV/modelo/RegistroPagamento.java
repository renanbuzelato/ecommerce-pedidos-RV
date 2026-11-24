package com.ecommerce.pedidos.RV.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Última tentativa de pagamento do pedido, gravada nas colunas pagamento_* da tabela pedido.
 * Se todas as colunas forem nulas, o Hibernate devolve o campo como null (pedido sem pagamento).
 */
@Embeddable
public class RegistroPagamento {

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "pagamento_forma", length = 10)
    private TipoPagamento forma;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "pagamento_resultado", length = 10)
    private ResultadoPagamento resultado;

    @Column(name = "pagamento_comprovante", length = 40)
    private String comprovante;

    @Column(name = "pagamento_descricao", length = 120)
    private String descricao;

    protected RegistroPagamento() { }        // exigido pelo JPA

    public RegistroPagamento(TipoPagamento forma, ResultadoPagamento resultado,
                             String comprovante, String descricao) {
        this.forma = forma;
        this.resultado = resultado;
        this.comprovante = comprovante;
        this.descricao = descricao;
    }

    /** Usado na compensação do boleto: o pagamento pendente passa a aprovado. */
    RegistroPagamento comResultado(ResultadoPagamento novo) {
        return new RegistroPagamento(forma, novo, comprovante, descricao);
    }

    public TipoPagamento getForma() { return forma; }
    public ResultadoPagamento getResultado() { return resultado; }
    public String getComprovante() { return comprovante; }
    public String getDescricao() { return descricao; }
}
