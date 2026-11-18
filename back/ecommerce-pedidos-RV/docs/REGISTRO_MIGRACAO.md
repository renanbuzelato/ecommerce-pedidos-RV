# Registro de Migração — Aula 14 (JDBC → Spring Boot)

Branch: `feature/migracao-spring`

## 1. Comparação de linhas (DAO × Spring Data)

| Antes (JDBC) | Linhas | Depois (Spring Data) | Linhas | Métodos declarados |
|---|---|---|---|---|
| `ProdutoDAO.java` | 132 | `ProdutoRepository.java` | 19 (4 métodos) | 4 |
| `RepositorioProduto.java` (interface) | 12 | — (herdado de `JpaRepository`) | 0 | — |
| `RepositorioException.java` | 10 | — (Spring traduz para `DataAccessException`) | 0 | — |
| `ConexaoFactory` (referenciada, mas não estava no repositório) | — | `application.properties` | 6 propriedades | — |
| **Total** | **154** | **Total** | **19** | |

`save`, `findById`, `findAll`, `deleteById`, `existsById` e `count` vêm de graça pela herança.

**O que se perdeu:** o controle explícito do SQL. **O que se ganhou:** conexão, `PreparedStatement`,
`ResultSet`, mapeamento linha→objeto, tratamento de `SQLException` e transação deixaram de ser código nosso.

## 2. O que foi SUBSTITUÍDO

| Antes | Depois |
|---|---|
| `App.java` com `main` manual | `EcommerceApplication` com `@SpringBootApplication` |
| `ConexaoFactory` + propriedades da Aula 12 | `application.properties` com `${DB_USER:root}` / `${DB_PASSWORD}` |
| Mapeamento feito à mão na ficha da Aula 12 | Anotações JPA (`@Entity`, `@Table`, `@Column`, `@ManyToOne`, `@OneToMany`) |
| `ProdutoDAO` / `RepositorioProduto` | `ProdutoRepository extends JpaRepository` |
| `commit`/`rollback` manuais da Aula 13 | `@Transactional` na camada de serviço |
| `new` espalhado no `main` | Injeção por construtor (sem `@Autowired`) |
| `double` em preço e total | `BigDecimal` com `precision = 10, scale = 2` |
| `boolean pago` em `Pedido` | `enum SituacaoPedido` (ABERTO, PAGO, CANCELADO) com `EnumType.STRING` |

## 3. O que foi PRESERVADO

- `groupId` / `artifactId` / pacote `com.ecommerce.pedidos.RV` (identidade do projeto desde a Aula 01).
- Driver do MySQL (Aula 12) e plugin JaCoCo (Aula 11) no `pom.xml`.
- **Validações de negócio nos construtores** (Aula 09): o JPA usa o construtor `protected` vazio e preenche
  os campos por reflexão, então as validações não substituem nem são substituídas — as duas defesas ficam.
- Hierarquia `Pessoa → Cliente / Funcionario` (Aula 05): `Pessoa` virou `@MappedSuperclass`.
- Pagamento polimórfico (Aulas 06 e 08): `ProcessadorPagamento`, `Pix`, `CartaoCredito`, `Boleto`, `Dinheiro` sem alteração.
- Exceções de domínio da Aula 09 (`EstoqueInsuficienteException`, `PedidoInvalidoException`, `PagamentoRecusadoException`).
- `schema.sql` como fonte da verdade (`ddl-auto=validate`), agora versionado em `src/main/resources/db/schema.sql`.
- Políticas de exclusão da Aula 13: pedido→item **CASCADE** (composição) e pedido→cliente / item→produto **RESTRICT** (associação).

## 4. Relacionamentos e fetch

| Relacionamento | Mapeamento | Fetch | Por quê |
|---|---|---|---|
| Pedido → Cliente | `@ManyToOne(optional = false)`, sem cascade | **LAZY** (ajustado) | O padrão EAGER buscaria o cliente em todo SELECT de pedido (N+1) |
| Pedido → ItemPedido | `@OneToMany(mappedBy, cascade = ALL, orphanRemoval = true)` | LAZY (padrão) | Composição: item não existe sem o pedido |
| ItemPedido → Produto | `@ManyToOne(optional = false)` | **LAZY** (ajustado) | Evita um SELECT de produto por item (N+1) |

Quando o pedido precisa vir completo, `PedidoRepository.buscarComItens` usa `@Query` com `JOIN FETCH`
(pedido + cliente + itens + produtos em um único SELECT).

## 5. Consultas migradas

| Consulta do DAO / SQL | Equivalente no Spring Data |
|---|---|
| `SELECT ... WHERE nome LIKE ?` | `findByNomeContainingIgnoreCase(String)` |
| `SELECT ... WHERE ativo = TRUE` | `findByAtivoTrue()` |
| `SELECT ... WHERE cliente_id = ?` | `findByClienteId(Long)` |
| `SELECT ... WHERE situacao = ?` | `findBySituacao(SituacaoPedido)` |
| `SELECT COUNT(*) ... WHERE email = ?` | `existsByEmail(String)` |
| JOIN pedido/itens/produto | `@Query("SELECT DISTINCT p FROM Pedido p JOIN FETCH ... WHERE p.id = :id")` |

## 6. Camadas

| Camada | Pacote | O que tem | O que NUNCA tem |
|---|---|---|---|
| Entity | `modelo` | Estado, validação, comportamento (`adicionarItem`, `pagar`, `cancelar`, `baixarEstoque`) | Acesso a banco |
| Repository | `repositorio` | Consultas | Regra de negócio |
| Service | `servico` | Regra de negócio, orquestração, `@Transactional` | SQL, HTTP |

## 7. Decisões e pontos de atenção

- **Rollback com exceção verificada:** as exceções da Aula 09 estendem `Exception`. Por padrão o
  `@Transactional` só desfaz com `RuntimeException`; por isso `ServicoPedido` usa
  `@Transactional(rollbackFor = ECommerceException.class)`. Teste que prova: `faltaDeEstoqueFazRollback`.
- **Exceções novas de serviço** (`ProdutoNaoEncontradoException`, `ProdutoDuplicadoException`, `Cliente...`,
  `PedidoNaoEncontradoException`) são `RuntimeException` — na Aula 15 viram 404 / 409.
- **Dirty checking:** `alterarPreco`, `reporEstoque` e `adicionarItem` não chamam `save()`; o Hibernate
  grava o UPDATE no commit porque a entidade está gerenciada dentro da transação.
- **Preço congelado no item:** `ItemPedido.precoUnitario` guarda o preço do momento da venda.
- **Produto excluído logicamente** (`ativo = false`): produto já vendido não pode ser apagado (RESTRICT).
- **Situação como VARCHAR:** `@JdbcTypeCode(SqlTypes.VARCHAR)` porque o Hibernate 6 usaria o tipo `ENUM`
  nativo do MySQL e o `validate` falharia contra o `VARCHAR(20)` do schema.
- `spring.jpa.open-in-view=false`: a sessão do JPA não fica aberta fora do serviço.

## 8. Testes

| Teste | Tipo | Precisa de banco? |
|---|---|---|
| `modelo/ProdutoTest`, `modelo/PedidoTest` | Unitário do domínio | Não |
| `servico/ServicoProdutoTest` | Unitário com Mockito | Não |
| `servico/ServicoPedidoIntegracaoTest` | `@SpringBootTest` | H2 em memória (só no teste) |
| `fluxo/PedidoFluxoIT` | Fluxo do domínio | Não |

**Acoplamento escondido encontrado:** os testes antigos estavam em `src/test/modelo` (fora do
`src/test/java`, então o Maven nem os executava) e usavam construtores que não existiam mais
(`Produto("PROD-01", ...)`, `Pedido("PED-001", cliente)`, `pagarCom("PIX")`). O `ProdutoDAOTest`
dependia de um MySQL com o produto de id 1 cadastrado. Os testes foram movidos para o lugar certo e
ajustados para o domínio atual; o do DAO saiu junto com o DAO.

## 9. Pendente (Aula 15)

- Controladores REST (`@RestController`) e mapeamento das exceções para códigos HTTP (`@RestControllerAdvice`).
- Pipeline de CI/CD (GitHub Actions) rodando `mvn test` a cada push — os testes já rodam sem MySQL.
- `Funcionario` ainda não é persistido (não há tabela; fica para quando houver caso de uso).
