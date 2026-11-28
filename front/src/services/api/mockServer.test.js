import { describe, expect, it } from 'vitest';
import { produtoService } from '../produtoService';
import { pedidoService } from '../pedidoService';
import { pagamentoService } from '../pagamentoService';
import { clienteService } from '../clienteService';

/** Regras de negócio que a API simulada espelha do back-end Java. */
describe('API simulada (regras do back-end)', () => {
  const endereco = {
    cep: '13560-000',
    logradouro: 'Rua A',
    numero: '1',
    bairro: 'Centro',
    cidade: 'São Carlos',
    uf: 'SP',
  };

  it('traduz o formato do back (quantidadeEmEstoque, documento) para o front', async () => {
    const [primeiro] = await produtoService.listar();
    expect(primeiro).toHaveProperty('estoque', 25);
    const cliente = await clienteService.buscar(1);
    expect(cliente.cpf).toBe('529.982.247-25');
  });

  it('não permite produto com nome duplicado (409)', async () => {
    await expect(
      produtoService.criar({
        nome: 'Mouse Sem Fio Ergo',
        categoria: 'Informática',
        preco: 10,
        estoque: 1,
        ativo: true,
      }),
    ).rejects.toMatchObject({ status: 409 });
  });

  it('cria pedido baixando estoque e calcula frete', async () => {
    const pedido = await pedidoService.criar({
      clienteId: 2,
      enderecoEntrega: endereco,
      itens: [{ id: 8, quantidade: 2 }], // 2 x 89,90
    });
    expect(pedido.situacao).toBe('ABERTO');
    expect(pedido.subtotal).toBeCloseTo(179.8);
    expect(pedido.frete).toBeCloseTo(19.9);
    expect((await produtoService.buscar(8)).estoque).toBe(28);
  });

  it('recusa pedido com estoque insuficiente sem alterar nada (rollback)', async () => {
    await expect(
      pedidoService.criar({
        clienteId: 1,
        enderecoEntrega: endereco,
        itens: [
          { id: 1, quantidade: 1 },
          { id: 2, quantidade: 99 },
        ],
      }),
    ).rejects.toMatchObject({ status: 409 });
    expect((await produtoService.buscar(1)).estoque).toBe(25);
  });

  it('simula pagamentos: Pix aprova, boleto pendente, cartão final 0000 recusa', async () => {
    const novo = () =>
      pedidoService.criar({
        clienteId: 1,
        enderecoEntrega: endereco,
        itens: [{ id: 4, quantidade: 1 }],
      });

    const p1 = await novo();
    expect(
      (await pagamentoService.processar({ pedidoId: p1.id, forma: 'PIX', dados: { chave: 'x' } }))
        .resultado,
    ).toBe('APROVADO');
    expect((await pedidoService.buscar(p1.id)).situacao).toBe('PAGO');

    const p2 = await novo();
    const boleto = await pagamentoService.processar({
      pedidoId: p2.id,
      forma: 'BOLETO',
      dados: { vencimento: '01/12/2026' },
    });
    expect(boleto.resultado).toBe('PENDENTE');

    const p3 = await novo();
    const cartao = await pagamentoService.processar({
      pedidoId: p3.id,
      forma: 'CARTAO',
      dados: { numero: '4111111111110000', parcelas: 1, cvv: '123' },
    });
    expect(cartao.resultado).toBe('RECUSADO');
    expect((await pedidoService.buscar(p3.id)).situacao).toBe('ABERTO');
  });

  it('pedido pago não pode ser cancelado; cancelar devolve o estoque', async () => {
    await expect(pedidoService.alterarSituacao(2, 'CANCELADO')).rejects.toThrow(
      /não é possível|pago/i,
    );
    const antes = (await produtoService.buscar(10)).estoque;
    await pedidoService.alterarSituacao(3, 'CANCELADO');
    expect((await produtoService.buscar(10)).estoque).toBe(antes + 1);
  });

  it('pagamento é sub-recurso do pedido: GET 404 antes, GET 200 depois', async () => {
    const pedido = await pedidoService.criar({
      clienteId: 2,
      enderecoEntrega: endereco,
      itens: [{ id: 4, quantidade: 1 }],
    });
    await expect(pagamentoService.buscar(pedido.id)).rejects.toMatchObject({ status: 404 });
    await pagamentoService.processar({ pedidoId: pedido.id, forma: 'PIX', dados: { chave: 'x' } });
    expect(await pagamentoService.buscar(pedido.id)).toMatchObject({
      pedidoId: pedido.id,
      resultado: 'APROVADO',
    });
  });

  it('não exclui produto que já foi vendido (RESTRICT)', async () => {
    await expect(produtoService.excluir(1)).rejects.toMatchObject({ status: 409 });
  });
});
