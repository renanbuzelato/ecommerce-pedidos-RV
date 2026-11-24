import { api } from './api/client';
import { produtoDaApi, produtoParaApi } from './mappers';

export const produtoService = {
  async listar() {
    const lista = await api.get('/produtos');
    return lista.map(produtoDaApi);
  },
  async buscar(id) {
    return produtoDaApi(await api.get(`/produtos/${id}`));
  },
  async criar(produto) {
    return produtoDaApi(await api.post('/produtos', produtoParaApi(produto)));
  },
  async atualizar(id, produto) {
    return produtoDaApi(await api.put(`/produtos/${id}`, produtoParaApi(produto)));
  },
  excluir(id) {
    return api.delete(`/produtos/${id}`);
  },
};

/** Filtros do catálogo: ficam no front porque a API ainda não recebe parâmetros. */
export function filtrarProdutos(
  produtos,
  { busca = '', categoria = '', precoMin = '', precoMax = '' },
) {
  const termo = busca.trim().toLowerCase();
  return produtos.filter(
    (p) =>
      (!termo || p.nome.toLowerCase().includes(termo)) &&
      (!categoria || p.categoria === categoria) &&
      (precoMin === '' || p.preco >= Number(precoMin)) &&
      (precoMax === '' || p.preco <= Number(precoMax)),
  );
}
