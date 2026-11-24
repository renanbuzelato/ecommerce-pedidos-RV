import { createContext, useContext, useEffect, useMemo, useReducer } from 'react';
import { calcularFrete } from '../utils/constants';

const CartContext = createContext(null);
const CHAVE = 'rv-carrinho';

/**
 * Estado do carrinho com useReducer: todas as mudanças passam por ações nomeadas,
 * o que deixa as regras (limite de estoque, quantidade mínima) num lugar só.
 */
export function cartReducer(itens, acao) {
  switch (acao.tipo) {
    case 'adicionar': {
      const { produto, quantidade = 1 } = acao;
      const existente = itens.find((i) => i.id === produto.id);
      if (existente) {
        const nova = Math.min(existente.quantidade + quantidade, produto.estoque);
        return itens.map((i) =>
          i.id === produto.id ? { ...i, quantidade: nova, estoque: produto.estoque } : i,
        );
      }
      const { id, nome, preco, estoque, categoria, imagem } = produto;
      return [
        ...itens,
        { id, nome, preco, estoque, categoria, imagem, quantidade: Math.min(quantidade, estoque) },
      ];
    }
    case 'alterarQuantidade': {
      return itens.map((i) =>
        i.id === acao.id
          ? { ...i, quantidade: Math.max(1, Math.min(acao.quantidade, i.estoque)) }
          : i,
      );
    }
    case 'remover':
      return itens.filter((i) => i.id !== acao.id);
    case 'limpar':
      return [];
    default:
      throw new Error(`Ação desconhecida: ${acao.tipo}`);
  }
}

function carregarInicial() {
  try {
    return JSON.parse(localStorage.getItem(CHAVE)) ?? [];
  } catch {
    return [];
  }
}

export function CartProvider({ children }) {
  const [itens, dispatch] = useReducer(cartReducer, undefined, carregarInicial);

  // Persistência: o carrinho sobrevive à navegação e ao F5
  useEffect(() => {
    try {
      localStorage.setItem(CHAVE, JSON.stringify(itens));
    } catch {
      /* ignora */
    }
  }, [itens]);

  const valor = useMemo(() => {
    const subtotal = itens.reduce((s, i) => s + i.preco * i.quantidade, 0);
    const frete = calcularFrete(subtotal);
    return {
      itens,
      quantidadeTotal: itens.reduce((s, i) => s + i.quantidade, 0),
      subtotal,
      frete,
      total: subtotal + frete,
      adicionar: (produto, quantidade) => dispatch({ tipo: 'adicionar', produto, quantidade }),
      alterarQuantidade: (id, quantidade) =>
        dispatch({ tipo: 'alterarQuantidade', id, quantidade }),
      remover: (id) => dispatch({ tipo: 'remover', id }),
      limpar: () => dispatch({ tipo: 'limpar' }),
    };
  }, [itens]);

  return <CartContext.Provider value={valor}>{children}</CartContext.Provider>;
}

export function useCart() {
  const ctx = useContext(CartContext);
  if (!ctx) throw new Error('useCart deve ser usado dentro de <CartProvider>');
  return ctx;
}
