import { formatarMoeda } from '../../utils/formatters';
import { FRETE_GRATIS_A_PARTIR_DE } from '../../utils/constants';

/** Subtotal, frete e total — reutilizado no carrinho, checkout e detalhe do pedido. */
export function ResumoValores({ subtotal, frete, total, mostrarDicaFrete = false }) {
  const falta = FRETE_GRATIS_A_PARTIR_DE - subtotal;
  return (
    <dl className="space-y-2 text-sm">
      <div className="flex justify-between">
        <dt className="text-slate-600">Subtotal</dt>
        <dd className="font-medium">{formatarMoeda(subtotal)}</dd>
      </div>
      <div className="flex justify-between">
        <dt className="text-slate-600">Frete</dt>
        <dd className="font-medium">{frete === 0 ? 'Grátis' : formatarMoeda(frete)}</dd>
      </div>
      {mostrarDicaFrete && frete > 0 && (
        <p className="rounded-md bg-marca-50 px-3 py-2 text-xs text-marca-800">
          Faltam {formatarMoeda(falta)} para ganhar frete grátis.
        </p>
      )}
      <div className="flex justify-between border-t border-slate-200 pt-2 text-base">
        <dt className="font-semibold">Total</dt>
        <dd className="font-bold text-slate-900">{formatarMoeda(total)}</dd>
      </div>
    </dl>
  );
}
