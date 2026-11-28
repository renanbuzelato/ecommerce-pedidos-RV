# Documentação da API REST — E-commerce Pedidos RV

> Aula 15 — Template de Documentação da API. Quem for consumir a API deve conseguir
> usá-la só com este documento e a coleção do Postman, sem perguntar nada à equipe.

- **Base:** `http://localhost:8080/api` · JSON (UTF-8)
- **Coleção para importar no Postman/Insomnia:** [`ecommerce-pedidos-RV.postman_collection.json`](ecommerce-pedidos-RV.postman_collection.json)
  (24 requisições, com os casos de sucesso e de erro, e testes de status em cada uma)
- **Exemplos completos de JSON:** [`front/docs/CONTRATO_API.md`](../../../front/docs/CONTRATO_API.md)

## Parte 1 — Endpoints

| Método | Endereço | Sucesso | Erros previstos |
|---|---|---|---|
| GET | `/produtos` | 200 | — |
| GET | `/produtos/{id}` | 200 | 404 |
| POST | `/produtos` | 201 + Location | 400, 409 (nome repetido) |
| PUT | `/produtos/{id}` | 200 | 400, 404, 409 |
| DELETE | `/produtos/{id}` | 204 | 404, 409 (produto já vendido) |
| GET | `/clientes` | 200 | — |
| GET | `/clientes/{id}` | 200 | 404 |
| POST | `/clientes` | 201 + Location | 400, 409 (e-mail ou CPF repetido) |
| PUT | `/clientes/{id}` | 200 | 400, 404, 409 |
| GET | `/pedidos` | 200 | — |
| GET | `/pedidos/{id}` | 200 | 404 |
| POST | `/pedidos` | 201 + Location | 400, 404, 409 (estoque insuficiente, produto inativo) |
| PATCH | `/pedidos/{id}` | 200 | 400, 404, 409 (transição de situação inválida) |
| POST | `/pedidos/{id}/pagamento` | 201 + Location | 400, 404, 409 (pedido já pago ou cancelado) |
| GET | `/pedidos/{id}/pagamento` | 200 | 404 (pedido inexistente ou sem pagamento) |

Regras seguidas em todos os endpoints:

- Todo endereço é substantivo no plural, sem verbo.
- Todo POST bem-sucedido devolve **201 com o cabeçalho `Location`**.
- Todo DELETE bem-sucedido devolve **204 sem corpo**.
- Nenhuma entidade JPA aparece na assinatura de um controlador: entrada e saída são `record`s (DTOs).
- Nenhuma regra de negócio no controlador — ela mora no serviço.
- Toda entrada é validada com `@Valid`.

## Parte 2 — Corpo de erro (único para a API inteira)

```json
{
  "status": 409,
  "erro": "Conflito",
  "mensagem": "Estoque insuficiente de Smartwatch Fit 2: disponível 3, solicitado 999",
  "caminho": "/api/pedidos",
  "instante": "2026-11-27T22:10:07"
}
```

No 400 de validação aparece também a lista `campos` (ela some nos demais erros):

```json
{
  "status": 400,
  "erro": "Dados inválidos",
  "mensagem": "O preço do produto deve ser positivo.",
  "caminho": "/api/produtos",
  "instante": "2026-11-27T22:11:41",
  "campos": [ { "campo": "preco", "mensagem": "O preço do produto deve ser positivo." } ]
}
```

| Situação | Exceção | HTTP |
|---|---|---|
| Recurso não existe | `ProdutoNaoEncontrado`, `ClienteNaoEncontrado`, `PedidoNaoEncontrado`, `PagamentoNaoEncontrado` | 404 |
| Conflito com o estado atual | `ProdutoDuplicado`, `ClienteDuplicado`, `EstoqueInsuficiente`, `PedidoInvalido`, regra de estado (`IllegalStateException`), violação de integridade | 409 |
| Campo inválido no corpo | `MethodArgumentNotValidException` (`@Valid`) | 400 + `campos` |
| Dado recusado pelo domínio, JSON malformado, id não numérico | `IllegalArgumentException`, `HttpMessageNotReadableException` | 400 |

As mensagens dizem o que aconteceu em linguagem de negócio, sem nome de tabela, classe ou SQL (Aula 09).
Tudo isso fica num só lugar, o `TratadorDeErros` (`@RestControllerAdvice`), sem `try/catch` nos controladores.

## Parte 3 — Decisões da equipe

| Decisão | O que escolhemos | Por quê |
|---|---|---|
| Prefixo dos endereços | `/api` antes de todos os recursos | Separa a API das páginas do front e permite o proxy do Vite (`/api` → `localhost:8080`) sem configurar CORS |
| Pagamento | Sub-recurso: `POST /pedidos/{id}/pagamento` cria um pagamento do pedido (201 + Location) | Pagar não é criar nem atualizar um pedido; é uma ação sobre ele. O `GET` do mesmo endereço consulta a última tentativa |
| Pagamento recusado | Também 201, com `"resultado": "RECUSADO"` | A tentativa foi registrada; quem recusou foi a operadora. O pedido continua aberto para nova tentativa |
| Regras de negócio violadas | 409 Conflito (estoque, pedido pago, produto vendido, duplicados) | O pedido está bem formado, mas conflita com o estado atual do sistema |
| Contrato × entidade | `record`s de entrada (`*Entrada`) e saída (`*Resposta`) | Renomear um atributo da entidade não quebra quem consome a API |
| Banco nos testes do pipeline | **H2 em memória** (`src/test/resources/application.properties`) | A máquina do GitHub não tem MySQL; com H2 a suíte inteira roda no CI sem serviço extra |
| Status 201 do pedido | Pedido nasce `ABERTO` e já baixa o estoque, numa transação só | Se faltar estoque em qualquer item, nada é gravado (rollback) |

## Parte 4 — Pipeline de CI

Arquivo: `.github/workflows/back-ci.yml`. A cada push na `main` e a cada Pull Request:

1. **Compilar e testar** — `mvn -B verify` numa máquina limpa (JDK 17). O `verify` passa pela fase
   `package`, então o `.jar` já sai pronto, sem `-DskipTests`.
2. **Publicar relatório de cobertura** — artefato `cobertura-jacoco` (relatório da Aula 11).
3. **Publicar o artefato** — artefato `ecommerce-jar` com o `.jar` da aplicação.

Combinado da equipe: **Pull Request com pipeline vermelho não é integrado.** A `main` é protegida e
exige aprovação. Se alguém enviar código que quebra a suíte, o pipeline avisa no Pull Request em
cerca de dois minutos.
