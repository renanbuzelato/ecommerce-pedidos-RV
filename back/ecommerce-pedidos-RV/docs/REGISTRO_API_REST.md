# Registro da API REST — Aula 15

Branch: `feature/api-rest-front`

## 1. Camadas depois da Aula 15

| Camada | Pacote | O que tem | O que NUNCA tem |
|---|---|---|---|
| Controller | `controlador` | Verbo HTTP, rota, `@Valid`, código de status, conversão entidade → DTO | Regra de negócio, `@Transactional` |
| DTO | `controlador.dto` | `record`s de entrada (com Bean Validation) e de saída | Entidade JPA |
| Tratamento de erros | `controlador.TratadorDeErros` | Exceção → HTTP + `{ status, mensagem, timestamp }` | Stack trace para o cliente |
| Service | `servico` | Regras, orquestração, transação | HTTP, JSON |
| Repository / Entity | `repositorio`, `modelo` | Como nas Aulas 13–14 | — |

## 2. Endpoints

| Método | Rota | Status de sucesso | Erros possíveis |
|---|---|---|---|
| GET | `/api/produtos` · `/api/produtos/{id}` | 200 | 404 |
| POST | `/api/produtos` | 201 + `Location` | 400, 409 |
| PUT | `/api/produtos/{id}` | 200 | 400, 404, 409 |
| DELETE | `/api/produtos/{id}` | 204 | 404, 409 (já vendido) |
| GET | `/api/clientes` · `/api/clientes/{id}` | 200 | 404 |
| POST / PUT | `/api/clientes` · `/api/clientes/{id}` | 201 / 200 | 400, 404, 409 |
| GET | `/api/pedidos` · `/api/pedidos/{id}` | 200 | 404 |
| POST | `/api/pedidos` | 201 + `Location` | 400, 404, 409 |
| PATCH | `/api/pedidos/{id}` `{ "situacao": ... }` | 200 | 404, 409 |
| POST | `/api/pedidos/{id}/pagamento` | 201 + `Location` | 400, 404, 409 |
| GET | `/api/pedidos/{id}/pagamento` | 200 | 404 |

## 3. Mapa exceção → HTTP

| Exceção | HTTP | Por quê |
|---|---|---|
| `ProdutoNaoEncontrado`, `ClienteNaoEncontrado`, `PedidoNaoEncontrado` | 404 | Recurso não existe |
| `ProdutoDuplicado`, `ClienteDuplicado`, `DataIntegrityViolationException` | 409 | Conflito com o estado atual |
| `EstoqueInsuficiente`, `PedidoInvalido` (verificadas, Aula 09), `IllegalStateException` | 409 | Pedido bem formado, mas conflita com o estado atual (roteiro da Aula 15) |
| `IllegalArgumentException` (validação nos construtores), Bean Validation, JSON malformado | 400 | Dados inválidos |

**Pagamento recusado não é erro HTTP:** a tentativa foi registrada e a resposta é
`{ "resultado": "RECUSADO" }` (201, como todo POST que cria). Quem recusou foi a operadora, e a interface precisa
mostrar isso e oferecer nova tentativa.

## 4. Mudanças no domínio (pedidas pela interface)

| Mudança | Onde | Observação |
|---|---|---|
| `descricao`, `categoria` | `Produto` + colunas | Catálogo com filtro por categoria |
| `definirEstoque(int)` | `Produto` | Ajuste de inventário na edição (valida negativo) |
| `telefone` + entidade `Endereco` | `Cliente` + tabela `endereco` | Composição (cascade ALL, orphanRemoval, `ON DELETE CASCADE`) |
| `ENVIADO`, `ENTREGUE` | `SituacaoPedido` | Novos métodos `enviar()`, `entregar()` |
| `EnderecoEntrega` (`@Embedded`) | colunas `entrega_*` | "Foto" do endereço, como o preço congelado do item |
| `RegistroPagamento` (`@Embedded`) | colunas `pagamento_*` | Última tentativa: forma, resultado, comprovante |
| Histórico (`@ElementCollection`) | tabela `pedido_historico` | Toda mudança de situação passa por `mudarSituacao()` |
| `confirmarCompensacaoBoleto()` | `Pedido` | Único caminho `ABERTO → PAGO` sem novo processamento |
| Cartão final `0000` recusado; descrição mostra só os 4 últimos dígitos | `CartaoCredito` | Permite demonstrar o fluxo de recusa |

## 5. Decisões e pontos de atenção

- **DTOs em vez de entidades na resposta:** com `open-in-view=false`, serializar uma relação
  LAZY fora da transação dá `LazyInitializationException`; além disso a entidade exporia
  campos internos. Os controladores sempre devolvem `record`s.
- **Fetch:** pedidos vêm com `JOIN FETCH` de cliente, itens e produtos (`buscarComItens`,
  `listarCompletos`); o histórico é `EAGER` e carregado num SELECT separado — colocar duas
  coleções `List` no mesmo `JOIN FETCH` causaria `MultipleBagFetchException`.
- **Criação do pedido em uma transação:** `ServicoPedido.criar` abre o pedido e adiciona todos
  os itens com `rollbackFor = ECommerceException.class`; se faltar estoque em qualquer item,
  nada é gravado.
- **Fábrica de pagamento:** `ServicoPagamento` escolhe `Pix`, `CartaoCredito` ou `Boleto`
  pela `forma`; o `Pedido` continua conhecendo só `ProcessadorPagamento` (Aula 08). O CVV
  nunca é enviado pelo front.
- **CORS:** origens em `app.cors.origens` (padrão `localhost:5173` e `4173`). Em
  desenvolvimento o proxy do Vite já evita CORS.
- **Banco:** `schema.sql` continua sendo a fonte da verdade (`ddl-auto=validate`); quem
  criou o banco na Aula 14 precisa recriá-lo.

## 6. Testes novos

| Teste | Tipo | O que prova |
|---|---|---|
| `controlador/ApiRestIntegracaoTest` | `@SpringBootTest` + MockMvc + H2 | Todos os endpoints, códigos 201/204/400/404/409, corpo de erro único com `campos`, Location do pagamento, DELETE idempotente, rollback por falta de estoque, ciclo de vida do pedido, Pix aprovado, cartão recusado com nova tentativa, boleto pendente e compensação |

## 7. CI

`.github/workflows/back-ci.yml` roda `mvn -B verify` a cada push/PR que mexa em `back/`
e publica o relatório do JaCoCo como artefato. Não precisa de MySQL (H2 em memória).

## 8. Ajustes do roteiro prático da Aula 15

| Item do roteiro | Como ficou |
|---|---|
| Corpo de erro único (Momento 3, Passo 4) | `ErroResponse { status, erro, mensagem, caminho, instante, campos? }` + `CampoInvalido`; substituiu o antigo `ErroResposta` |
| Conflitos de negócio | 422 → **409** (estoque insuficiente, pedido pago, produto vendido) |
| Pagamento como sub-recurso (Momento 3, Passo 2) | `POST /api/pedidos/{id}/pagamento` → 201 + Location; `GET` do mesmo endereço; substituiu `POST /api/pagamentos` |
| `Location` (Momento 2, Passo 4) | Montado com `UriComponentsBuilder` em todos os POSTs |
| `Produto.alterarDados` (Momento 2, Passo 5) | PUT altera pelo método da entidade, mantendo as validações da Aula 09 |
| DELETE (Momento 2, Passo 5) | `@ResponseStatus(NO_CONTENT)`; repetir dá 404 (idempotente) |
| Pipeline (Momento 3, Passo 5) | Steps nomeados + artefatos `cobertura-jacoco` e `ecommerce-jar` |
| Documentação e coleção (Momento 3, Passo 6) | [`API.md`](API.md) + [`ecommerce-pedidos-RV.postman_collection.json`](ecommerce-pedidos-RV.postman_collection.json) |
