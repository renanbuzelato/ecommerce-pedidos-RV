import { ApiError } from './ApiError';
import { criarSeed } from './seed';
import { calcularFrete, TRANSICOES } from '../../utils/constants';

/**
 * API SIMULADA — responde às mesmas rotas do contrato REST (/produtos, /clientes,
 * /pedidos e o sub-recurso /pedidos/{id}/pagamento) e aplica as MESMAS regras de negócio do back-end Java:
 *  - nome de produto único (ProdutoDuplicadoException -> 409)
 *  - CPF e e-mail de cliente únicos (ClienteDuplicadoException -> 409)
 *  - baixa de estoque ao criar pedido (EstoqueInsuficienteException -> 409)
 *  - produto inativo não entra em pedido; cancelar devolve o estoque
 *  - pedido pago não pode ser cancelado; não se paga pedido pago/cancelado
 *  - frete grátis a partir de R$ 200 (CalculadoraFrete)
 *  - Pix aprova, Boleto fica pendente, Cartão aprova (máx. 12 parcelas)
 * Códigos iguais ao TratadorDeErros: 400 dados inválidos, 404 não encontrado, 409 conflito.
 * Os dados ficam no localStorage, então sobrevivem ao F5.
 */
const CHAVE = 'rv-mock-db';

const clonar = (obj) => JSON.parse(JSON.stringify(obj));

function carregar() {
  try {
    const salvo = localStorage.getItem(CHAVE);
    if (salvo) return JSON.parse(salvo);
  } catch {
    /* localStorage indisponível: segue em memória */
  }
  return criarSeed();
}

let db = carregar();

function salvar() {
  try {
    localStorage.setItem(CHAVE, JSON.stringify(db));
  } catch {
    /* ignora */
  }
}

export function resetarMock() {
  db = criarSeed();
  salvar();
}

const proximoId = (tabela) => ++db.sequencias[tabela];
const agoraIso = () => new Date().toISOString();
const normalizar = (s = '') => s.trim().toLowerCase();
const digitos = (s = '') => s.replace(/\D/g, '');

function naoEncontrado(entidade, id) {
  return new ApiError(404, `${entidade} ${id} não encontrado.`);
}

function encontrar(tabela, id, entidade) {
  const registro = db[tabela].find((r) => r.id === Number(id));
  if (!registro) throw naoEncontrado(entidade, id);
  return registro;
}

function totaisDoPedido(pedido) {
  const subtotal = pedido.itens.reduce((s, i) => s + Number(i.precoUnitario) * i.quantidade, 0);
  const frete = calcularFrete(subtotal);
  return {
    valorTotal: subtotal.toFixed(2),
    frete: frete.toFixed(2),
    totalComFrete: (subtotal + frete).toFixed(2),
  };
}

const comTotais = (pedido) => ({ ...pedido, ...totaisDoPedido(pedido) });

/* ------------------------------- PRODUTOS ------------------------------- */

function validarProdutoBody(body) {
  if (!body?.nome?.trim()) throw new ApiError(400, 'O nome do produto não pode ser vazio.');
  if (!(Number(body.preco) > 0)) throw new ApiError(400, 'O preço do produto deve ser positivo.');
  if (Number(body.quantidadeEmEstoque) < 0)
    throw new ApiError(400, 'A quantidade em estoque não pode ser negativa.');
}

function nomeDuplicado(nome, ignorarId) {
  return db.produtos.some((p) => normalizar(p.nome) === normalizar(nome) && p.id !== ignorarId);
}

const produtos = {
  listar: () => db.produtos,
  buscar: (id) => encontrar('produtos', id, 'Produto'),
  criar(body) {
    validarProdutoBody(body);
    if (nomeDuplicado(body.nome))
      throw new ApiError(409, `Já existe um produto chamado "${body.nome}".`);
    const novo = {
      ...body,
      nome: body.nome.trim(),
      id: proximoId('produtos'),
      ativo: body.ativo ?? true,
    };
    db.produtos.push(novo);
    return novo;
  },
  atualizar(id, body) {
    const atual = encontrar('produtos', id, 'Produto');
    validarProdutoBody(body);
    if (nomeDuplicado(body.nome, atual.id))
      throw new ApiError(409, `Já existe um produto chamado "${body.nome}".`);
    Object.assign(atual, body, { id: atual.id, nome: body.nome.trim() });
    return atual;
  },
  excluir(id) {
    const atual = encontrar('produtos', id, 'Produto');
    // RESTRICT (Aula 13): produto já vendido não pode ser apagado
    const vendido = db.pedidos.some((p) => p.itens.some((i) => i.produto.id === atual.id));
    if (vendido) {
      throw new ApiError(
        409,
        `"${atual.nome}" já aparece em pedidos e não pode ser excluído. Desative-o na edição.`,
      );
    }
    db.produtos = db.produtos.filter((p) => p.id !== atual.id);
    return null;
  },
};

/* ------------------------------- CLIENTES ------------------------------- */

function verificarUnicidadeCliente(body, ignorarId) {
  const outros = db.clientes.filter((c) => c.id !== ignorarId);
  if (outros.some((c) => normalizar(c.email) === normalizar(body.email)))
    throw new ApiError(409, `Já existe um cliente com o e-mail ${body.email}.`);
  if (outros.some((c) => digitos(c.documento) === digitos(body.documento)))
    throw new ApiError(409, `Já existe um cliente com o CPF ${body.documento}.`);
}

const comIdsDeEndereco = (enderecos = []) =>
  enderecos.map((e) => ({ ...e, id: e.id ?? proximoId('enderecos') }));

const clientes = {
  listar: () => db.clientes,
  buscar: (id) => encontrar('clientes', id, 'Cliente'),
  criar(body) {
    if (!body?.nome?.trim()) throw new ApiError(400, 'Nome é obrigatório');
    if (!body?.email?.includes('@')) throw new ApiError(400, 'E-mail inválido');
    verificarUnicidadeCliente(body);
    const novo = {
      ...body,
      email: normalizar(body.email),
      id: proximoId('clientes'),
      enderecos: comIdsDeEndereco(body.enderecos),
    };
    db.clientes.push(novo);
    return novo;
  },
  atualizar(id, body) {
    const atual = encontrar('clientes', id, 'Cliente');
    verificarUnicidadeCliente(body, atual.id);
    Object.assign(atual, body, {
      id: atual.id,
      email: normalizar(body.email),
      enderecos: comIdsDeEndereco(body.enderecos),
    });
    // mantém o nome do cliente atualizado nos pedidos (como faria um JOIN)
    db.pedidos.forEach((p) => {
      if (p.cliente.id === atual.id) p.cliente.nome = atual.nome;
    });
    return atual;
  },
};

/* -------------------------------- PEDIDOS ------------------------------- */

function registrarSituacao(pedido, situacao) {
  pedido.situacao = situacao;
  pedido.historico.push({ situacao, data: agoraIso() });
}

const pedidos = {
  listar: () => db.pedidos.map(comTotais),
  buscar: (id) => comTotais(encontrar('pedidos', id, 'Pedido')),
  criar(body) {
    const cliente = encontrar('clientes', body?.clienteId, 'Cliente');
    if (!body.itens?.length) throw new ApiError(409, 'Não é possível criar um pedido sem itens.');
    if (!body.enderecoEntrega) throw new ApiError(409, 'Informe o endereço de entrega.');

    // 1ª passada: valida tudo antes de alterar qualquer estoque (equivale ao rollback)
    const linhas = body.itens.map(({ produtoId, quantidade }) => {
      const produto = encontrar('produtos', produtoId, 'Produto');
      if (!produto.ativo) throw new ApiError(409, `Produto ${produto.nome} está inativo.`);
      if (!(quantidade > 0)) throw new ApiError(400, 'Quantidade deve ser positiva.');
      if (quantidade > produto.quantidadeEmEstoque) {
        throw new ApiError(
          409,
          `Estoque insuficiente para "${produto.nome}": solicitado ${quantidade}, disponível ${produto.quantidadeEmEstoque}.`,
        );
      }
      return { produto, quantidade };
    });

    // 2ª passada: baixa o estoque e congela o preço no item
    const itens = linhas.map(({ produto, quantidade }) => {
      produto.quantidadeEmEstoque -= quantidade;
      return {
        id: proximoId('itens'),
        produto: { id: produto.id, nome: produto.nome },
        quantidade,
        precoUnitario: produto.preco,
      };
    });

    const agora = agoraIso();
    const novo = {
      id: proximoId('pedidos'),
      cliente: { id: cliente.id, nome: cliente.nome },
      enderecoEntrega: body.enderecoEntrega,
      itens,
      situacao: 'ABERTO',
      dataCriacao: agora,
      historico: [{ situacao: 'ABERTO', data: agora }],
      pagamento: null,
    };
    db.pedidos.push(novo);
    return comTotais(novo);
  },
  alterarSituacao(id, body) {
    const pedido = encontrar('pedidos', id, 'Pedido');
    const nova = body?.situacao;
    // Compensação do boleto: único caso em que ABERTO -> PAGO acontece sem novo pagamento
    const boletoPendente =
      pedido.pagamento?.forma === 'BOLETO' && pedido.pagamento?.resultado === 'PENDENTE';
    if (pedido.situacao === 'ABERTO' && nova === 'PAGO') {
      if (!boletoPendente)
        throw new ApiError(409, 'Pedido só fica pago após um pagamento aprovado.');
      pedido.pagamento.resultado = 'APROVADO';
      registrarSituacao(pedido, 'PAGO');
      return comTotais(pedido);
    }
    if (!(TRANSICOES[pedido.situacao] || []).includes(nova)) {
      if (pedido.situacao === 'PAGO' && nova === 'CANCELADO')
        throw new ApiError(409, 'Pedido pago não pode ser cancelado.');
      throw new ApiError(409, `Não é possível mudar o pedido de ${pedido.situacao} para ${nova}.`);
    }
    if (nova === 'CANCELADO') {
      // Pedido.cancelar(): devolve os itens ao estoque
      pedido.itens.forEach((i) => {
        const produto = db.produtos.find((p) => p.id === i.produto.id);
        if (produto) produto.quantidadeEmEstoque += i.quantidade;
      });
    }
    registrarSituacao(pedido, nova);
    return comTotais(pedido);
  },
};

/* ------------------------------ PAGAMENTOS ------------------------------ */

const pagamentos = {
  processar(pedidoId, body) {
    const pedido = encontrar('pedidos', pedidoId, 'Pedido');
    if (pedido.situacao === 'PAGO' || pedido.pagamento?.resultado === 'APROVADO')
      throw new ApiError(409, 'Este pedido já foi pago anteriormente.');
    if (pedido.situacao === 'CANCELADO')
      throw new ApiError(409, 'Não é possível pagar um pedido cancelado.');

    const { forma, dados = {} } = body;
    let resultado;
    let descricao;
    let mensagem;

    if (forma === 'PIX') {
      resultado = 'APROVADO';
      descricao = `Pix chave: ${dados.chave}`;
      mensagem = 'Cobrança Pix confirmada.';
    } else if (forma === 'BOLETO') {
      // Boleto.processar() devolve false: o pagamento só se confirma na compensação
      resultado = 'PENDENTE';
      descricao = `Boleto bancário com vencimento em ${dados.vencimento}`;
      mensagem = 'Boleto gerado. O pedido será pago após a compensação (até 3 dias úteis).';
    } else if (forma === 'CARTAO') {
      const numero = digitos(dados.numero);
      if (Number(dados.parcelas) > 12)
        throw new ApiError(400, 'Cartão aceita no máximo 12 parcelas');
      // Simulação: cartões terminados em 0000 são recusados pela "operadora"
      const recusado = numero.endsWith('0000');
      resultado = recusado ? 'RECUSADO' : 'APROVADO';
      descricao = `Cartão final ${numero.slice(-4)} (${dados.parcelas}x)`;
      mensagem = recusado
        ? 'Pagamento por CartaoCredito recusado: Saldo/Transação recusada pela operadora.'
        : 'Pagamento autorizado pela operadora.';
    } else {
      throw new ApiError(400, 'Forma de pagamento inválida.');
    }

    const comprovante = `${forma === 'CARTAO' ? 'CARD' : forma === 'PIX' ? 'PIX' : 'BOL'}-${Date.now()}`;
    pedido.pagamento = { forma, resultado, comprovante, descricao };
    if (resultado === 'APROVADO') registrarSituacao(pedido, 'PAGO');

    return { pedidoId: pedido.id, forma, resultado, comprovante, descricao, mensagem };
  },
  buscar(pedidoId) {
    const pedido = encontrar('pedidos', pedidoId, 'Pedido');
    if (!pedido.pagamento)
      throw new ApiError(404, `O pedido ${pedido.id} ainda não tem pagamento registrado.`);
    return { pedidoId: pedido.id, ...pedido.pagamento };
  },
};

/* -------------------------------- ROTEADOR ------------------------------- */

const rotas = [
  ['GET', /^\/produtos$/, () => produtos.listar()],
  ['POST', /^\/produtos$/, (_, b) => produtos.criar(b)],
  ['GET', /^\/produtos\/(\d+)$/, ([id]) => produtos.buscar(id)],
  ['PUT', /^\/produtos\/(\d+)$/, ([id], b) => produtos.atualizar(id, b)],
  ['DELETE', /^\/produtos\/(\d+)$/, ([id]) => produtos.excluir(id)],
  ['GET', /^\/clientes$/, () => clientes.listar()],
  ['POST', /^\/clientes$/, (_, b) => clientes.criar(b)],
  ['GET', /^\/clientes\/(\d+)$/, ([id]) => clientes.buscar(id)],
  ['PUT', /^\/clientes\/(\d+)$/, ([id], b) => clientes.atualizar(id, b)],
  ['GET', /^\/pedidos$/, () => pedidos.listar()],
  ['POST', /^\/pedidos$/, (_, b) => pedidos.criar(b)],
  ['GET', /^\/pedidos\/(\d+)$/, ([id]) => pedidos.buscar(id)],
  ['PATCH', /^\/pedidos\/(\d+)$/, ([id], b) => pedidos.alterarSituacao(id, b)],
  ['POST', /^\/pedidos\/(\d+)\/pagamento$/, ([id], b) => pagamentos.processar(id, b)],
  ['GET', /^\/pedidos\/(\d+)\/pagamento$/, ([id]) => pagamentos.buscar(id)],
];

const esperar = (ms) => new Promise((r) => setTimeout(r, ms));

export function criarMockClient({ atraso = 0 } = {}) {
  async function request(method, path, body) {
    if (atraso) await esperar(atraso);
    const caminho = path.split('?')[0];
    const rota = rotas.find(([m, re]) => m === method && re.test(caminho));
    if (!rota) throw new ApiError(404, `Rota ${method} ${path} não existe na API simulada.`);
    const params = caminho.match(rota[1]).slice(1);
    const resultado = rota[2](params, body ? clonar(body) : undefined);
    salvar();
    return resultado === null ? null : clonar(resultado); // devolve cópia, como um JSON de verdade
  }

  return {
    get: (p) => request('GET', p),
    post: (p, b) => request('POST', p, b),
    put: (p, b) => request('PUT', p, b),
    patch: (p, b) => request('PATCH', p, b),
    delete: (p) => request('DELETE', p),
  };
}
