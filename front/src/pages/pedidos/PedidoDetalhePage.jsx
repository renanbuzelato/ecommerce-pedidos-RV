import { useState } from 'react';
import { useParams } from 'react-router-dom';
import { ResumoValores } from '../../components/pedidos/ResumoValores';
import { StatusTimeline } from '../../components/pedidos/StatusTimeline';
import {
  Button,
  ButtonLink,
  Card,
  ConfirmDialog,
  DataTable,
  ErrorState,
  Loading,
  PageHeader,
  StatusBadge,
  Badge,
} from '../../components/ui';
import { useToast } from '../../contexts/ToastContext';
import { useAsync } from '../../hooks/useAsync';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { pedidoService } from '../../services/pedidoService';
import {
  FORMAS_PAGAMENTO,
  RESULTADOS_PAGAMENTO,
  SITUACOES,
  TRANSICOES,
} from '../../utils/constants';
import { formatarDataHora, formatarMoeda } from '../../utils/formatters';
import NotFoundPage from '../NotFoundPage';

const rotuloAcao = {
  ENVIADO: 'Marcar como enviado',
  ENTREGUE: 'Marcar como entregue',
  CANCELADO: 'Cancelar pedido',
};

export default function PedidoDetalhePage() {
  const { id } = useParams();
  useDocumentTitle(`Pedido #${id}`);
  const {
    data: pedido,
    loading,
    error,
    reload,
    setData,
  } = useAsync(() => pedidoService.buscar(id), [id]);
  const toast = useToast();
  const [alterando, setAlterando] = useState(null);
  const [confirmarCancelamento, setConfirmarCancelamento] = useState(false);

  if (loading && !pedido) return <Loading texto="Carregando pedido..." />;
  if (error?.status === 404) return <NotFoundPage mensagem={`O pedido ${id} não existe.`} />;
  if (error) return <ErrorState erro={error} aoTentarNovamente={reload} />;

  async function alterarSituacao(situacao) {
    setAlterando(situacao);
    try {
      const atualizado = await pedidoService.alterarSituacao(pedido.id, situacao);
      setData(atualizado);
      toast.sucesso(`Pedido #${pedido.id} agora está: ${SITUACOES[situacao].rotulo}.`);
    } catch (err) {
      toast.erro(err.message);
    } finally {
      setAlterando(null);
      setConfirmarCancelamento(false);
    }
  }

  const boletoPendente =
    pedido.pagamento?.forma === 'BOLETO' && pedido.pagamento?.resultado === 'PENDENTE';
  const acoes = TRANSICOES[pedido.situacao] ?? [];
  const e = pedido.enderecoEntrega;

  return (
    <>
      <PageHeader
        titulo={`Pedido #${pedido.id}`}
        subtitulo={`Criado em ${formatarDataHora(pedido.dataCriacao)} · Cliente: ${pedido.cliente.nome}`}
        acoes={
          <ButtonLink to="/pedidos" variante="secundario">
            ← Todos os pedidos
          </ButtonLink>
        }
      />

      <Card as="section" aria-labelledby="titulo-status" className="mb-6 p-6">
        <div className="mb-6 flex flex-wrap items-center justify-between gap-3">
          <h2 id="titulo-status" className="text-lg font-bold">
            Situação: <StatusBadge situacao={pedido.situacao} />
          </h2>
          <div className="flex flex-wrap gap-2">
            {boletoPendente && pedido.situacao === 'ABERTO' && (
              <Button
                tamanho="sm"
                carregando={alterando === 'PAGO'}
                onClick={() => alterarSituacao('PAGO')}
              >
                Confirmar compensação do boleto
              </Button>
            )}
            {acoes.map((s) =>
              s === 'CANCELADO' ? (
                <Button
                  key={s}
                  variante="perigo"
                  tamanho="sm"
                  onClick={() => setConfirmarCancelamento(true)}
                >
                  {rotuloAcao[s]}
                </Button>
              ) : (
                <Button
                  key={s}
                  tamanho="sm"
                  carregando={alterando === s}
                  onClick={() => alterarSituacao(s)}
                >
                  {rotuloAcao[s]}
                </Button>
              ),
            )}
          </div>
        </div>
        <StatusTimeline situacao={pedido.situacao} historico={pedido.historico} />
      </Card>

      <div className="grid gap-6 lg:grid-cols-3">
        <section aria-labelledby="titulo-itens" className="space-y-3 lg:col-span-2">
          <h2 id="titulo-itens" className="text-lg font-bold">
            Itens
          </h2>
          <DataTable
            legenda={`Itens do pedido ${pedido.id}`}
            linhas={pedido.itens}
            colunas={[
              { chave: 'nome', titulo: 'Produto' },
              { chave: 'quantidade', titulo: 'Qtd.', className: 'text-center' },
              {
                chave: 'precoUnitario',
                titulo: 'Preço unit.',
                render: (i) => formatarMoeda(i.precoUnitario),
                className: 'text-right',
              },
              {
                chave: 'precoTotal',
                titulo: 'Total',
                render: (i) => formatarMoeda(i.precoTotal),
                className: 'text-right',
              },
            ]}
          />
          <p className="text-xs text-slate-600">
            O preço unitário fica congelado no momento da compra.
          </p>
        </section>

        <div className="space-y-6">
          <Card as="section" aria-labelledby="titulo-valores" className="p-6">
            <h2 id="titulo-valores" className="mb-3 text-lg font-bold">
              Valores
            </h2>
            <ResumoValores subtotal={pedido.subtotal} frete={pedido.frete} total={pedido.total} />
          </Card>

          <Card as="section" aria-labelledby="titulo-pagamento" className="p-6">
            <h2 id="titulo-pagamento" className="mb-3 text-lg font-bold">
              Pagamento
            </h2>
            {pedido.pagamento ? (
              <dl className="space-y-1 text-sm">
                <dt className="sr-only">Forma</dt>
                <dd className="font-semibold">{FORMAS_PAGAMENTO[pedido.pagamento.forma]}</dd>
                <dt className="sr-only">Descrição</dt>
                <dd>{pedido.pagamento.descricao}</dd>
                <dt className="sr-only">Resultado</dt>
                <dd>
                  <Badge cor={RESULTADOS_PAGAMENTO[pedido.pagamento.resultado].cor}>
                    {RESULTADOS_PAGAMENTO[pedido.pagamento.resultado].rotulo}
                  </Badge>
                </dd>
                <dt className="sr-only">Comprovante</dt>
                <dd className="font-mono text-xs text-slate-600">{pedido.pagamento.comprovante}</dd>
              </dl>
            ) : (
              <p className="text-sm text-slate-600">Nenhum pagamento registrado.</p>
            )}
          </Card>

          {e && (
            <Card as="section" aria-labelledby="titulo-entrega" className="p-6">
              <h2 id="titulo-entrega" className="mb-3 text-lg font-bold">
                Entrega
              </h2>
              <address className="text-sm not-italic text-slate-700">
                {e.logradouro}, {e.numero} {e.complemento && `- ${e.complemento}`}
                <br />
                {e.bairro} — {e.cidade}/{e.uf}
                <br />
                CEP {e.cep}
              </address>
            </Card>
          )}
        </div>
      </div>

      <ConfirmDialog
        aberto={confirmarCancelamento}
        titulo={`Cancelar o pedido #${pedido.id}?`}
        mensagem="Os itens voltam para o estoque e esta ação não pode ser desfeita."
        textoConfirmar="Sim, cancelar pedido"
        carregando={alterando === 'CANCELADO'}
        aoConfirmar={() => alterarSituacao('CANCELADO')}
        aoCancelar={() => setConfirmarCancelamento(false)}
      />
    </>
  );
}
