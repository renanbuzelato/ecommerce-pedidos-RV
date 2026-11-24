import { api } from './api/client';

/**
 * Envia o pagamento simulado. Resultado: APROVADO | RECUSADO | PENDENTE.
 * `dados` varia por forma: CARTAO {numero, titular, validade, cvv, parcelas},
 * PIX {chave}, BOLETO {vencimento}.
 */
export const pagamentoService = {
  processar({ pedidoId, forma, dados }) {
    // Nunca enviamos o CVV adiante: só os dados que o back precisa para simular
    const { cvv: _cvv, ...dadosSeguros } = dados;
    return api.post('/pagamentos', { pedidoId, forma, dados: dadosSeguros });
  },
};
