package com.ecommerce.pedidos.RV.modelo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

/** Uma linha do histórico do pedido (tabela pedido_historico): situação + quando. */
@Embeddable
public class RegistroSituacao {

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private SituacaoPedido situacao;

    @Column(name = "data_registro", nullable = false)
    private LocalDateTime data;

    protected RegistroSituacao() { }         // exigido pelo JPA

    public RegistroSituacao(SituacaoPedido situacao, LocalDateTime data) {
        this.situacao = situacao;
        this.data = data;
    }

    public SituacaoPedido getSituacao() { return situacao; }
    public LocalDateTime getData() { return data; }
}
