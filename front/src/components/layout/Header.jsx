import { useState } from 'react';
import { Link, NavLink } from 'react-router-dom';
import { useCart } from '../../contexts/CartContext';

const links = [
  { to: '/', rotulo: 'Catálogo', fim: true },
  { to: '/pedidos', rotulo: 'Pedidos' },
  { to: '/clientes', rotulo: 'Clientes' },
  { to: '/admin/produtos', rotulo: 'Gestão de produtos' },
];

const classeLink = ({ isActive }) =>
  `block rounded-lg px-3 py-2 font-medium transition-colors ${
    isActive ? 'bg-white/15 text-white' : 'text-blue-100 hover:bg-white/10 hover:text-white'
  }`;

function IconeCarrinho() {
  return (
    <svg
      aria-hidden="true"
      viewBox="0 0 24 24"
      className="h-6 w-6"
      fill="none"
      stroke="currentColor"
      strokeWidth="2"
    >
      <path
        strokeLinecap="round"
        strokeLinejoin="round"
        d="M3 4h2l2.4 10.2a1 1 0 0 0 1 .8h8.9a1 1 0 0 0 1-.8L20 7H6"
      />
      <circle cx="9" cy="19" r="1.5" />
      <circle cx="17" cy="19" r="1.5" />
    </svg>
  );
}

export function Header() {
  const [menuAberto, setMenuAberto] = useState(false);
  const { quantidadeTotal } = useCart();

  return (
    <header className="sticky top-0 z-30 bg-marca-800 text-white shadow">
      <div className="mx-auto flex max-w-7xl items-center justify-between gap-4 px-4 py-3 sm:px-6">
        <Link to="/" className="flex items-center gap-2 text-lg font-extrabold tracking-tight">
          <span
            aria-hidden="true"
            className="grid h-9 w-9 place-items-center rounded-lg bg-destaque text-marca-800"
          >
            RV
          </span>
          <span>
            Loja RV<span className="sr-only"> — página inicial</span>
          </span>
        </Link>

        <nav aria-label="Principal" className="hidden lg:block">
          <ul className="flex items-center gap-1">
            {links.map((l) => (
              <li key={l.to}>
                <NavLink to={l.to} end={l.fim} className={classeLink}>
                  {l.rotulo}
                </NavLink>
              </li>
            ))}
          </ul>
        </nav>

        <div className="flex items-center gap-2">
          <Link
            to="/carrinho"
            className="relative flex items-center gap-2 rounded-lg px-3 py-2 font-medium hover:bg-white/10"
            aria-label={`Carrinho, ${quantidadeTotal} ${quantidadeTotal === 1 ? 'item' : 'itens'}`}
          >
            <IconeCarrinho />
            <span className="hidden sm:inline" aria-hidden="true">
              Carrinho
            </span>
            {quantidadeTotal > 0 && (
              <span
                aria-hidden="true"
                className="absolute -right-1 -top-1 grid h-5 min-w-5 place-items-center rounded-full bg-destaque px-1 text-xs font-bold text-marca-800"
              >
                {quantidadeTotal}
              </span>
            )}
          </Link>

          <button
            type="button"
            className="rounded-lg p-2 hover:bg-white/10 lg:hidden"
            aria-expanded={menuAberto}
            aria-controls="menu-movel"
            onClick={() => setMenuAberto((v) => !v)}
          >
            <span className="sr-only">{menuAberto ? 'Fechar menu' : 'Abrir menu'}</span>
            <svg
              aria-hidden="true"
              viewBox="0 0 24 24"
              className="h-6 w-6"
              fill="none"
              stroke="currentColor"
              strokeWidth="2"
            >
              {menuAberto ? (
                <path strokeLinecap="round" d="M6 6l12 12M18 6L6 18" />
              ) : (
                <path strokeLinecap="round" d="M4 7h16M4 12h16M4 17h16" />
              )}
            </svg>
          </button>
        </div>
      </div>

      {menuAberto && (
        <nav
          id="menu-movel"
          aria-label="Principal (celular)"
          className="border-t border-white/10 px-4 pb-4 lg:hidden"
        >
          <ul className="mt-2 space-y-1">
            {links.map((l) => (
              <li key={l.to}>
                <NavLink
                  to={l.to}
                  end={l.fim}
                  className={classeLink}
                  onClick={() => setMenuAberto(false)}
                >
                  {l.rotulo}
                </NavLink>
              </li>
            ))}
          </ul>
        </nav>
      )}
    </header>
  );
}
