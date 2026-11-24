import { createContext, useCallback, useContext, useMemo, useState } from 'react';

const ToastContext = createContext(null);
let contador = 0;

const estilos = {
  sucesso: 'border-green-600 bg-green-50 text-green-900',
  erro: 'border-red-600 bg-red-50 text-red-900',
  info: 'border-blue-600 bg-blue-50 text-blue-900',
};

/**
 * Mensagens rápidas de feedback. A região usa aria-live para que o leitor de
 * tela anuncie a mensagem sem tirar o foco do usuário (WCAG 4.1.3).
 */
export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const fechar = useCallback((id) => setToasts((t) => t.filter((x) => x.id !== id)), []);

  const mostrar = useCallback(
    (mensagem, tipo = 'info') => {
      const id = ++contador;
      setToasts((t) => [...t, { id, mensagem, tipo }]);
      setTimeout(() => fechar(id), 5000);
    },
    [fechar],
  );

  const api = useMemo(
    () => ({
      sucesso: (m) => mostrar(m, 'sucesso'),
      erro: (m) => mostrar(m, 'erro'),
      info: (m) => mostrar(m, 'info'),
    }),
    [mostrar],
  );

  return (
    <ToastContext.Provider value={api}>
      {children}
      <div
        aria-live="polite"
        aria-atomic="false"
        className="pointer-events-none fixed inset-x-0 bottom-4 z-50 flex flex-col items-center gap-2 px-4 sm:items-end sm:pr-6"
      >
        {toasts.map((t) => (
          <div
            key={t.id}
            role={t.tipo === 'erro' ? 'alert' : 'status'}
            className={`pointer-events-auto flex w-full max-w-sm items-start gap-3 rounded-lg border-l-4 p-4 shadow-lg ${estilos[t.tipo]}`}
          >
            <p className="flex-1 text-sm font-medium">{t.mensagem}</p>
            <button
              type="button"
              onClick={() => fechar(t.id)}
              className="rounded px-1 text-lg leading-none"
              aria-label="Fechar mensagem"
            >
              ×
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast deve ser usado dentro de <ToastProvider>');
  return ctx;
}
