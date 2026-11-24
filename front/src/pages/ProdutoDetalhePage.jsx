import { useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { ProductImage } from '../components/produtos/ProductImage';
import { StockIndicator } from '../components/produtos/StockIndicator';
import { Button, ButtonLink, Card, ErrorState, Loading } from '../components/ui';
import { useCart } from '../contexts/CartContext';
import { useToast } from '../contexts/ToastContext';
import { useAsync } from '../hooks/useAsync';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { produtoService } from '../services/produtoService';
import { formatarMoeda } from '../utils/formatters';
import NotFoundPage from './NotFoundPage';

export default function ProdutoDetalhePage() {
  const { id } = useParams(); // rota dinâmica /produtos/:id
  const { data: produto, loading, error, reload } = useAsync(() => produtoService.buscar(id), [id]);
  const { adicionar, itens } = useCart();
  const toast = useToast();
  const [quantidade, setQuantidade] = useState(1);
  useDocumentTitle(produto?.nome ?? 'Produto');

  if (loading) return <Loading texto="Carregando produto..." />;
  if (error?.status === 404) return <NotFoundPage mensagem={`O produto ${id} não existe.`} />;
  if (error) return <ErrorState erro={error} aoTentarNovamente={reload} />;

  const noCarrinho = itens.find((i) => i.id === produto.id)?.quantidade ?? 0;
  const maximo = Math.max(0, produto.estoque - noCarrinho);
  const disponivel = produto.ativo && maximo > 0;

  function aoAdicionar(e) {
    e.preventDefault();
    adicionar(produto, quantidade);
    toast.sucesso(`${quantidade}× ${produto.nome} adicionado ao carrinho.`);
    setQuantidade(1);
  }

  return (
    <>
      <nav aria-label="Trilha de navegação" className="mb-4 text-sm">
        <ol className="flex flex-wrap gap-1 text-slate-600">
          <li>
            <Link to="/" className="text-marca-700 underline">
              Catálogo
            </Link>
            <span aria-hidden="true"> / </span>
          </li>
          <li>
            <Link
              to={`/?categoria=${encodeURIComponent(produto.categoria)}`}
              className="text-marca-700 underline"
            >
              {produto.categoria}
            </Link>
            <span aria-hidden="true"> / </span>
          </li>
          <li aria-current="page">{produto.nome}</li>
        </ol>
      </nav>

      <Card as="article" className="grid overflow-hidden md:grid-cols-2">
        <ProductImage produto={produto} className="h-full" />
        <div className="flex flex-col gap-4 p-6 md:p-8">
          <p className="text-sm font-semibold uppercase tracking-wide text-marca-700">
            {produto.categoria}
          </p>
          <h1 className="text-2xl font-bold text-slate-900 sm:text-3xl">{produto.nome}</h1>
          <p className="text-slate-700">{produto.descricao || 'Sem descrição.'}</p>
          <div>
            <StockIndicator estoque={produto.estoque} ativo={produto.ativo} />
            {noCarrinho > 0 && (
              <p className="mt-2 text-sm text-slate-600">Você já tem {noCarrinho} no carrinho.</p>
            )}
          </div>
          <p className="text-3xl font-extrabold text-slate-900">{formatarMoeda(produto.preco)}</p>

          {disponivel ? (
            <form onSubmit={aoAdicionar} className="flex flex-col gap-3 sm:flex-row sm:items-end">
              <div>
                <label htmlFor="quantidade" className="block text-sm font-medium text-slate-800">
                  Quantidade
                </label>
                <input
                  id="quantidade"
                  type="number"
                  min="1"
                  max={maximo}
                  value={quantidade}
                  onChange={(e) =>
                    setQuantidade(Math.max(1, Math.min(maximo, Number(e.target.value) || 1)))
                  }
                  className="mt-1 w-28 rounded-lg border border-slate-400 px-3 py-2"
                  aria-describedby="quantidade-max"
                />
                <p id="quantidade-max" className="mt-1 text-xs text-slate-600">
                  Máximo disponível: {maximo}
                </p>
              </div>
              <Button type="submit" tamanho="lg">
                Adicionar ao carrinho
              </Button>
            </form>
          ) : (
            <p className="font-medium text-red-800">
              {produto.ativo
                ? 'Sem estoque disponível no momento.'
                : 'Este produto não está mais à venda.'}
            </p>
          )}
          <ButtonLink to="/" variante="fantasma" className="self-start">
            ← Voltar ao catálogo
          </ButtonLink>
        </div>
      </Card>
    </>
  );
}
