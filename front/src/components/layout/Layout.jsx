import { useEffect, useRef } from 'react';
import { Outlet, useLocation } from 'react-router-dom';
import { Header } from './Header';
import { Footer } from './Footer';

/**
 * Esqueleto de todas as telas.
 * - "Pular para o conteúdo" é o primeiro item do Tab (WCAG 2.4.1)
 * - Ao trocar de rota, o foco vai para o <main>: o leitor de tela
 *   não fica "preso" no link que foi clicado na página anterior.
 */
export function Layout() {
  const { pathname } = useLocation();
  const main = useRef(null);
  const primeiraRenderizacao = useRef(true);

  useEffect(() => {
    if (primeiraRenderizacao.current) {
      primeiraRenderizacao.current = false;
      return;
    }
    main.current?.focus();
    window.scrollTo(0, 0);
  }, [pathname]);

  return (
    <div className="flex min-h-screen flex-col">
      <a
        href="#conteudo"
        className="sr-only z-50 rounded-lg bg-white font-semibold text-marca-800 shadow-lg focus:not-sr-only focus:fixed focus:left-4 focus:top-3 focus:px-4 focus:py-2"
      >
        Pular para o conteúdo
      </a>
      <Header />
      <main
        id="conteudo"
        ref={main}
        tabIndex={-1}
        className="mx-auto w-full max-w-7xl flex-1 px-4 py-8 outline-none sm:px-6"
      >
        <Outlet />
      </main>
      <Footer />
    </div>
  );
}
