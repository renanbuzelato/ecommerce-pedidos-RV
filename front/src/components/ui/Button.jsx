import { Link } from 'react-router-dom';

const base =
  'inline-flex items-center justify-center gap-2 rounded-lg font-semibold transition-colors disabled:cursor-not-allowed disabled:opacity-60';

const variantes = {
  primario: 'bg-marca-700 text-white hover:bg-marca-800',
  secundario: 'border border-slate-300 bg-white text-slate-800 hover:bg-slate-100',
  perigo: 'bg-red-700 text-white hover:bg-red-800',
  fantasma: 'text-marca-700 hover:bg-marca-50',
};

const tamanhos = {
  sm: 'px-3 py-1.5 text-sm',
  md: 'px-4 py-2 text-sm sm:text-base',
  lg: 'px-6 py-3 text-base',
};

const classesBotao = ({ variante = 'primario', tamanho = 'md', larguraTotal = false } = {}) =>
  `${base} ${variantes[variante]} ${tamanhos[tamanho]} ${larguraTotal ? 'w-full' : ''}`;

/**
 * Botão padrão do sistema.
 * @param {'primario'|'secundario'|'perigo'|'fantasma'} variante
 * @param {'sm'|'md'|'lg'} tamanho
 * @param {boolean} carregando  mostra spinner e bloqueia clique duplo
 */
export function Button({
  variante,
  tamanho,
  larguraTotal,
  carregando = false,
  type = 'button',
  children,
  disabled,
  className = '',
  ...props
}) {
  return (
    <button
      type={type}
      disabled={disabled || carregando}
      aria-busy={carregando || undefined}
      className={`${classesBotao({ variante, tamanho, larguraTotal })} ${className}`}
      {...props}
    >
      {carregando && (
        <span
          aria-hidden="true"
          className="h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
        />
      )}
      {children}
    </button>
  );
}

/** Link com aparência de botão (navegação continua sendo <a>, como deve ser). */
export function ButtonLink({
  to,
  variante,
  tamanho,
  larguraTotal,
  className = '',
  children,
  ...props
}) {
  return (
    <Link
      to={to}
      className={`${classesBotao({ variante, tamanho, larguraTotal })} ${className}`}
      {...props}
    >
      {children}
    </Link>
  );
}
