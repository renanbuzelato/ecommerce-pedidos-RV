import { describe, expect, it, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { ProductCard } from './ProductCard';

const produto = {
  id: 1,
  nome: 'Fone ProSound',
  categoria: 'Eletrônicos',
  preco: 349.9,
  estoque: 3,
  ativo: true,
};
const renderCard = (p, aoAdicionar = vi.fn()) =>
  render(
    <MemoryRouter>
      <ProductCard produto={p} aoAdicionar={aoAdicionar} />
    </MemoryRouter>,
  );

describe('ProductCard', () => {
  it('exibe nome, preço formatado, imagem com alt e aviso de estoque baixo', () => {
    renderCard(produto);
    expect(screen.getByRole('heading', { name: 'Fone ProSound' })).toBeInTheDocument();
    expect(screen.getByText(/R\$\s?349,90/)).toBeInTheDocument();
    expect(screen.getByRole('img')).toHaveAccessibleName(
      'Imagem ilustrativa do produto Fone ProSound',
    );
    expect(screen.getByText('Últimas 3 unidades')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Fone ProSound' })).toHaveAttribute(
      'href',
      '/produtos/1',
    );
  });

  it('chama aoAdicionar com o produto', async () => {
    const aoAdicionar = vi.fn();
    renderCard(produto, aoAdicionar);
    await userEvent.click(
      screen.getByRole('button', { name: 'Adicionar ao carrinho: Fone ProSound' }),
    );
    expect(aoAdicionar).toHaveBeenCalledWith(produto);
  });

  it('desabilita o botão quando esgotado', () => {
    renderCard({ ...produto, estoque: 0 });
    expect(screen.getByText('Esgotado')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /indisponível/i })).toBeDisabled();
  });
});
