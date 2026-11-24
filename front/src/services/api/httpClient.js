import { ApiError } from './ApiError';

/**
 * Cliente HTTP para a API REST real (Spring Boot).
 * Mensagem de erro: tenta os formatos mais comuns de corpo de erro
 * ({ mensagem }, { message }, { error }) para casar com o @RestControllerAdvice do back.
 */
export function criarHttpClient(baseUrl) {
  async function request(method, path, body) {
    let resposta;
    try {
      resposta = await fetch(`${baseUrl}${path}`, {
        method,
        headers: body ? { 'Content-Type': 'application/json' } : undefined,
        body: body ? JSON.stringify(body) : undefined,
      });
    } catch {
      throw new ApiError(0, 'Não foi possível conectar à API. Verifique se o back-end está no ar.');
    }

    const texto = await resposta.text();
    const dados = texto ? JSON.parse(texto) : null;

    if (!resposta.ok) {
      const mensagem =
        dados?.mensagem || dados?.message || dados?.error || `Erro ${resposta.status} na API.`;
      throw new ApiError(resposta.status, mensagem, dados);
    }
    return dados;
  }

  return {
    get: (path) => request('GET', path),
    post: (path, body) => request('POST', path, body),
    put: (path, body) => request('PUT', path, body),
    patch: (path, body) => request('PATCH', path, body),
    delete: (path) => request('DELETE', path),
  };
}
