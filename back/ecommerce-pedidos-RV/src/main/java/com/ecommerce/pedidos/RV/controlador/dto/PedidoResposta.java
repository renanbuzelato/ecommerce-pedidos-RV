package com.ecommerce.pedidos.RV.controlador.dto;

import com.ecommerce.pedidos.RV.modelo.ResultadoPagamento;
import com.ecommerce.pedidos.RV.modelo.SituacaoPedido;
import com.ecommerce.pedidos.RV.modelo.TipoPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** JSON de saída do pedido (montado pelo PedidoMapper, que também calcula o frete). */
public record PedidoResposta(
        Long id,
        Resumo cliente,
        EnderecoDTO enderecoEntrega,
        List<Item> itens,
        SituacaoPedido situacao,
        LocalDateTime dataCriacao,
        BigDecimal valorTotal,
        BigDecimal frete,
        BigDecimal totalComFrete,
        List<Historico> historico,
        Pagamento pagamento) {

    /** Referência curta (id + nome) para cliente e produto. */
    public record Resumo(Long id, String nome) { }

    public record Item(Long id, Resumo produto, int quantidade, BigDecimal precoUnitario, BigDecimal precoTotal) { }

    public record Historico(SituacaoPedido situacao, LocalDateTime data) { }

    public record Pagamento(TipoPagamento forma, ResultadoPagamento resultado, String comprovante, String descricao) { }
}
