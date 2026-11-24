import { useId } from 'react';

const classeCampo = (erro) =>
  `mt-1 block w-full rounded-lg border bg-white px-3 py-2 text-slate-900 shadow-sm placeholder:text-slate-500 ${
    erro ? 'border-red-600' : 'border-slate-400'
  }`;

/**
 * Envolve qualquer campo com <label>, dica e mensagem de erro ligadas por
 * aria-describedby, e marca aria-invalid quando há erro (WCAG 1.3.1 / 3.3.1).
 */
function Campo({ label, erro, dica, obrigatorio, className = '', children }) {
  const id = useId();
  const idDica = dica ? `${id}-dica` : undefined;
  const idErro = erro ? `${id}-erro` : undefined;
  const descritoPor = [idDica, idErro].filter(Boolean).join(' ') || undefined;

  return (
    <div className={className}>
      <label htmlFor={id} className="block text-sm font-medium text-slate-800">
        {label}
        {obrigatorio && (
          <span className="text-red-700" aria-hidden="true">
            {' '}
            *
          </span>
        )}
      </label>
      {children({
        id,
        'aria-describedby': descritoPor,
        'aria-invalid': erro ? true : undefined,
        required: obrigatorio,
      })}
      {dica && (
        <p id={idDica} className="mt-1 text-xs text-slate-600">
          {dica}
        </p>
      )}
      {erro && (
        <p id={idErro} className="mt-1 text-sm font-medium text-red-700">
          {erro}
        </p>
      )}
    </div>
  );
}

export function Input({ label, erro, dica, obrigatorio, className, ...props }) {
  return (
    <Campo label={label} erro={erro} dica={dica} obrigatorio={obrigatorio} className={className}>
      {(a11y) => <input className={classeCampo(erro)} {...a11y} {...props} />}
    </Campo>
  );
}

export function Select({
  label,
  erro,
  dica,
  obrigatorio,
  className,
  opcoes = [],
  placeholder,
  ...props
}) {
  return (
    <Campo label={label} erro={erro} dica={dica} obrigatorio={obrigatorio} className={className}>
      {(a11y) => (
        <select className={classeCampo(erro)} {...a11y} {...props}>
          {placeholder && <option value="">{placeholder}</option>}
          {opcoes.map((o) => {
            const { valor, rotulo } = typeof o === 'object' ? o : { valor: o, rotulo: o };
            return (
              <option key={valor} value={valor}>
                {rotulo}
              </option>
            );
          })}
        </select>
      )}
    </Campo>
  );
}

export function Textarea({ label, erro, dica, obrigatorio, className, ...props }) {
  return (
    <Campo label={label} erro={erro} dica={dica} obrigatorio={obrigatorio} className={className}>
      {(a11y) => <textarea rows={3} className={classeCampo(erro)} {...a11y} {...props} />}
    </Campo>
  );
}
