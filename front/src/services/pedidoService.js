import { api } from './api/client';
import { pedidoDaApi } from './mappers';

export const pedidoService = {
  async listar() {
    const lista = (await api.get('/pedidos')).map(pedidoDaApi);
    return lista.sort((a, b) => new Date(b.dataCriacao) - new Date(a.dataCriacao));
  },
  async buscar(id) {
    return pedidoDaApi(await api.get(`/pedidos/${id}`));
  },
  /** Cria o pedido a partir dos itens do carrinho. */
  async criar({ clienteId, enderecoEntrega, itens }) {
    const corpo = {
      clienteId,
      enderecoEntrega,
      itens: itens.map((i) => ({ produtoId: i.id, quantidade: i.quantidade })),
    };
    return pedidoDaApi(await api.post('/pedidos', corpo));
  },
  async alterarSituacao(id, situacao) {
    return pedidoDaApi(await api.patch(`/pedidos/${id}`, { situacao }));
  },
};
