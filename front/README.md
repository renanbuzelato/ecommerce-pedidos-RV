# Front-end — Sistema de Gestão de Pedidos para E-commerce

Interface web em **React** do Sistema de Gestão de Pedidos (squad RV), construída para a
Situação de Aprendizagem Desafiadora integrada entre as UCs **Framework Desenvolvimento
Front-End** e **Desenvolvimento Back-end** — ADS, Faculdade SENAI "Antonio Adolpho Lobbe".

**Aplicação publicada:** _(cole aqui o link do GitHub Pages depois do primeiro deploy —
ex.: https://renanbuzelato.github.io/ecommerce-pedidos-RV/)_

|               |                                                                                                       |
| ------------- | ----------------------------------------------------------------------------------------------------- |
| **Stack**     | React 19 · Vite 8 · React Router 7 · Tailwind CSS 4                                                   |
| **Testes**    | Vitest + React Testing Library (componentes) · Playwright + axe-core (ponta a ponta e acessibilidade) |
| **Qualidade** | ESLint 9 (com `jsx-a11y`) · Prettier · GitHub Actions                                                 |
| **Dados**     | API simulada no navegador **ou** API REST do back-end Spring Boot (troca por variável de ambiente)    |

## Funcionalidades

| Módulo             | O que tem                                                                                                                                  |
| ------------------ | ------------------------------------------------------------------------------------------------------------------------------------------ |
| Catálogo           | Grade de cartões, busca por nome, filtro por categoria e faixa de preço (filtros ficam na URL), página de detalhe com indicação de estoque |
| Gestão de produtos | Listagem, cadastro, edição e exclusão com formulário validado e confirmação em modal                                                       |
| Clientes           | Listagem com busca, cadastro e edição com validação de e-mail, CPF (dígitos verificadores), CEP e telefone; vários endereços por cliente   |
| Carrinho           | Adicionar/remover, alterar quantidade (limitada ao estoque), subtotal, frete e total automáticos, persistência no `localStorage`           |
| Checkout           | Seleção de cliente e endereço, pagamento por cartão, Pix ou boleto, resultado simulado **aprovado / recusado / pendente** e nova tentativa |
| Pedidos            | Lista com filtro por situação, detalhe com linha do tempo (pendente → pago → enviado → entregue / cancelado) e mudança de situação         |
| Navegação          | Menu responsivo, rotas para todas as telas, rotas dinâmicas, página 404, carregamento, mensagens de sucesso e erro                         |

## Pré-requisitos

- Node.js **20 ou superior** (testado no 22) e npm
- (Opcional) back-end Spring Boot rodando em `http://localhost:8080` para usar a API real

## Instalação e execução

```bash
cd front
npm install
cp .env.example .env      # Windows: copy .env.example .env
npm run dev               # http://localhost:5173
```

Sem mexer no `.env`, a aplicação usa a **API simulada** e já abre com produtos, clientes e
pedidos de exemplo. Os dados ficam no `localStorage` do navegador; para voltar ao estado
inicial, limpe o armazenamento do site no DevTools (Application → Storage → Clear site data).

### Usando a API real (back-end Spring Boot)

1. Suba o back (veja `back/README.md`): MySQL com `schema.sql` + `dados-exemplo.sql` e
   `mvn spring-boot:run` (porta 8080).
2. No `.env` do front:

```env
VITE_API_URL=/api
```

3. `npm run dev` — o rodapé passa a mostrar **Fonte dos dados: API REST do back-end**.

Com `/api`, o servidor de desenvolvimento do Vite repassa as chamadas para
`http://localhost:8080/api` (proxy configurado em `vite.config.js`), então não é preciso
configurar CORS. Para apontar direto para outro endereço, use a URL completa
(`VITE_API_URL=http://localhost:8080/api`) — nesse caso o back precisa liberar CORS.
O contrato (e onde cada endpoint foi implementado no back) está em
[`docs/CONTRATO_API.md`](docs/CONTRATO_API.md). O site publicado no GitHub Pages usa a API
simulada, porque o back-end não está hospedado.

## Scripts NPM

| Script                            | O que faz                                                                                       |
| --------------------------------- | ----------------------------------------------------------------------------------------------- |
| `npm run dev`                     | Servidor de desenvolvimento com recarga automática                                              |
| `npm run build`                   | Build de produção em `dist/` (gera também `404.html` para o GitHub Pages)                       |
| `npm run preview`                 | Serve o build de produção localmente                                                            |
| `npm run lint`                    | ESLint (inclui regras de acessibilidade `jsx-a11y`)                                             |
| `npm run format` / `format:check` | Formata / confere a formatação com Prettier                                                     |
| `npm test`                        | Testes de componentes e integração (Vitest + Testing Library)                                   |
| `npm run test:watch`              | Testes em modo observação                                                                       |
| `npm run test:e2e`                | Testes ponta a ponta e auditoria axe (Playwright). Na 1ª vez: `npx playwright install chromium` |

## Estrutura do projeto

```
front/
├── e2e/                      # Playwright: fluxo de compra + auditoria de acessibilidade (axe)
├── docs/                     # Manual do usuário, contrato da API, relatórios
├── public/                   # favicon, robots.txt
├── scripts/spa-fallback.mjs  # copia index.html -> 404.html (rotas no GitHub Pages)
└── src/
    ├── components/
    │   ├── ui/               # Botão, Card, campos de formulário, Modal, Tabela, Badge, Feedback
    │   ├── layout/           # Layout, Header (menu responsivo), Footer
    │   ├── produtos/         # ProductCard, ProductFilters, ProductForm, StockIndicator...
    │   ├── clientes/         # ClienteForm
    │   └── pedidos/          # StatusTimeline, ResumoValores
    ├── contexts/             # CartContext (carrinho), ToastContext (mensagens)
    ├── hooks/                # useAsync (carregamento/erro), useDocumentTitle
    ├── pages/                # Uma pasta/arquivo por tela (catálogo, checkout, pedidos, admin...)
    ├── services/             # Camada de serviços (a ÚNICA que conhece a API)
    │   ├── api/              # client.js (troca mock/real), httpClient, mockServer, seed
    │   ├── mappers.js        # tradução formato do back <-> formato das telas
    │   └── *Service.js       # produto, cliente, pedido, pagamento
    ├── utils/                # validadores, máscaras/formatadores, constantes
    ├── routes.jsx            # mapa de rotas
    └── main.jsx              # ponto de entrada
```

## Rotas

| Rota                                                                      | Tela                                                       |
| ------------------------------------------------------------------------- | ---------------------------------------------------------- |
| `/`                                                                       | Catálogo (aceita `?busca=&categoria=&precoMin=&precoMax=`) |
| `/produtos/:id`                                                           | Detalhe do produto                                         |
| `/carrinho`                                                               | Carrinho                                                   |
| `/checkout`                                                               | Finalizar compra (entrega → pagamento → resultado)         |
| `/pedidos` · `/pedidos/:id`                                               | Lista (aceita `?situacao=`) e detalhe do pedido            |
| `/clientes` · `/clientes/novo` · `/clientes/:id/editar`                   | Clientes                                                   |
| `/admin/produtos` · `/admin/produtos/novo` · `/admin/produtos/:id/editar` | Gestão de produtos                                         |
| qualquer outra                                                            | Página 404                                                 |

## Simulação de pagamento

| Forma  | Resultado                                                                                   |
| ------ | ------------------------------------------------------------------------------------------- |
| Pix    | **Aprovado** — pedido passa para _Pago_                                                     |
| Boleto | **Pendente** — pedido fica _Pendente_ até "Confirmar compensação do boleto" no detalhe      |
| Cartão | **Aprovado**; cartões terminados em **0000** são **recusados** (ex.: `4111 1111 1111 0000`) |

As regras espelham o domínio Java (`Pix`, `Boleto`, `CartaoCredito`, `Pedido.pagar()`).

## Testes e CI

- `src/**/*.test.{js,jsx}` — 49 testes: validadores, carrinho, componentes, regras da API simulada, mappers com o JSON real do back e
  fluxos de tela (cadastro, exclusão com confirmação, filtros, checkout com pagamento recusado e nova tentativa).
- `e2e/compra.spec.js` — fluxo completo no navegador real, persistência do carrinho, teclado e 404 (desktop e celular).
- `e2e/acessibilidade.spec.js` — axe-core (WCAG 2.1 A/AA) em todas as telas; falha se houver violação.
- `.github/workflows/front-ci.yml` (na raiz do repositório) — lint → testes → build → E2E → deploy no GitHub Pages.

## Deploy (GitHub Pages)

1. No GitHub: **Settings → Pages → Source: GitHub Actions**.
2. Faça merge na `main`; o job `deploy` publica `front/dist` em `https://<usuario>.github.io/<repositorio>/`.
3. Copie o link para o topo deste README.

O build usa `VITE_BASE=/<repositorio>/` e o React Router lê o mesmo prefixo
(`import.meta.env.BASE_URL`), por isso as rotas funcionam também ao abrir um link direto.

## Documentação

- [Manual do usuário](docs/MANUAL_USUARIO.md)
- [Contrato da API e implementação no back-end](docs/CONTRATO_API.md)
- Relatório técnico e relatório de acessibilidade (ABNT, PDF e DOCX): `docs/Relatorio_*_ABNT.*`

## Equipe

- Vitor de Souza Dias
- Renan Buzelato
