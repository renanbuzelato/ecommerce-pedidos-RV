import { useState } from 'react';
import {
  ButtonLink,
  Button,
  ConfirmDialog,
  DataTable,
  EmptyState,
  ErrorState,
  Input,
  Loading,
  PageHeader,
} from '../../components/ui';
import { StockIndicator } from '../../components/produtos/StockIndicator';
import { useToast } from '../../contexts/ToastContext';
import { useAsync } from '../../hooks/useAsync';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { filtrarProdutos, produtoService } from '../../services/produtoService';
import { formatarMoeda } from '../../utils/formatters';

export default function ProdutosAdminPage() {
  useDocumentTitle('Gestão de produtos');
  const { data: produtos, loading, error, reload } = useAsync(() => produtoService.listar(), []);
  const toast = useToast();
  const [busca, setBusca] = useState('');
  const [excluindo, setExcluindo] = useState(null); // produto aguardando confirmação
  const [processando, setProcessando] = useState(false);

  async function confirmarExclusao() {
    setProcessando(true);
    try {
      await produtoService.excluir(excluindo.id);
      toast.sucesso(`Produto "${excluindo.nome}" excluído.`);
      setExcluindo(null);
      reload();
    } catch (err) {
      toast.erro(err.message);
      setExcluindo(null);
    } finally {
      setProcessando(false);
    }
  }

  const colunas = [
    { chave: 'id', titulo: 'ID' },
    {
      chave: 'nome',
      titulo: 'Produto',
      render: (p) => (
        <>
          <span className="font-semibold">{p.nome}</span>
          {!p.ativo && <span className="ml-2 text-xs text-slate-600">(inativo)</span>}
        </>
      ),
    },
    { chave: 'categoria', titulo: 'Categoria' },
    {
      chave: 'preco',
      titulo: 'Preço',
      render: (p) => formatarMoeda(p.preco),
      className: 'text-right whitespace-nowrap',
    },
    {
      chave: 'estoque',
      titulo: 'Estoque',
      render: (p) => <StockIndicator estoque={p.estoque} ativo={p.ativo} />,
    },
    {
      chave: 'acoes',
      titulo: 'Ações',
      render: (p) => (
        <div className="flex gap-2">
          <ButtonLink
            to={`/admin/produtos/${p.id}/editar`}
            variante="secundario"
            tamanho="sm"
            aria-label={`Editar ${p.nome}`}
          >
            Editar
          </ButtonLink>
          <Button
            variante="perigo"
            tamanho="sm"
            onClick={() => setExcluindo(p)}
            aria-label={`Excluir ${p.nome}`}
          >
            Excluir
          </Button>
        </div>
      ),
    },
  ];

  const visiveis = filtrarProdutos(produtos ?? [], { busca });

  return (
    <>
      <PageHeader
        titulo="Gestão de produtos"
        subtitulo="Área administrativa: cadastre, edite e exclua produtos."
        acoes={<ButtonLink to="/admin/produtos/novo">+ Novo produto</ButtonLink>}
      />
      <div className="mb-4 max-w-sm">
        <Input
          label="Filtrar por nome"
          type="search"
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />
      </div>
      {loading && <Loading texto="Carregando produtos..." />}
      {error && <ErrorState erro={error} aoTentarNovamente={reload} />}
      {produtos &&
        (visiveis.length ? (
          <DataTable
            colunas={colunas}
            linhas={visiveis}
            legenda={`Produtos cadastrados (${visiveis.length})`}
          />
        ) : (
          <EmptyState titulo="Nenhum produto encontrado" />
        ))}

      <ConfirmDialog
        aberto={Boolean(excluindo)}
        titulo="Excluir produto?"
        mensagem={`Tem certeza que deseja excluir "${excluindo?.nome}"? Esta ação não pode ser desfeita.`}
        textoConfirmar="Sim, excluir"
        carregando={processando}
        aoConfirmar={confirmarExclusao}
        aoCancelar={() => setExcluindo(null)}
      />
    </>
  );
}
