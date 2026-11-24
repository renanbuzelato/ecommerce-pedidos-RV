import { Layout } from './components/layout/Layout';
import CatalogoPage from './pages/CatalogoPage';
import ProdutoDetalhePage from './pages/ProdutoDetalhePage';
import CarrinhoPage from './pages/CarrinhoPage';
import CheckoutPage from './pages/CheckoutPage';
import PedidosPage from './pages/pedidos/PedidosPage';
import PedidoDetalhePage from './pages/pedidos/PedidoDetalhePage';
import ClientesPage from './pages/clientes/ClientesPage';
import ClienteFormPage from './pages/clientes/ClienteFormPage';
import ProdutosAdminPage from './pages/admin/ProdutosAdminPage';
import ProdutoFormPage from './pages/admin/ProdutoFormPage';
import NotFoundPage from './pages/NotFoundPage';

/**
 * Mapa de rotas (React Router). Exportado separado do App para que os testes
 * montem o MESMO mapa com createMemoryRouter.
 */
export const rotas = [
  {
    path: '/',
    element: <Layout />,
    children: [
      { index: true, element: <CatalogoPage /> },
      { path: 'produtos/:id', element: <ProdutoDetalhePage /> },
      { path: 'carrinho', element: <CarrinhoPage /> },
      { path: 'checkout', element: <CheckoutPage /> },
      { path: 'pedidos', element: <PedidosPage /> },
      { path: 'pedidos/:id', element: <PedidoDetalhePage /> },
      { path: 'clientes', element: <ClientesPage /> },
      { path: 'clientes/novo', element: <ClienteFormPage /> },
      { path: 'clientes/:id/editar', element: <ClienteFormPage /> },
      { path: 'admin/produtos', element: <ProdutosAdminPage /> },
      { path: 'admin/produtos/novo', element: <ProdutoFormPage /> },
      { path: 'admin/produtos/:id/editar', element: <ProdutoFormPage /> },
      { path: '*', element: <NotFoundPage /> },
    ],
  },
];
