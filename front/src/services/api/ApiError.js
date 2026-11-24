/** Erro padronizado da camada de serviços: as telas só conhecem esta classe. */
export class ApiError extends Error {
  constructor(status, mensagem, detalhes = null) {
    super(mensagem);
    this.name = 'ApiError';
    this.status = status;
    this.detalhes = detalhes;
  }
}
