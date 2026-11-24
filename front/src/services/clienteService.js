import { api } from './api/client';
import { clienteDaApi, clienteParaApi } from './mappers';

export const clienteService = {
  async listar() {
    return (await api.get('/clientes')).map(clienteDaApi);
  },
  async buscar(id) {
    return clienteDaApi(await api.get(`/clientes/${id}`));
  },
  async criar(cliente) {
    return clienteDaApi(await api.post('/clientes', clienteParaApi(cliente)));
  },
  async atualizar(id, cliente) {
    return clienteDaApi(await api.put(`/clientes/${id}`, clienteParaApi(cliente)));
  },
};
