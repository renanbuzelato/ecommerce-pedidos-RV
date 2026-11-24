import { FLUXO_SITUACAO, SITUACOES } from '../../utils/constants';
import { formatarDataHora } from '../../utils/formatters';

/**
 * Linha do tempo do pedido: Pendente → Pago → Enviado → Entregue.
 * Se o pedido foi cancelado, mostra as etapas percorridas + "Cancelado".
 * Lista ordenada (<ol>) + aria-current="step" na etapa atual.
 */
export function StatusTimeline({ situacao, historico = [] }) {
  const dataDe = (s) => historico.find((h) => h.situacao === s)?.data;
  const cancelado = situacao === 'CANCELADO';
  const etapas = cancelado
    ? [...FLUXO_SITUACAO.filter((s) => dataDe(s)), 'CANCELADO']
    : FLUXO_SITUACAO;
  const indiceAtual = etapas.indexOf(situacao);

  return (
    <ol className="flex flex-col gap-0 sm:flex-row sm:gap-0" aria-label="Linha do tempo do pedido">
      {etapas.map((etapa, i) => {
        const concluida = i <= indiceAtual;
        const atual = i === indiceAtual;
        const corBola = !concluida
          ? 'border-slate-300 bg-white text-slate-500'
          : etapa === 'CANCELADO'
            ? 'border-red-700 bg-red-700 text-white'
            : 'border-marca-700 bg-marca-700 text-white';
        const estado = atual ? 'etapa atual' : concluida ? 'concluída' : 'pendente';

        return (
          <li
            key={etapa}
            aria-current={atual ? 'step' : undefined}
            className="relative flex flex-1 items-start gap-3 pb-6 sm:flex-col sm:items-center sm:pb-0 sm:text-center"
          >
            {i < etapas.length - 1 && (
              <span
                aria-hidden="true"
                className={`absolute left-4 top-8 h-full w-0.5 sm:left-1/2 sm:top-4 sm:h-0.5 sm:w-full ${
                  i < indiceAtual ? 'bg-marca-700' : 'bg-slate-300'
                }`}
              />
            )}
            <span
              aria-hidden="true"
              className={`relative z-10 grid h-8 w-8 shrink-0 place-items-center rounded-full border-2 text-sm font-bold ${corBola}`}
            >
              {concluida ? (etapa === 'CANCELADO' ? '×' : '✓') : i + 1}
            </span>
            <div className="sm:mt-2">
              <p className={`font-semibold ${concluida ? 'text-slate-900' : 'text-slate-500'}`}>
                {SITUACOES[etapa].rotulo}
                <span className="sr-only"> ({estado})</span>
              </p>
              <p className="text-xs text-slate-600">
                {dataDe(etapa) ? formatarDataHora(dataDe(etapa)) : '—'}
              </p>
            </div>
          </li>
        );
      })}
    </ol>
  );
}
