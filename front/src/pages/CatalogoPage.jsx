import { useMemo } from 'react';
import { useSearchParams } from 'react-router-dom';
import { ProductCard } from '../components/produtos/ProductCard';
import { ProductFilters } from '../components/produtos/ProductFilters';
import { EmptyState, ErrorState, Loading, PageHeader } from '../components/ui';
import { useCart } from '../contexts/CartContext';
import { useToast } from '../contexts/ToastContext';
import { useAsync } from '../hooks/useAsync';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { filtrarProdutos, produtoService } from '../services/produtoService';

const CAMPOS = ['busca', 'categoria', 'precoMin', 'precoMax'];

export default function CatalogoPage() {
  useDocumentTitle('Catálogo');
  const { data: produtos, loading, error, reload } = useAsync(() => produtoService.listar(), []);
  const { adicionar } = useCart();
  const toast = useToast();

  // Filtros guardados na URL (?busca=...&categoria=...): dá para compartilhar o link e usar o "voltar"
  const [params, setParams] = useSearchParams();
  const filtros = Object.fromEntries(CAMPOS.map((c) => [c, params.get(c) ?? '']));
  const aoMudarFiltros = (novos) =>
    setParams(Object.fromEntries(Object.entries(novos).filter(([, v]) => v !== '')), {
      replace: true,
    });

  const visiveis = useMemo(
    () =>
      filtrarProdutos(
        (produtos ?? []).filter((p) => p.ativo),
        filtros,
      ),
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [produtos, params],
  );

  function aoAdicionar(produto) {
    adicionar(produto, 1);
    toast.sucesso(`${produto.nome} foi adicionado ao carrinho.`);
  }

  return (
    <>
      <PageHeader
        titulo="Catálogo de produtos"
        subtitulo="Encontre o que precisa e monte seu carrinho."
      />
      <ProductFilters filtros={filtros} aoMudar={aoMudarFiltros} />

      {loading && <Loading texto="Carregando produtos..." />}
      {error && <ErrorState erro={error} aoTentarNovamente={reload} />}

      {produtos && (
        <section aria-labelledby="titulo-resultados" className="mt-6">
          <h2 id="titulo-resultados" className="sr-only">
            Resultados
          </h2>
          <p className="mb-4 text-sm text-slate-600" role="status">
            {visiveis.length}{' '}
            {visiveis.length === 1 ? 'produto encontrado' : 'produtos encontrados'}
          </p>
          {visiveis.length === 0 ? (
            <EmptyState titulo="Nenhum produto encontrado">
              Tente outro termo ou limpe os filtros.
            </EmptyState>
          ) : (
            <ul className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
              {visiveis.map((p) => (
                <li key={p.id} className="flex">
                  <ProductCard produto={p} aoAdicionar={aoAdicionar} />
                </li>
              ))}
            </ul>
          )}
        </section>
      )}
    </>
  );
}
