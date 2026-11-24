package com.ecommerce.pedidos.RV.controlador;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de integração da API REST (Aula 15): requisição HTTP simulada (MockMvc)
 * -> controlador -> serviço -> repositório -> H2. Usa exatamente o JSON que o
 * front-end React envia, então também valida o contrato front x back.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ApiRestIntegracaoTest {

    private static final AtomicInteger SEQ = new AtomicInteger();

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper json;

    private static final String ENDERECO = """
            { "apelido": "Casa", "cep": "13560-000", "logradouro": "Rua Episcopal", "numero": "1200",
              "complemento": "Apto 12", "bairro": "Centro", "cidade": "São Carlos", "uf": "SP" }""";

    /* ------------------------------ auxiliares ------------------------------ */

    private ResultActions enviar(org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder req,
                                 String corpo) throws Exception {
        return mvc.perform(req.contentType(MediaType.APPLICATION_JSON).content(corpo));
    }

    private JsonNode corpo(ResultActions r) throws Exception {
        return json.readTree(r.andReturn().getResponse().getContentAsString());
    }

    private long criarProduto(String nome, String preco, int estoque) throws Exception {
        String body = """
                { "nome": "%s", "descricao": "Teste", "categoria": "Informática",
                  "preco": "%s", "quantidadeEmEstoque": %d, "ativo": true }""".formatted(nome, preco, estoque);
        return corpo(enviar(post("/api/produtos"), body).andExpect(status().isCreated())).get("id").asLong();
    }

    private long criarCliente() throws Exception {
        int n = SEQ.incrementAndGet();
        String body = """
                { "nome": "Cliente %d", "documento": "000.000.%03d-00", "email": "cliente%d.api@email.com",
                  "telefone": "(16) 99999-0000", "enderecos": [ %s ] }""".formatted(n, n, n, ENDERECO);
        return corpo(enviar(post("/api/clientes"), body).andExpect(status().isCreated())).get("id").asLong();
    }

    private long criarPedido(long clienteId, long produtoId, int quantidade) throws Exception {
        String body = """
                { "clienteId": %d, "enderecoEntrega": %s, "itens": [ { "produtoId": %d, "quantidade": %d } ] }"""
                .formatted(clienteId, ENDERECO, produtoId, quantidade);
        return corpo(enviar(post("/api/pedidos"), body).andExpect(status().isCreated())).get("id").asLong();
    }

    private ResultActions pagar(long pedidoId, String forma, String dados) throws Exception {
        return enviar(post("/api/pagamentos"),
                "{ \"pedidoId\": %d, \"forma\": \"%s\", \"dados\": %s }".formatted(pedidoId, forma, dados));
    }

    private String nome(String base) {
        return base + " " + SEQ.incrementAndGet();
    }

    /* -------------------------------- produtos ------------------------------- */

    @Test
    @DisplayName("POST /api/produtos devolve 201 com Location e o JSON no formato do contrato")
    void cadastraProduto() throws Exception {
        String nome = nome("Teclado API");
        enviar(post("/api/produtos"), """
                { "nome": "%s", "categoria": "Informática", "preco": "289.90", "quantidadeEmEstoque": 14 }"""
                .formatted(nome))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/produtos/")))
                .andExpect(jsonPath("$.nome").value(nome))
                .andExpect(jsonPath("$.categoria").value("Informática"))
                .andExpect(jsonPath("$.quantidadeEmEstoque").value(14))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    @Test
    @DisplayName("Produto inválido -> 400; nome repetido -> 409; id inexistente -> 404")
    void errosDeProduto() throws Exception {
        enviar(post("/api/produtos"), """
                { "nome": "", "categoria": "Casa", "preco": "-1", "quantidadeEmEstoque": 1 }""")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));

        String nome = nome("Mouse Duplicado");
        criarProduto(nome, "50.00", 1);
        enviar(post("/api/produtos"), """
                { "nome": "%s", "categoria": "Casa", "preco": "10", "quantidadeEmEstoque": 1 }""".formatted(nome))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem", containsString("Já existe")));

        mvc.perform(get("/api/produtos/999999")).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/produtos/{id} edita todos os campos, inclusive estoque e ativo")
    void editaProduto() throws Exception {
        long id = criarProduto(nome("Cafeteira"), "799.90", 8);
        String novoNome = nome("Cafeteira Barista");
        enviar(put("/api/produtos/" + id), """
                { "nome": "%s", "descricao": "Nova", "categoria": "Casa", "preco": "749.90",
                  "quantidadeEmEstoque": 3, "ativo": false }""".formatted(novoNome))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value(novoNome))
                .andExpect(jsonPath("$.preco").value(749.90))
                .andExpect(jsonPath("$.quantidadeEmEstoque").value(3))
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    @DisplayName("DELETE: produto nunca vendido -> 204; produto já vendido -> 422 (RESTRICT)")
    void excluiProduto() throws Exception {
        long livre = criarProduto(nome("Produto Livre"), "10.00", 1);
        mvc.perform(delete("/api/produtos/" + livre)).andExpect(status().isNoContent());
        mvc.perform(get("/api/produtos/" + livre)).andExpect(status().isNotFound());

        long vendido = criarProduto(nome("Produto Vendido"), "10.00", 5);
        criarPedido(criarCliente(), vendido, 1);
        mvc.perform(delete("/api/produtos/" + vendido))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem", containsString("já aparece em pedidos")));
    }

    /* -------------------------------- clientes ------------------------------- */

    @Test
    @DisplayName("Cliente: cadastro com endereços, e-mail repetido -> 409, edição troca endereços")
    void clientes() throws Exception {
        long id = criarCliente();
        JsonNode cliente = corpo(mvc.perform(get("/api/clientes/" + id)).andExpect(status().isOk()));
        String email = cliente.get("email").asText();
        mvc.perform(get("/api/clientes/" + id))
                .andExpect(jsonPath("$.enderecos", hasSize(1)))
                .andExpect(jsonPath("$.enderecos[0].cidade").value("São Carlos"));

        enviar(post("/api/clientes"), """
                { "nome": "Outro", "documento": "999.999.999-99", "email": "%s", "enderecos": [] }"""
                .formatted(email))
                .andExpect(status().isConflict());

        enviar(put("/api/clientes/" + id), """
                { "nome": "Nome Editado", "documento": "%s", "email": "%s", "telefone": "(16) 3333-4444",
                  "enderecos": [ %s, %s ] }""".formatted(cliente.get("documento").asText(), email, ENDERECO, ENDERECO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Editado"))
                .andExpect(jsonPath("$.enderecos", hasSize(2)));
    }

    /* ------------------------- pedidos e pagamentos -------------------------- */

    @Test
    @DisplayName("Pedido: criação baixa estoque, calcula frete e começa ABERTO com histórico")
    void criaPedido() throws Exception {
        long produto = criarProduto(nome("Livro"), "89.90", 30);
        long pedido = criarPedido(criarCliente(), produto, 2);

        mvc.perform(get("/api/pedidos/" + pedido))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("ABERTO"))
                .andExpect(jsonPath("$.itens[0].quantidade").value(2))
                .andExpect(jsonPath("$.valorTotal").value(179.80))
                .andExpect(jsonPath("$.frete").value(19.90))
                .andExpect(jsonPath("$.totalComFrete").value(199.70))
                .andExpect(jsonPath("$.enderecoEntrega.cidade").value("São Carlos"))
                .andExpect(jsonPath("$.historico[0].situacao").value("ABERTO"))
                .andExpect(jsonPath("$.pagamento").doesNotExist());
        mvc.perform(get("/api/produtos/" + produto)).andExpect(jsonPath("$.quantidadeEmEstoque").value(28));
        mvc.perform(get("/api/pedidos")).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Estoque insuficiente -> 422 e nada é gravado (rollback)")
    void estoqueInsuficiente() throws Exception {
        long ok = criarProduto(nome("Com Estoque"), "10.00", 10);
        long pouco = criarProduto(nome("Sem Estoque"), "10.00", 1);
        String body = """
                { "clienteId": %d, "enderecoEntrega": %s,
                  "itens": [ { "produtoId": %d, "quantidade": 3 }, { "produtoId": %d, "quantidade": 5 } ] }"""
                .formatted(criarCliente(), ENDERECO, ok, pouco);
        enviar(post("/api/pedidos"), body)
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.mensagem", containsString("Estoque insuficiente")));
        mvc.perform(get("/api/produtos/" + ok)).andExpect(jsonPath("$.quantidadeEmEstoque").value(10));
    }

    @Test
    @DisplayName("Pix aprova -> PAGO -> ENVIADO -> ENTREGUE; pago não pode ser cancelado")
    void cicloDeVidaComPix() throws Exception {
        long pedido = criarPedido(criarCliente(), criarProduto(nome("Fone"), "349.90", 5), 1);

        pagar(pedido, "PIX", "{ \"chave\": \"ana@pix.com\" }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value("APROVADO"))
                .andExpect(jsonPath("$.comprovante", containsString("PIX-")));

        enviar(patch("/api/pedidos/" + pedido), "{ \"situacao\": \"CANCELADO\" }")
                .andExpect(status().isUnprocessableEntity());
        pagar(pedido, "PIX", "{ \"chave\": \"x\" }").andExpect(status().isUnprocessableEntity());

        enviar(patch("/api/pedidos/" + pedido), "{ \"situacao\": \"ENVIADO\" }").andExpect(status().isOk());
        enviar(patch("/api/pedidos/" + pedido), "{ \"situacao\": \"ENTREGUE\" }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("ENTREGUE"))
                .andExpect(jsonPath("$.historico", hasSize(4)))
                .andExpect(jsonPath("$.pagamento.resultado").value("APROVADO"));
    }

    @Test
    @DisplayName("Cartão final 0000 é recusado (pedido continua ABERTO) e dá para tentar de novo")
    void cartaoRecusadoENovaTentativa() throws Exception {
        long pedido = criarPedido(criarCliente(), criarProduto(nome("Mouse"), "129.90", 5), 1);

        pagar(pedido, "CARTAO", "{ \"numero\": \"4111111111110000\", \"titular\": \"ANA\", \"parcelas\": 3 }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultado").value("RECUSADO"))
                .andExpect(jsonPath("$.descricao").value("Cartão final 0000 (3x)"));
        mvc.perform(get("/api/pedidos/" + pedido))
                .andExpect(jsonPath("$.situacao").value("ABERTO"))
                .andExpect(jsonPath("$.pagamento.resultado").value("RECUSADO"));

        pagar(pedido, "CARTAO", "{ \"numero\": \"4242 4242 4242 4242\", \"parcelas\": 1 }")
                .andExpect(jsonPath("$.resultado").value("APROVADO"));
        mvc.perform(get("/api/pedidos/" + pedido)).andExpect(jsonPath("$.situacao").value("PAGO"));
    }

    @Test
    @DisplayName("Boleto fica PENDENTE até a compensação (PATCH situacao=PAGO)")
    void boletoPendenteECompensacao() throws Exception {
        long pedido = criarPedido(criarCliente(), criarProduto(nome("Bola"), "119.90", 5), 1);

        pagar(pedido, "BOLETO", "{ \"vencimento\": \"27/11/2026\" }")
                .andExpect(jsonPath("$.resultado").value("PENDENTE"));
        enviar(patch("/api/pedidos/" + pedido), "{ \"situacao\": \"PAGO\" }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("PAGO"))
                .andExpect(jsonPath("$.pagamento.resultado").value("APROVADO"));
    }

    @Test
    @DisplayName("Cancelar pedido em aberto devolve o estoque")
    void cancelarDevolveEstoque() throws Exception {
        long produto = criarProduto(nome("Tapete"), "149.00", 4);
        long pedido = criarPedido(criarCliente(), produto, 3);
        enviar(patch("/api/pedidos/" + pedido), "{ \"situacao\": \"CANCELADO\" }")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("CANCELADO"));
        mvc.perform(get("/api/produtos/" + produto)).andExpect(jsonPath("$.quantidadeEmEstoque").value(4));
    }

    @Test
    @DisplayName("Pedido sem itens -> 400 (Bean Validation)")
    void pedidoSemItens() throws Exception {
        enviar(post("/api/pedidos"), """
                { "clienteId": %d, "enderecoEntrega": %s, "itens": [] }""".formatted(criarCliente(), ENDERECO))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value("Não é possível criar um pedido sem itens."));
    }
}
