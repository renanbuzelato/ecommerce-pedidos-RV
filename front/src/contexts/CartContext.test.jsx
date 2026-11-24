import { describe, expect, it } from 'vitest';
import { act, renderHook } from '@testing-library/react';
import { CartProvider, cartReducer, useCart } from './CartContext';

const teclado = { id: 3, nome: 'Teclado', preco: 100, estoque: 3, categoria: 'Informática' };
const livro = { id: 8, nome: 'Livro', preco: 50, estoque: 10, categoria: 'Livros' };

describe('cartReducer', () => {
  it('soma quantidades do mesmo produto sem passar do estoque', () => {
    let estado = cartReducer([], { tipo: 'adicionar', produto: teclado, quantidade: 2 });
    estado = cartReducer(estado, { tipo: 'adicionar', produto: teclado, quantidade: 5 });
    expect(estado).toHaveLength(1);
    expect(estado[0].quantidade).toBe(3);
  });

  it('não deixa a quantidade ficar abaixo de 1', () => {
    const estado = cartReducer([{ ...teclado, quantidade: 2 }], {
      tipo: 'alterarQuantidade',
      id: 3,
      quantidade: 0,
    });
    expect(estado[0].quantidade).toBe(1);
  });
});

describe('useCart', () => {
  const wrapper = ({ children }) => <CartProvider>{children}</CartProvider>;

  it('calcula subtotal, frete e total (frete grátis a partir de R$ 200)', () => {
    const { result } = renderHook(() => useCart(), { wrapper });

    act(() => result.current.adicionar(livro, 1));
    expect(result.current.subtotal).toBe(50);
    expect(result.current.frete).toBe(19.9);
    expect(result.current.total).toBeCloseTo(69.9);

    act(() => result.current.adicionar(teclado, 2));
    expect(result.current.subtotal).toBe(250);
    expect(result.current.frete).toBe(0);
    expect(result.current.quantidadeTotal).toBe(3);
  });

  it('persiste o carrinho no localStorage', () => {
    const { result, unmount } = renderHook(() => useCart(), { wrapper });
    act(() => result.current.adicionar(livro, 2));
    unmount();

    const { result: novo } = renderHook(() => useCart(), { wrapper });
    expect(novo.current.itens[0]).toMatchObject({ id: 8, quantidade: 2 });
  });
});
