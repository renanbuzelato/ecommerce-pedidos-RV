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
| DELETE | `/api/produtos/{id}` | 204 | 404, 422 (já vendido) |
| GET | `/api/clientes` · `/api/clientes/{id}` | 200 | 404 |
| POST / PUT | `/api/clientes` · `/api/clientes/{id}` | 201 / 200 | 400, 404, 409 |
| GET | `/api/pedidos` · `/api/pedidos/{id}` | 200 | 404 |
| POST | `/api/pedidos` | 201 + `Location` | 400, 404, 422 |
| PATCH | `/api/pedidos/{id}` `{ "situacao": ... }` | 200 | 404, 422 |
| POST | `/api/pagamentos` | 200 | 400, 404, 422 |

## 3. Mapa exceção → HTTP

| Exceção | HTTP | Por quê |
|---|---|---|
| `ProdutoNaoEncontrado`, `ClienteNaoEncontrado`, `PedidoNaoEncontrado` | 404 | Recurso não existe |
| `ProdutoDuplicado`, `ClienteDuplicado`, `DataIntegrityViolationException` | 409 | Conflito com o estado atual |
| `EstoqueInsuficiente`, `PedidoInvalido` (verificadas, Aula 09), `IllegalStateException` | 422 | Pedido bem formado, mas a regra de negócio não permite |
| `IllegalArgumentException` (validação nos construtores), Bean Validation, JSON malformado | 400 | Dados inválidos |

**Pagamento recusado não é erro HTTP:** a requisição deu certo e a resposta é
`{ "resultado": "RECUSADO" }` (200). Quem recusou foi a operadora, e a interface precisa
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
| `controlador/ApiRestIntegracaoTest` | `@SpringBootTest` + MockMvc + H2 | Todos os endpoints, códigos 201/204/400/404/409/422, rollback por falta de estoque, ciclo de vida do pedido, Pix aprovado, cartão recusado com nova tentativa, boleto pendente e compensação |

## 7. CI

`.github/workflows/back-ci.yml` roda `mvn -B verify` a cada push/PR que mexa em `back/`
e publica o relatório do JaCoCo como artefato. Não precisa de MySQL (H2 em memória).
