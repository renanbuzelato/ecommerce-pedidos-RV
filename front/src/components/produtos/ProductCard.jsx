import { Link } from 'react-router-dom';
import { Button, Card } from '../ui';
import { ProductImage } from './ProductImage';
import { StockIndicator } from './StockIndicator';
import { formatarMoeda } from '../../utils/formatters';

/**
 * Cartão do catálogo.
 * @param {object} produto
 * @param {(produto) => void} aoAdicionar  callback do botão "Adicionar ao carrinho"
 */
export function ProductCard({ produto, aoAdicionar }) {
  const disponivel = produto.ativo && produto.estoque > 0;

  return (
    <Card
      as="article"
      className="flex w-full flex-col overflow-hidden transition-shadow hover:shadow-md"
    >
      <ProductImage produto={produto} />
      <div className="flex flex-1 flex-col gap-2 p-4">
        <p className="text-xs font-semibold uppercase tracking-wide text-marca-700">
          {produto.categoria}
        </p>
        <h3 className="text-base font-semibold leading-snug text-slate-900">
          <Link to={`/produtos/${produto.id}`} className="hover:underline">
            {produto.nome}
          </Link>
        </h3>
        <StockIndicator estoque={produto.estoque} ativo={produto.ativo} />
        <p className="mt-auto pt-2 text-xl font-bold text-slate-900">
          {formatarMoeda(produto.preco)}
        </p>
        <Button onClick={() => aoAdicionar(produto)} disabled={!disponivel} larguraTotal>
          {disponivel ? 'Adicionar ao carrinho' : 'Indisponível'}
          {/* nome do produto só para leitor de tela; o texto visível vem primeiro (WCAG 2.5.3) */}
          <span className="sr-only">: {produto.nome}</span>
        </Button>
      </div>
    </Card>
  );
}
