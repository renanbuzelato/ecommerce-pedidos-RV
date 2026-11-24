package com.ecommerce.pedidos.RV.controlador;

import com.ecommerce.pedidos.RV.controlador.dto.PagamentoEntrada;
import com.ecommerce.pedidos.RV.excecao.PedidoInvalidoException;
import com.ecommerce.pedidos.RV.servico.ServicoPagamento;
import com.ecommerce.pedidos.RV.servico.ServicoPedido.ResultadoProcessamento;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *   POST /api/pagamentos   200 { resultado: APROVADO | RECUSADO | PENDENTE, ... }
 *                          422 se o pedido já foi pago ou está cancelado
 * Pagamento RECUSADO é 200: a requisição deu certo, quem recusou foi a operadora.
 */
@RestController
@RequestMapping("/api/pagamentos")
public class PagamentoController {

    private final ServicoPagamento servico;

    public PagamentoController(ServicoPagamento servico) {
        this.servico = servico;
    }

    @PostMapping
    public ResultadoProcessamento pagar(@Valid @RequestBody PagamentoEntrada entrada) throws PedidoInvalidoException {
        return servico.pagar(entrada.pedidoId(), entrada.forma(), entrada.dados());
    }
}
