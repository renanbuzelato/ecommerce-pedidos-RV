import { Button } from './Button';

/** Indicador de carregamento anunciado ao leitor de tela. */
export function Loading({ texto = 'Carregando...' }) {
  return (
    <div
      role="status"
      className="flex flex-col items-center justify-center gap-3 py-16 text-slate-600"
    >
      <span
        aria-hidden="true"
        className="h-10 w-10 animate-spin rounded-full border-4 border-marca-100 border-t-marca-700"
      />
      <span>{texto}</span>
    </div>
  );
}

const estilosAlerta = {
  erro: 'border-red-300 bg-red-50 text-red-900',
  sucesso: 'border-green-300 bg-green-50 text-green-900',
  aviso: 'border-amber-300 bg-amber-50 text-amber-900',
  info: 'border-blue-300 bg-blue-50 text-blue-900',
};

export function Alert({ tipo = 'info', titulo, children, className = '' }) {
  return (
    <div
      role={tipo === 'erro' ? 'alert' : 'status'}
      className={`rounded-lg border p-4 ${estilosAlerta[tipo]} ${className}`}
    >
      {titulo && <p className="font-semibold">{titulo}</p>}
      {children && <div className="text-sm">{children}</div>}
    </div>
  );
}

/** Erro de carregamento com botão de tentar de novo. */
export function ErrorState({ erro, aoTentarNovamente }) {
  return (
    <Alert tipo="erro" titulo="Não foi possível carregar os dados." className="my-8">
      <p>{erro?.message ?? 'Erro inesperado.'}</p>
      {aoTentarNovamente && (
        <Button variante="secundario" tamanho="sm" className="mt-3" onClick={aoTentarNovamente}>
          Tentar novamente
        </Button>
      )}
    </Alert>
  );
}

export function EmptyState({ titulo, children, acao }) {
  return (
    <div className="rounded-xl border-2 border-dashed border-slate-300 bg-white px-6 py-12 text-center">
      <p className="text-lg font-semibold text-slate-800">{titulo}</p>
      {children && <div className="mt-2 text-slate-600">{children}</div>}
      {acao && <div className="mt-6">{acao}</div>}
    </div>
  );
}
