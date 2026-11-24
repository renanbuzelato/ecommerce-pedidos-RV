import { useEffect } from 'react';

/** Título da aba por página: ajuda leitores de tela a saber onde estão (WCAG 2.4.2). */
export function useDocumentTitle(titulo) {
  useEffect(() => {
    document.title = titulo ? `${titulo} | Loja RV` : 'Loja RV — Gestão de Pedidos';
  }, [titulo]);
}
