import { useCallback, useEffect, useRef, useState } from 'react';

/**
 * Executa uma função assíncrona (normalmente um serviço) e expõe
 * { data, loading, error, reload, setData }.
 *
 * Cada combinação de dependências gera uma "chave"; enquanto a resposta
 * da chave atual não chega, loading = true. Respostas atrasadas de chaves
 * antigas são descartadas (evita condição de corrida ao trocar de rota).
 */
export function useAsync(funcao, deps = []) {
  const [versao, setVersao] = useState(0);
  const [estado, setEstado] = useState({ chave: null, data: null, error: null });
  const funcaoRef = useRef(funcao);
  const chave = JSON.stringify([...deps, versao]);

  useEffect(() => {
    funcaoRef.current = funcao;
  });

  useEffect(() => {
    let ativo = true;
    funcaoRef.current().then(
      (data) => ativo && setEstado({ chave, data, error: null }),
      (error) => ativo && setEstado({ chave, data: null, error }),
    );
    return () => {
      ativo = false;
    };
  }, [chave]);

  const reload = useCallback(() => setVersao((v) => v + 1), []);
  const setData = useCallback((data) => setEstado((s) => ({ ...s, data })), []);
  const loading = estado.chave !== chave;

  return {
    data: loading ? null : estado.data,
    error: loading ? null : estado.error,
    loading,
    reload,
    setData,
  };
}
