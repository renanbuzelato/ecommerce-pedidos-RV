# Contrato da API — front-end × back-end

Este documento diz **o que o front-end envia e espera receber** de cada endpoint.
A API simulada (`src/services/api/mockServer.js`) implementa exatamente este contrato,
então ela serve de referência executável para os controladores REST da Aula 15.

Base: `/api` · JSON · valores monetários como texto ou número (`"349.90"` ou `349.9`).

## Formato de erro

Corpo único para a API inteira (Aula 15). O front lê `mensagem` (ou, por compatibilidade, `message`/`error`):

```json
{
  "status": 409,
  "erro": "Conflito",
  "mensagem": "Já existe um produto chamado \"Mouse Sem Fio Ergo\".",
  "caminho": "/api/produtos",
  "instante": "2026-11-27T22:10:07"
}
```

No 400 de validação vem também `"campos": [ { "campo": "preco", "mensagem": "..." } ]`.

| Exceção do domínio (Java)                                                                        | HTTP esperado |
| ------------------------------------------------------------------------------------------------ | ------------- |
| `ProdutoNaoEncontradoException`, `ClienteNaoEncontradoException`, `PedidoNaoEncontradoException` | 404           |
| `ProdutoDuplicadoException`, `ClienteDuplicadoException`, exclusão bloqueada por RESTRICT        | 409           |
| `EstoqueInsuficienteException`, `PedidoInvalidoException`, transição de situação inválida        | 409           |
| Bean Validation (`@Valid`), `IllegalArgumentException` (validação nos construtores)              | 400           |

O front usa o **404** para mostrar a página "não encontrado" nas rotas dinâmicas.

## Produtos

| Método | Rota             | Corpo enviado    | Resposta        |
| ------ | ---------------- | ---------------- | --------------- |
| GET    | `/produtos`      | —                | `Produto[]`     |
| GET    | `/produtos/{id}` | —                | `Produto`       |
| POST   | `/produtos`      | `ProdutoEntrada` | 201 + `Produto` |
| PUT    | `/produtos/{id}` | `ProdutoEntrada` | `Produto`       |
| DELETE | `/produtos/{id}` | —                | 204             |

```json
// Produto (saída)  — mesmos nomes dos getters da entidade Produto
{ "id": 1, "nome": "Fone Bluetooth ProSound", "descricao": "...", "categoria": "Eletrônicos",
  "preco": "349.90", "quantidadeEmEstoque": 25, "ativo": true }

// ProdutoEntrada
{ "nome": "...", "descricao": "...", "categoria": "Casa", "preco": "59.90",
  "quantidadeEmEstoque": 10, "ativo": true }
```

## Clientes

| Método | Rota             | Corpo enviado    | Resposta        |
| ------ | ---------------- | ---------------- | --------------- |
| GET    | `/clientes`      | —                | `Cliente[]`     |
| GET    | `/clientes/{id}` | —                | `Cliente`       |
| POST   | `/clientes`      | `ClienteEntrada` | 201 + `Cliente` |
| PUT    | `/clientes/{id}` | `ClienteEntrada` | `Cliente`       |

```json
{
  "id": 1,
  "nome": "Ana Souza",
  "documento": "529.982.247-25",
  "email": "ana.souza@email.com",
  "telefone": "(16) 99123-4567",
  "enderecos": [
    {
      "id": 1,
      "apelido": "Casa",
      "cep": "13560-000",
      "logradouro": "Rua Episcopal",
      "numero": "1200",
      "complemento": "Apto 12",
      "bairro": "Centro",
      "cidade": "São Carlos",
      "uf": "SP"
    }
  ]
}
```

`documento` é o nome do getter em `Pessoa` (coluna `cpf`). O front aceita `documento` **ou** `cpf`.

## Pedidos

| Método | Rota            | Corpo enviado                                                        | Resposta                           |
| ------ | --------------- | -------------------------------------------------------------------- | ---------------------------------- |
| GET    | `/pedidos`      | —                                                                    | `Pedido[]`                         |
| GET    | `/pedidos/{id}` | —                                                                    | `Pedido`                           |
| POST   | `/pedidos`      | `{ clienteId, enderecoEntrega, itens: [{ produtoId, quantidade }] }` | 201 + `Pedido` (situação `ABERTO`) |
| PATCH  | `/pedidos/{id}` | `{ "situacao": "ENVIADO" }`                                          | `Pedido`                           |

```json
{
  "id": 5,
  "cliente": { "id": 1, "nome": "Ana Souza" },
  "enderecoEntrega": { "...": "mesmo formato do endereço" },
  "itens": [
    {
      "id": 9,
      "produto": { "id": 4, "nome": "Mouse Sem Fio Ergo" },
      "quantidade": 2,
      "precoUnitario": "129.90"
    }
  ],
  "situacao": "ABERTO",
  "dataCriacao": "2026-11-24T19:10:00",
  "valorTotal": "259.80",
  "frete": "19.90",
  "totalComFrete": "279.70",
  "historico": [{ "situacao": "ABERTO", "data": "2026-11-24T19:10:00" }],
  "pagamento": null
}
```

`valorTotal` = `Pedido.calcularValorTotal()`; `frete` = `CalculadoraFrete.calcular()`.
Se o back não mandar `frete`/`totalComFrete`/`historico`, o front calcula/monta o que der.

**Transições aceitas no PATCH:** `ABERTO → CANCELADO` (devolve estoque), `PAGO → ENVIADO`,
`ENVIADO → ENTREGUE`, e `ABERTO → PAGO` **somente** para boleto pendente (compensação).
`PAGO → CANCELADO` dá 409 ("Pedido pago não pode ser cancelado.").

## Pagamento (sub-recurso do pedido)

Pagar não é criar nem atualizar um pedido, é uma ação sobre ele: em REST vira
"criar um pagamento do pedido".

| Método | Rota                      | Corpo enviado       | Resposta                                      |
| ------ | ------------------------- | ------------------- | --------------------------------------------- |
| POST   | `/pedidos/{id}/pagamento` | `{ forma, dados }`  | 201 + `Location` + `ResultadoPagamento`       |
| GET    | `/pedidos/{id}/pagamento` | —                   | 200 (última tentativa) ou 404 se não houver   |

```json
// forma = "CARTAO" -> dados: { "numero": "4111111111111111", "titular": "ANA", "validade": "12/30", "parcelas": 3 }
// forma = "PIX"    -> dados: { "chave": "ana@email.com" }
// forma = "BOLETO" -> dados: { "vencimento": "27/11/2026" }
// (o CVV nunca é enviado)

{
  "pedidoId": 5,
  "forma": "PIX",
  "resultado": "APROVADO",
  "comprovante": "PIX-1764012345678",
  "descricao": "Pix chave: ana@email.com",
  "mensagem": "Cobrança Pix confirmada."
}
```

`resultado`: `APROVADO` (pedido vira `PAGO`), `RECUSADO` (pedido continua `ABERTO`, o usuário
pode tentar de novo) ou `PENDENTE` (boleto). Recusado também é 201: a tentativa foi registrada.
Pagar pedido já pago ou cancelado dá 409. No back isso é o `ProcessadorPagamento`
escolhido pela `forma` — `getComprovante()` e `getDescricao()` já existem nas classes.

## Implementação no back-end (Aula 15)

O contrato acima está implementado em `back/ecommerce-pedidos-RV` e coberto pelo teste
`ApiRestIntegracaoTest` (MockMvc + H2), que envia exatamente estes JSON.

| Endpoint | Controlador | Serviço / regra usada |
|---|---|---|
| `/api/produtos` | `ProdutoController` | `ServicoProduto` (nome único, exclusão bloqueada se vendido) |
| `/api/clientes` | `ClienteController` | `ServicoCliente` (e-mail/CPF únicos, endereços em composição) |
| `/api/pedidos` | `PedidoController` + `PedidoMapper` | `ServicoPedido.criar / alterarSituacao`, `CalculadoraFrete` |
| `/api/pedidos/{id}/pagamento` | `PagamentoController` | `ServicoPagamento` escolhe `Pix`, `CartaoCredito` ou `Boleto` (`ProcessadorPagamento`) |
| erros | `TratadorDeErros` (`@RestControllerAdvice`) | exceções da Aula 09/14 → 400/404/409, corpo único |

Mudanças no domínio feitas para casar com a interface: `Produto.descricao/categoria`,
`Cliente.telefone` + entidade `Endereco`, `SituacaoPedido` com `ENVIADO`/`ENTREGUE`,
histórico (`pedido_historico`), endereço de entrega e último pagamento gravados no pedido,
e `CartaoCredito` recusando cartões terminados em `0000`. As entidades nunca saem pela API:
os controladores devolvem records (DTOs), evitando `LazyInitializationException` com
`open-in-view=false`.

Exclusão de produto já vendido devolve **409** (conflito com o estado atual), com a mensagem
explicando que ele deve ser desativado.

Documentação no formato do template da aula e coleção do Postman:
`back/ecommerce-pedidos-RV/docs/API.md` e `back/ecommerce-pedidos-RV/docs/ecommerce-pedidos-RV.postman_collection.json`.
