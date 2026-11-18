package com.ecommerce.pedidos.RV.servico;

import com.ecommerce.pedidos.RV.excecao.EstoqueInsuficienteException;
import com.ecommerce.pedidos.RV.modelo.Cliente;
import com.ecommerce.pedidos.RV.modelo.Pedido;
import com.ecommerce.pedidos.RV.modelo.Produto;
import com.ecommerce.pedidos.RV.modelo.SituacaoPedido;
import com.ecommerce.pedidos.RV.modelo.pagamento.Pix;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de integração: sobe o contexto do Spring inteiro com o H2 em memória
 * (src/test/resources/application.properties). Prova que as camadas se
 * encaixam: entidade -> repositório -> serviço -> transação.
 */
@SpringBootTest
class ServicoPedidoIntegracaoTest {

    // Em teste, injeção por campo é aceitável (o JUnit é quem cria a classe de teste)
    @Autowired private ServicoCliente servicoCliente;
    @Autowired private ServicoProduto servicoProduto;
    @Autowired private ServicoPedido servicoPedido;

    @Test
    @DisplayName("Fluxo completo: cadastra, monta pedido, paga e baixa estoque no banco")
    void fluxoCompleto() throws Exception {
        Cliente ana = servicoCliente.cadastrar(new Cliente("Ana Silva", "111.111.111-11", "ana.fluxo@email.com"));
        Produto teclado = servicoProduto.cadastrar(new Produto("Teclado Fluxo", new BigDecimal("150.00"), 10));

        Pedido pedido = servicoPedido.abrir(ana.getId());
        servicoPedido.adicionarItem(pedido.getId(), teclado.getId(), 2);

        // 2 x 150 = 300 -> frete grátis (>= 200)
        assertEquals(0, new BigDecimal("300.00").compareTo(servicoPedido.calcularTotalComFrete(pedido.getId())));

        servicoPedido.pagar(pedido.getId(), new Pix(new BigDecimal("300.00"), "ana@pix.com"));

        Pedido salvo = servicoPedido.buscarComItens(pedido.getId());
        assertEquals(SituacaoPedido.PAGO, salvo.getSituacao());
        assertEquals(1, salvo.getItens().size());
        assertEquals(8, servicoProduto.buscar(teclado.getId()).getQuantidadeEmEstoque());
    }

    @Test
    @DisplayName("Falta de estoque faz rollback: nada é gravado")
    void faltaDeEstoqueFazRollback() {
        Cliente bia = servicoCliente.cadastrar(new Cliente("Bia Souza", "222.222.222-22", "bia.rollback@email.com"));
        Produto monitor = servicoProduto.cadastrar(new Produto("Monitor Rollback", new BigDecimal("900.00"), 1));
        Pedido pedido = servicoPedido.abrir(bia.getId());

        assertThrows(EstoqueInsuficienteException.class,
                () -> servicoPedido.adicionarItem(pedido.getId(), monitor.getId(), 5));

        assertTrue(servicoPedido.buscarComItens(pedido.getId()).getItens().isEmpty());
        assertEquals(1, servicoProduto.buscar(monitor.getId()).getQuantidadeEmEstoque());
    }

    @Test
    @DisplayName("Cliente com pedido não pode ser excluído (RESTRICT)")
    void clienteComPedidoNaoPodeSerExcluido() {
        Cliente caio = servicoCliente.cadastrar(new Cliente("Caio Lima", "333.333.333-33", "caio.restrict@email.com"));
        servicoPedido.abrir(caio.getId());

        assertThrows(IllegalStateException.class, () -> servicoCliente.excluir(caio.getId()));
    }
}
