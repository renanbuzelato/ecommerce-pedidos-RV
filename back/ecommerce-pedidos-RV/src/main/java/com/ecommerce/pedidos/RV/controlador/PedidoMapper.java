package com.ecommerce.pedidos.RV.controlador;

import com.ecommerce.pedidos.RV.controlador.dto.EnderecoDTO;
import com.ecommerce.pedidos.RV.controlador.dto.PedidoResposta;
import com.ecommerce.pedidos.RV.modelo.Pedido;
import com.ecommerce.pedidos.RV.modelo.RegistroPagamento;
import com.ecommerce.pedidos.RV.servico.CalculadoraFrete;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Converte Pedido -> PedidoResposta. É um bean porque precisa da CalculadoraFrete
 * (a mesma regra usada no ServicoPedido), em vez de repetir o cálculo aqui.
 * Recebe pedidos já carregados com cliente, itens e produtos (JOIN FETCH no repositório).
 */
@Component
public class PedidoMapper {

    private final CalculadoraFrete frete;

    public PedidoMapper(CalculadoraFrete frete) {
        this.frete = frete;
    }

    public PedidoResposta paraResposta(Pedido p) {
        BigDecimal subtotal = p.calcularValorTotal();
        BigDecimal valorFrete = p.getItens().isEmpty() ? BigDecimal.ZERO : frete.calcular(subtotal);

        var itens = p.getItens().stream()
                .map(i -> new PedidoResposta.Item(i.getId(),
                        new PedidoResposta.Resumo(i.getProduto().getId(), i.getProduto().getNome()),
                        i.getQuantidade(), i.getPrecoUnitario(), i.getPrecoTotal()))
                .toList();

        var historico = p.getHistorico().stream()
                .map(h -> new PedidoResposta.Historico(h.getSituacao(), h.getData()))
                .toList();

        RegistroPagamento pg = p.getPagamento();
        var pagamento = pg == null || pg.getForma() == null ? null
                : new PedidoResposta.Pagamento(pg.getForma(), pg.getResultado(), pg.getComprovante(), pg.getDescricao());

        return new PedidoResposta(p.getId(),
                new PedidoResposta.Resumo(p.getCliente().getId(), p.getCliente().getNome()),
                EnderecoDTO.de(p.getEnderecoEntrega()),
                itens, p.getSituacao(), p.getDataCriacao(),
                subtotal, valorFrete, subtotal.add(valorFrete),
                historico, pagamento);
    }
}
