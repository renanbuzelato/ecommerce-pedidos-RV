package com.ecommerce.pedidos.RV.servico;

import com.ecommerce.pedidos.RV.excecao.PedidoInvalidoException;
import com.ecommerce.pedidos.RV.modelo.TipoPagamento;
import com.ecommerce.pedidos.RV.modelo.pagamento.Boleto;
import com.ecommerce.pedidos.RV.modelo.pagamento.CartaoCredito;
import com.ecommerce.pedidos.RV.modelo.pagamento.Pix;
import com.ecommerce.pedidos.RV.modelo.pagamento.ProcessadorPagamento;
import com.ecommerce.pedidos.RV.servico.ServicoPedido.ResultadoProcessamento;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Escolhe a implementação de ProcessadorPagamento a partir da forma enviada pela
 * interface (fábrica simples) e delega o processamento ao ServicoPedido.
 * O polimorfismo da Aula 08 continua intacto: o Pedido só conhece a interface.
 */
@Service
public class ServicoPagamento {

    /** Dados que a interface envia; cada forma usa só os campos dela. O CVV nunca chega aqui. */
    public record DadosPagamento(String chave, String numero, String titular, Integer parcelas) { }

    private static final int DIAS_VENCIMENTO_BOLETO = 3;

    private final ServicoPedido servicoPedido;

    public ServicoPagamento(ServicoPedido servicoPedido) {
        this.servicoPedido = servicoPedido;
    }

    public ResultadoProcessamento pagar(Long pedidoId, TipoPagamento forma, DadosPagamento dados)
            throws PedidoInvalidoException {
        if (forma == null) {
            throw new IllegalArgumentException("Forma de pagamento inválida.");
        }
        BigDecimal valor = servicoPedido.calcularTotalComFrete(pedidoId);
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new PedidoInvalidoException("Não é possível pagar um pedido sem itens.");
        }
        DadosPagamento d = dados == null ? new DadosPagamento(null, null, null, null) : dados;
        return servicoPedido.processarPagamento(pedidoId, forma, criarProcessador(forma, valor, d));
    }

    private ProcessadorPagamento criarProcessador(TipoPagamento forma, BigDecimal valor, DadosPagamento d) {
        return switch (forma) {
            case PIX -> new Pix(valor, d.chave());
            case CARTAO -> {
                String numero = d.numero() == null ? "" : d.numero().replaceAll("\\D", "");
                if (numero.length() != 16) {
                    throw new IllegalArgumentException("O número do cartão deve ter 16 dígitos.");
                }
                int parcelas = d.parcelas() == null ? 1 : d.parcelas();
                if (parcelas < 1) {
                    throw new IllegalArgumentException("Número de parcelas inválido.");
                }
                yield new CartaoCredito(valor, numero, parcelas);
            }
            case BOLETO -> new Boleto(valor, LocalDate.now().plusDays(DIAS_VENCIMENTO_BOLETO));
        };
    }
}
