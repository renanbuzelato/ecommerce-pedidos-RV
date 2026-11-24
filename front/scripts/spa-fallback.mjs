// GitHub Pages não conhece as rotas do React Router: ao abrir /pedidos/3 direto,
// ele procura um arquivo que não existe e devolve 404.html. Copiamos o index.html
// para 404.html, assim o próprio React carrega e resolve a rota.
import { copyFileSync, existsSync } from 'node:fs';

if (existsSync('dist/index.html')) {
  copyFileSync('dist/index.html', 'dist/404.html');
  console.log('SPA fallback: dist/404.html criado.');
}
