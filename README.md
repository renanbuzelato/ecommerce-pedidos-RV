# 🛒 Sistema de Gestão de Pedidos — E-commerce
Projeto integrador desenvolvido para a Unidade Curricular de **Desenvolvimento Back-End** do Curso Superior de Tecnologia em Análise e Desenvolvimento de Sistemas (CSTADS) — Faculdade de Tecnologia SENAI "Antonio Adolpho Lobbe" (São Carlos, SP).

### 👥 Equipe / Squad
*   **Renan Buzelato** — Desenvolvedor de Software (Responsável do dia na Aula 01)
*   *(Demais integrantes da squad)*

---

### 📝 Descrição do Desafio
Construção de um sistema robusto e modular de gestão de pedidos para e-commerce, aplicando integralmente os conceitos de **Programação Orientada a Objetos (POO)** em Java, testes automatizados abrangentes, documentação técnica com Javadoc e UML, estruturação de build automatizado com Maven, e versionamento colaborativo avançado no GitHub.

---

### 🛠️ Tecnologias Utilizadas
*   **Java SE 17** (Linguagem de Programação)
*   **Apache Maven 3.9** (Gerenciamento de dependências e automação de builds)
*   **Git / GitHub** (Controle de versão distribuído)
*   *(Tecnologias futuras: JUnit 5, Spring Boot, Spring Data JPA, PostgreSQL)*

---

### 📂 Estrutura de Pastas Padrão Maven
```text
ecommerce-pedidos-RV/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/senai/ecommerce/
│   │           ├── modelo/         (Classes de domínio - Produto, Cliente, etc.)
│   │           ├── util/           (Classes utilitárias de apoio - PedidoUtils)
│   │           └── Aplicacao.java  (Classe principal de demonstração)
│   └── test/
│       └── java/
│           └── com/senai/ecommerce/ (Classes de testes automatizados)
├── docs/                           (Diagramas de classes UML e documentação)
├── pom.xml                         (Configuração do projeto Maven)
└── README.md                       (Documentação de entrada)
```

---

### 🤝 Combinado Ético da Equipe
1.  **Branch main protegida:** Nenhum commit será realizado diretamente na `main`. Todo código entra por Pull Request com revisão obrigatória.
2.  **Clean Code:** Seguir rigorosamente as convenções de nomenclatura Java (camelCase, PascalCase, UPPER_SNAKE_CASE) e manter métodos curtos com responsabilidade única.
3.  **Comunicação ativa:** Integrar as branches cedo para mitigar conflitos e debater discordâncias baseando-se estritamente em argumentos técnicos.

---

### ⚠️ Dívidas Técnicas Registradas
*   **double para valores monetários (Aula 03/04):** Os preços e subtotais estão utilizando `double` por simplificação de ensino. **Resolução:** Migrados com sucesso para `BigDecimal` na Aula 05 para evitar erros de precisão e arredondamento binário.
