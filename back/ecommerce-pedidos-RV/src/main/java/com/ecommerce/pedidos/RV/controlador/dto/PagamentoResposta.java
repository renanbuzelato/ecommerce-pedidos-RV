package com.ecommerce.pedidos.RV.controlador.dto;

import com.ecommerce.pedidos.RV.modelo.RegistroPagamento;
import com.ecommerce.pedidos.RV.modelo.ResultadoPagamento;
import com.ecommerce.pedidos.RV.modelo.TipoPagamento;
import com.ecommerce.pedidos.RV.servico.ServicoPedido.ResultadoProcessamento;

/** JSON do pagamento (sub-recurso do pedido). "mensagem" só vem na resposta do POST. */
public record PagamentoResposta(Long pedidoId, TipoPagamento forma, ResultadoPagamento resultado,
                                String comprovante, String descricao, String mensagem) {

    public static PagamentoResposta de(ResultadoProcessamento r) {
        return new PagamentoResposta(r.pedidoId(), r.forma(), r.resultado(),
                r.comprovante(), r.descricao(), r.mensagem());
    }

    public static PagamentoResposta de(Long pedidoId, RegistroPagamento p) {
        return new PagamentoResposta(pedidoId, p.getForma(), p.getResultado(),
                p.getComprovante(), p.getDescricao(), null);
    }
}
