import { useEffect, useId, useRef } from 'react';
import { createPortal } from 'react-dom';

const SELETOR_FOCAVEL =
  'a[href], button:not([disabled]), input:not([disabled]), select:not([disabled]), textarea:not([disabled]), [tabindex]:not([tabindex="-1"])';

/**
 * Modal acessível:
 *  - role="dialog" + aria-modal + aria-labelledby
 *  - foco vai para dentro ao abrir e VOLTA para quem abriu ao fechar
 *  - Tab/Shift+Tab ficam presos dentro do modal; Esc fecha
 */
export function Modal({ aberto, aoFechar, titulo, children, rodape }) {
  const idTitulo = useId();
  const painel = useRef(null);
  const focoAnterior = useRef(null);
  const aoFecharRef = useRef(aoFechar);

  useEffect(() => {
    aoFecharRef.current = aoFechar;
  });

  useEffect(() => {
    if (!aberto) return undefined;
    focoAnterior.current = document.activeElement;
    const focaveis = painel.current?.querySelectorAll(SELETOR_FOCAVEL);
    (focaveis?.[0] ?? painel.current)?.focus();

    function aoTeclar(e) {
      if (e.key === 'Escape') {
        e.stopPropagation();
        aoFecharRef.current();
        return;
      }
      if (e.key !== 'Tab') return;
      const itens = [...painel.current.querySelectorAll(SELETOR_FOCAVEL)];
      if (!itens.length) return;
      const primeiro = itens[0];
      const ultimo = itens[itens.length - 1];
      if (e.shiftKey && document.activeElement === primeiro) {
        e.preventDefault();
        ultimo.focus();
      } else if (!e.shiftKey && document.activeElement === ultimo) {
        e.preventDefault();
        primeiro.focus();
      }
    }

    document.addEventListener('keydown', aoTeclar);
    document.body.style.overflow = 'hidden';
    return () => {
      document.removeEventListener('keydown', aoTeclar);
      document.body.style.overflow = '';
      focoAnterior.current?.focus?.();
    };
  }, [aberto]);

  if (!aberto) return null;

  return createPortal(
    <div className="fixed inset-0 z-40 flex items-end justify-center p-4 sm:items-center">
      <div className="absolute inset-0 bg-slate-900/60" aria-hidden="true" onClick={aoFechar} />
      <div
        ref={painel}
        role="dialog"
        aria-modal="true"
        aria-labelledby={idTitulo}
        tabIndex={-1}
        className="relative w-full max-w-md rounded-xl bg-white p-6 shadow-xl"
      >
        <h2 id={idTitulo} className="text-lg font-bold text-slate-900">
          {titulo}
        </h2>
        <div className="mt-3 text-slate-700">{children}</div>
        {rodape && (
          <div className="mt-6 flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
            {rodape}
          </div>
        )}
      </div>
    </div>,
    document.body,
  );
}
