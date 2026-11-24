import { render } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { createMemoryRouter } from 'react-router-dom';
import App from '../App';
import { rotas } from '../routes';

/** Monta a aplicação inteira (provedores + rotas reais) a partir de uma URL. */
export function renderApp(url = '/') {
  const router = createMemoryRouter(rotas, { initialEntries: [url] });
  const user = userEvent.setup();
  return { user, router, ...render(<App router={router} />) };
}
