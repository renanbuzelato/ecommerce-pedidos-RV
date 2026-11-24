import { criarHttpClient } from './httpClient';
import { criarMockClient } from './mockServer';

/**
 * PONTO ÚNICO DE TROCA entre API simulada e API real.
 * - VITE_API_URL vazio  -> API simulada (mockServer, sem servidor)
 * - VITE_API_URL=http://localhost:8080/api -> back-end Spring Boot
 * Nenhuma tela nem serviço precisa mudar: todos usam este `api`.
 */
const API_URL = import.meta.env.VITE_API_URL?.trim();
const MODO_TESTE = import.meta.env.MODE === 'test';

export const usandoApiSimulada = !API_URL;

export const api = usandoApiSimulada
  ? criarMockClient({ atraso: MODO_TESTE ? 0 : Number(import.meta.env.VITE_MOCK_DELAY ?? 400) })
  : criarHttpClient(API_URL);
