/**
 * Tradução entre o formato da API (entidades Java serializadas pelo Jackson)
 * e o modelo usado pelas telas. Se o back mudar um nome de campo,
 * só este arquivo muda.
 *
 *  Back-end (Java)                 Front-end
 *  Produto.quantidadeEmEstoque  -> estoque
 *  Produto.preco (BigDecimal)   -> preco (number)
 *  Cliente.documento (col. cpf) -> cpf
 *  Pedido.situacao              -> situacao (ABERTO exibido como "Pendente")
 */

export const produtoDaApi = (p) => ({
  id: p.id,
  nome: p.nome,
  descricao: p.descricao ?? '',
  categoria: p.categoria ?? 'Geral',
  preco: Number(p.preco),
  estoque: p.quantidadeEmEstoque ?? p.estoque ?? 0,
  ativo: p.ativo ?? true,
  imagem: p.imagem ?? null,
});

export const produtoParaApi = (p) => ({
  nome: p.nome.trim(),
  descricao: p.descricao?.trim() ?? '',
  categoria: p.categoria,
  preco: Number(p.preco).toFixed(2),
  quantidadeEmEstoque: Number(p.estoque),
  ativo: Boolean(p.ativo),
});

// Campos opcionais chegam como null do Java; nos formulários controlados precisam ser ''
const textoOuVazio = (obj) => Object.fromEntries(Object.entries(obj).map(([k, v]) => [k, v ?? '']));

export const clienteDaApi = (c) => ({
  id: c.id,
  nome: c.nome,
  cpf: c.documento ?? c.cpf ?? '',
  email: c.email,
  telefone: c.telefone ?? '',
  enderecos: (c.enderecos ?? []).map((e) => ({ ...textoOuVazio(e), id: e.id })),
});

export const clienteParaApi = (c) => ({
  nome: c.nome.trim(),
  documento: c.cpf,
  email: c.email.trim().toLowerCase(),
  telefone: c.telefone,
  enderecos: c.enderecos.map((e) => ({ ...e })),
});

export const pedidoDaApi = (p) => {
  const itens = (p.itens ?? []).map((i) => ({
    id: i.id,
    produtoId: i.produto?.id ?? i.produtoId,
    nome: i.produto?.nome ?? i.nome,
    quantidade: i.quantidade,
    precoUnitario: Number(i.precoUnitario),
    precoTotal: Number(i.precoUnitario) * i.quantidade,
  }));
  const subtotal =
    p.valorTotal != null ? Number(p.valorTotal) : itens.reduce((s, i) => s + i.precoTotal, 0);
  const frete = Number(p.frete ?? 0);
  return {
    id: p.id,
    cliente: p.cliente ?? { id: p.clienteId, nome: '' },
    enderecoEntrega: p.enderecoEntrega ?? null,
    itens,
    subtotal,
    frete,
    total: p.totalComFrete != null ? Number(p.totalComFrete) : subtotal + frete,
    situacao: p.situacao,
    dataCriacao: p.dataCriacao,
    historico: p.historico ?? [{ situacao: p.situacao, data: p.dataCriacao }],
    pagamento: p.pagamento ?? null,
  };
};
