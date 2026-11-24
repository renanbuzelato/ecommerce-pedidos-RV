import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { createBrowserRouter } from 'react-router-dom';
import App from './App';
import { rotas } from './routes';
import './index.css';

// basename acompanha o "base" do Vite (necessário no GitHub Pages: /nome-do-repo/)
const router = createBrowserRouter(rotas, {
  basename: import.meta.env.BASE_URL.replace(/\/$/, '') || '/',
});

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <App router={router} />
  </StrictMode>,
);
