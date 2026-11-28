import { api } from './api/client';

/**
 * Pagamento é SUB-RECURSO do pedido: POST /pedidos/{id}/pagamento (201 + Location).
 * Resultado: APROVADO | RECUSADO | PENDENTE.
 * `dados` varia por forma: CARTAO {numero, titular, validade, cvv, parcelas},
 * PIX {chave}, BOLETO {vencimento}.
 */
export const pagamentoService = {
  processar({ pedidoId, forma, dados }) {
    // Nunca enviamos o CVV adiante: só os dados que o back precisa para simular
    const { cvv: _cvv, ...dadosSeguros } = dados;
    return api.post(`/pedidos/${pedidoId}/pagamento`, { forma, dados: dadosSeguros });
  },
  /** Última tentativa de pagamento do pedido (404 se ainda não houve nenhuma). */
  buscar(pedidoId) {
    return api.get(`/pedidos/${pedidoId}/pagamento`);
  },
};
