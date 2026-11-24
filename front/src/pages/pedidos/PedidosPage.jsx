import { useSearchParams } from 'react-router-dom';
import {
  DataTable,
  EmptyState,
  ErrorState,
  Loading,
  PageHeader,
  StatusBadge,
  ButtonLink,
} from '../../components/ui';
import { useAsync } from '../../hooks/useAsync';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { pedidoService } from '../../services/pedidoService';
import { SITUACOES } from '../../utils/constants';
import { formatarDataHora, formatarMoeda } from '../../utils/formatters';

export default function PedidosPage() {
  useDocumentTitle('Pedidos');
  const { data: pedidos, loading, error, reload } = useAsync(() => pedidoService.listar(), []);
  const [params, setParams] = useSearchParams();
  const filtro = params.get('situacao') ?? '';

  const visiveis = (pedidos ?? []).filter((p) => !filtro || p.situacao === filtro);
  const contar = (s) => (pedidos ?? []).filter((p) => !s || p.situacao === s).length;

  const colunas = [
    {
      chave: 'id',
      titulo: 'Pedido',
      render: (p) => <span className="font-semibold">#{p.id}</span>,
    },
    { chave: 'data', titulo: 'Data', render: (p) => formatarDataHora(p.dataCriacao) },
    { chave: 'cliente', titulo: 'Cliente', render: (p) => p.cliente.nome },
    {
      chave: 'itens',
      titulo: 'Itens',
      render: (p) => p.itens.reduce((s, i) => s + i.quantidade, 0),
    },
    {
      chave: 'total',
      titulo: 'Total',
      render: (p) => formatarMoeda(p.total),
      className: 'text-right',
    },
    { chave: 'situacao', titulo: 'Situação', render: (p) => <StatusBadge situacao={p.situacao} /> },
    {
      chave: 'acoes',
      titulo: 'Ações',
      render: (p) => (
        <ButtonLink
          to={`/pedidos/${p.id}`}
          variante="secundario"
          tamanho="sm"
          aria-label={`Ver detalhes do pedido ${p.id}`}
        >
          Detalhes
        </ButtonLink>
      ),
    },
  ];

  return (
    <>
      <PageHeader titulo="Pedidos" subtitulo="Acompanhe os pedidos e a situação de cada um." />

      <div role="group" aria-label="Filtrar por situação" className="mb-6 flex flex-wrap gap-2">
        {[['', 'Todos'], ...Object.entries(SITUACOES).map(([k, v]) => [k, v.rotulo])].map(
          ([valor, rotulo]) => (
            <button
              key={valor || 'todos'}
              type="button"
              aria-pressed={filtro === valor}
              onClick={() => setParams(valor ? { situacao: valor } : {}, { replace: true })}
              className={`rounded-full border px-4 py-1.5 text-sm font-medium ${
                filtro === valor
                  ? 'border-marca-700 bg-marca-700 text-white'
                  : 'border-slate-300 bg-white text-slate-800 hover:bg-slate-100'
              }`}
            >
              {rotulo} {pedidos && <span>({contar(valor)})</span>}
            </button>
          ),
        )}
      </div>

      {loading && <Loading texto="Carregando pedidos..." />}
      {error && <ErrorState erro={error} aoTentarNovamente={reload} />}
      {pedidos &&
        (visiveis.length === 0 ? (
          <EmptyState titulo="Nenhum pedido nesta situação" />
        ) : (
          <DataTable
            colunas={colunas}
            linhas={visiveis}
            legenda={`Lista de pedidos (${visiveis.length})`}
          />
        ))}
    </>
  );
}
