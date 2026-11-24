# Sistema de Gestão de Pedidos — E-commerce

> Projeto integrador das Unidades Curriculares **Desenvolvimento Back-end** e **Framework Desenvolvimento Front-End**
> Curso Superior de Tecnologia em Análise e Desenvolvimento de Sistemas — Turma CSTADS601

**Aplicação publicada:** _(cole aqui o link do GitHub Pages)_ · **Relatórios:** [`front/docs`](front/docs)

## Equipe / Squad

| Nome | Papel no projeto |
|---|---|
| Renan Buzelato | Back-end: domínio, persistência e API REST |
| Vitor de Souza Dias | Front-end React, domínio Cliente e documentação |


## Descrição do desafio

Uma equipe de desenvolvimento recebeu a demanda de construir um sistema de gestão de pedidos
para um e-commerce: cadastro de produtos e clientes, montagem de pedidos com controle de estoque,
processamento de pagamentos (cartão, Pix e boleto) e acompanhamento do pedido até a entrega.
O back-end (Java/Spring Boot) expõe uma API REST consumida por uma interface web em React.

## Funcionalidades previstas

- [x] Cadastro e gerenciamento de produtos
- [x] Cadastro e gerenciamento de clientes
- [x] Criação e gerenciamento de pedidos
- [x] Processamento de pagamentos (cartão, boleto, Pix)
- [x] Testes automatizados (unitários e de integração)
- [x] Pipeline de CI/CD
- [x] API REST para consumo por um front-end

## Tecnologias

- **Back-end:** Java 17, Maven, Spring Boot 3.5 (Web, Data JPA, Validation), MySQL 8, H2 (testes)
- **Testes:** JUnit 5, Mockito, MockMvc, JaCoCo
- **Front-end:** React 19, Vite, React Router, Tailwind CSS, Vitest, Testing Library, Playwright
- **Entrega contínua:** Git/GitHub (branches + Pull Requests), GitHub Actions, GitHub Pages

## Estrutura de pastas

```
ecommerce-pedidos-RV/
├── .github/workflows/      # back-ci.yml (mvn verify) e front-ci.yml (lint, testes, E2E, deploy)
├── back/ecommerce-pedidos-RV/
│   ├── src/main/java/com/ecommerce/pedidos/RV/
│   │   ├── controlador/    # @RestController, DTOs (records), PedidoMapper, TratadorDeErros
│   │   ├── config/         # CORS
│   │   ├── servico/        # regras de negócio + @Transactional
│   │   ├── repositorio/    # Spring Data JPA
│   │   ├── modelo/         # entidades e pagamento polimórfico
│   │   └── excecao/        # exceções do domínio
│   ├── src/main/resources/db/   # schema.sql (fonte da verdade) e dados-exemplo.sql
│   └── docs/               # registros de migração (Aula 14) e da API REST (Aula 15)
└── front/                  # interface React (ver front/README.md)
```

## Como rodar o projeto

**Back-end (API REST em http://localhost:8080/api)** — pré-requisitos: Java 17+, Maven e MySQL 8.

```bash
cd back/ecommerce-pedidos-RV
mysql -u root -p < src/main/resources/db/schema.sql
mysql -u root -p < src/main/resources/db/dados-exemplo.sql   # opcional: produtos e clientes de exemplo
export DB_PASSWORD=suasenha        # PowerShell: $env:DB_PASSWORD="suasenha"
mvn test                           # não precisa de MySQL (H2 em memória)
mvn spring-boot:run
```

**Front-end (http://localhost:5173)** — pré-requisito: Node.js 20+.

```bash
cd front
npm install
echo VITE_API_URL=/api > .env      # sem esta linha o front usa a API simulada
npm run dev
```

Detalhes: [`back/README.md`](back/README.md) · [`front/README.md`](front/README.md) ·
[contrato da API](front/docs/CONTRATO_API.md).

## Roadmap do projeto (por aula)

| Aula | Entrega |
|---|---|
| 01 | Repositório criado, estruturado, com README e commit inicial |
| 02 | Fluxo de branches e primeiro Pull Request revisado |
| 03 | Classe utilitária (Utils) do domínio |
| 04 | Classes de domínio inicial (Produto, Cliente, Pedido, ItemPedido) |
| 05 | Encapsulamento e abstração aplicados |
| 06 | Hierarquia de formas de pagamento (herança) |
| 07 | Relacionamentos entre classes do domínio |
| 08 | Módulo de pagamento polimórfico |
| 09 | Tratamento de exceções |
| 10 | Suíte de testes unitários |
| 11 | Suíte de testes de integração + relatório de cobertura |
| 12 | Persistência: conexão, Create e Read |
| 13 | Persistência: Update, Delete e padrão DAO/Repository |
| 14 | Migração para Spring Boot ✅ |
| 15 | API REST + pipeline CI/CD ✅ |
| 16 | Entrega final, documentação e apresentação ✅ |

## Combinado da equipe (ética e convivência)

1. Ninguém envia commit direto para a `main`: toda mudança entra por Pull Request revisado por outro integrante.
2. Commits pequenos e descritivos no padrão `feat:`, `fix:`, `test:`, `docs:`, uma branch por funcionalidade.
3. O código só é integrado com os testes passando no pipeline, e dúvidas ou atrasos são avisados no grupo.

## Licença

Projeto acadêmico — Faculdade de Tecnologia SENAI "Antonio Adolpho Lobbe".
