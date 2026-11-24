import { SITUACOES } from '../../utils/constants';

// Classes completas (o Tailwind só gera classes que aparecem literalmente no código)
const cores = {
  slate: 'bg-slate-100 text-slate-800 ring-slate-300',
  amber: 'bg-amber-100 text-amber-900 ring-amber-300',
  blue: 'bg-blue-100 text-blue-900 ring-blue-300',
  indigo: 'bg-indigo-100 text-indigo-900 ring-indigo-300',
  green: 'bg-green-100 text-green-900 ring-green-300',
  red: 'bg-red-100 text-red-900 ring-red-300',
};

export function Badge({ cor = 'slate', children, className = '' }) {
  return (
    <span
      className={`inline-flex w-fit items-center rounded-full px-2.5 py-0.5 text-xs font-semibold ring-1 ring-inset ${cores[cor]} ${className}`}
    >
      {children}
    </span>
  );
}

/** Situação do pedido: cor + TEXTO (cor sozinha não pode carregar a informação, WCAG 1.4.1). */
export function StatusBadge({ situacao }) {
  const info = SITUACOES[situacao] ?? { rotulo: situacao, cor: 'slate' };
  return <Badge cor={info.cor}>{info.rotulo}</Badge>;
}
