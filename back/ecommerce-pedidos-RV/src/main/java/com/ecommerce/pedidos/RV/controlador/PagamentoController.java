package com.ecommerce.pedidos.RV.controlador;

import com.ecommerce.pedidos.RV.controlador.dto.PagamentoEntrada;
import com.ecommerce.pedidos.RV.controlador.dto.PagamentoResposta;
import com.ecommerce.pedidos.RV.excecao.PedidoInvalidoException;
import com.ecommerce.pedidos.RV.servico.ServicoPagamento;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Pagamento como SUB-RECURSO do pedido (decisão da equipe, Aula 15 – Momento 3, Passo 2):
 * pagar não é criar nem atualizar um pedido, é uma ação sobre ele. Em REST, isso vira
 * "criar um pagamento do pedido".
 *
 *   POST /api/pedidos/{id}/pagamento   201 + Location { resultado: APROVADO | RECUSADO | PENDENTE }
 *                                      400 | 404 | 409 (pedido já pago ou cancelado)
 *   GET  /api/pedidos/{id}/pagamento   200 | 404 (pedido inexistente ou sem pagamento)
 *
 * Pagamento RECUSADO também é 201: a tentativa foi registrada; quem recusou foi a operadora,
 * e o pedido continua aberto para uma nova tentativa.
 */
@RestController
@RequestMapping("/api/pedidos/{pedidoId}/pagamento")
public class PagamentoController {

    private final ServicoPagamento servico;

    public PagamentoController(ServicoPagamento servico) {
        this.servico = servico;
    }

    @PostMapping
    public ResponseEntity<PagamentoResposta> pagar(@PathVariable Long pedidoId,
                                                   @Valid @RequestBody PagamentoEntrada entrada,
                                                   UriComponentsBuilder uriBuilder)
            throws PedidoInvalidoException {
        var resultado = servico.pagar(pedidoId, entrada.forma(), entrada.dados());
        return ResponseEntity.created(uriBuilder.path("/api/pedidos/{id}/pagamento")
                        .buildAndExpand(pedidoId).toUri())
                .body(PagamentoResposta.de(resultado));
    }

    @GetMapping
    public PagamentoResposta buscar(@PathVariable Long pedidoId) {
        return PagamentoResposta.de(pedidoId, servico.buscarRegistro(pedidoId));
    }
}
