import { Link } from 'react-router-dom';
import { ProductImage } from '../components/produtos/ProductImage';
import { ResumoValores } from '../components/pedidos/ResumoValores';
import { Button, ButtonLink, Card, EmptyState, PageHeader } from '../components/ui';
import { useCart } from '../contexts/CartContext';
import { useDocumentTitle } from '../hooks/useDocumentTitle';
import { formatarMoeda } from '../utils/formatters';

function SeletorQuantidade({ item, aoAlterar }) {
  return (
    <div className="flex items-center gap-1" role="group" aria-label={`Quantidade de ${item.nome}`}>
      <Button
        variante="secundario"
        tamanho="sm"
        onClick={() => aoAlterar(item.quantidade - 1)}
        disabled={item.quantidade <= 1}
        aria-label={`Diminuir quantidade de ${item.nome}`}
      >
        −
      </Button>
      <label htmlFor={`qtd-${item.id}`} className="sr-only">
        Quantidade de {item.nome}
      </label>
      <input
        id={`qtd-${item.id}`}
        type="number"
        min="1"
        max={item.estoque}
        value={item.quantidade}
        onChange={(e) => aoAlterar(Number(e.target.value) || 1)}
        className="w-14 rounded-lg border border-slate-400 px-2 py-1 text-center"
      />
      <Button
        variante="secundario"
        tamanho="sm"
        onClick={() => aoAlterar(item.quantidade + 1)}
        disabled={item.quantidade >= item.estoque}
        aria-label={`Aumentar quantidade de ${item.nome}`}
      >
        +
      </Button>
    </div>
  );
}

export default function CarrinhoPage() {
  useDocumentTitle('Carrinho');
  const { itens, subtotal, frete, total, alterarQuantidade, remover, limpar, quantidadeTotal } =
    useCart();

  if (itens.length === 0) {
    return (
      <>
        <PageHeader titulo="Carrinho" />
        <EmptyState
          titulo="Seu carrinho está vazio"
          acao={<ButtonLink to="/">Ver catálogo</ButtonLink>}
        >
          Adicione produtos do catálogo para continuar.
        </EmptyState>
      </>
    );
  }

  return (
    <>
      <PageHeader
        titulo="Carrinho"
        subtitulo={`${quantidadeTotal} ${quantidadeTotal === 1 ? 'item' : 'itens'} no carrinho`}
        acoes={
          <Button variante="fantasma" onClick={limpar}>
            Esvaziar carrinho
          </Button>
        }
      />
      <div className="grid gap-6 lg:grid-cols-3">
        <section aria-labelledby="titulo-itens" className="lg:col-span-2">
          <h2 id="titulo-itens" className="sr-only">
            Itens do carrinho
          </h2>
          <ul className="space-y-3">
            {itens.map((item) => (
              <li key={item.id}>
                <Card className="flex flex-col gap-4 p-4 sm:flex-row sm:items-center">
                  <ProductImage produto={item} className="w-full rounded-lg sm:w-28" />
                  <div className="flex-1">
                    <Link
                      to={`/produtos/${item.id}`}
                      className="font-semibold text-slate-900 hover:underline"
                    >
                      {item.nome}
                    </Link>
                    <p className="text-sm text-slate-600">{formatarMoeda(item.preco)} cada</p>
                    {item.quantidade >= item.estoque && (
                      <p className="text-xs font-medium text-amber-800">
                        Quantidade máxima em estoque.
                      </p>
                    )}
                  </div>
                  <SeletorQuantidade item={item} aoAlterar={(q) => alterarQuantidade(item.id, q)} />
                  <p className="w-28 font-bold text-slate-900 sm:text-right">
                    {formatarMoeda(item.preco * item.quantidade)}
                  </p>
                  <Button
                    variante="fantasma"
                    tamanho="sm"
                    onClick={() => remover(item.id)}
                    aria-label={`Remover ${item.nome} do carrinho`}
                  >
                    Remover
                  </Button>
                </Card>
              </li>
            ))}
          </ul>
        </section>

        <aside aria-labelledby="titulo-resumo">
          <Card className="sticky top-24 space-y-4 p-6">
            <h2 id="titulo-resumo" className="text-lg font-bold">
              Resumo
            </h2>
            {/* aria-live: o leitor de tela anuncia o novo total quando a quantidade muda */}
            <div aria-live="polite">
              <ResumoValores subtotal={subtotal} frete={frete} total={total} mostrarDicaFrete />
            </div>
            <ButtonLink to="/checkout" larguraTotal tamanho="lg">
              Finalizar compra
            </ButtonLink>
            <ButtonLink to="/" variante="secundario" larguraTotal>
              Continuar comprando
            </ButtonLink>
          </Card>
        </aside>
      </div>
    </>
  );
}
