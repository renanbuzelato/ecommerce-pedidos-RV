import { RouterProvider } from 'react-router-dom';
import { CartProvider } from './contexts/CartContext';
import { ToastProvider } from './contexts/ToastContext';

/** Provedores globais (carrinho e mensagens) + roteador. */
export default function App({ router }) {
  return (
    <ToastProvider>
      <CartProvider>
        <RouterProvider router={router} />
      </CartProvider>
    </ToastProvider>
  );
}
