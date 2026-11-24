/**
 * Situações do pedido. As CHAVES seguem o enum SituacaoPedido do back-end
 * (ABERTO, PAGO, CANCELADO) acrescido de ENVIADO e ENTREGUE pedidos pelo desafio.
 * O rótulo exibido para ABERTO é "Pendente".
 */
export const SITUACOES = {
  ABERTO: { rotulo: 'Pendente', cor: 'amber' },
  PAGO: { rotulo: 'Pago', cor: 'blue' },
  ENVIADO: { rotulo: 'Enviado', cor: 'indigo' },
  ENTREGUE: { rotulo: 'Entregue', cor: 'green' },
  CANCELADO: { rotulo: 'Cancelado', cor: 'red' },
};

/** Linha do tempo "feliz" do pedido. CANCELADO é um desvio. */
export const FLUXO_SITUACAO = ['ABERTO', 'PAGO', 'ENVIADO', 'ENTREGUE'];

/** Transições permitidas (espelha as regras de Pedido.pagar()/cancelar() do back-end). */
export const TRANSICOES = {
  ABERTO: ['CANCELADO'], // ABERTO -> PAGO só acontece por meio de um pagamento aprovado
  PAGO: ['ENVIADO'],
  ENVIADO: ['ENTREGUE'],
  ENTREGUE: [],
  CANCELADO: [],
};

export const CATEGORIAS = ['Eletrônicos', 'Informática', 'Casa', 'Livros', 'Esportes'];

export const FORMAS_PAGAMENTO = {
  CARTAO: 'Cartão de crédito',
  PIX: 'Pix',
  BOLETO: 'Boleto bancário',
};

export const RESULTADOS_PAGAMENTO = {
  APROVADO: { rotulo: 'Pagamento aprovado', cor: 'green' },
  RECUSADO: { rotulo: 'Pagamento recusado', cor: 'red' },
  PENDENTE: { rotulo: 'Pagamento pendente', cor: 'amber' },
};

/** Mesma regra da CalculadoraFrete do back-end. */
export const FRETE_GRATIS_A_PARTIR_DE = 200;
export const FRETE_PADRAO = 19.9;
export const calcularFrete = (subtotal) =>
  subtotal <= 0 || subtotal >= FRETE_GRATIS_A_PARTIR_DE ? 0 : FRETE_PADRAO;

export const ESTOQUE_BAIXO = 5;
