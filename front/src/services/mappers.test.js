import { describe, expect, it } from 'vitest';
import { clienteDaApi, pedidoDaApi, produtoDaApi } from './mappers';

/**
 * JSON exatamente como o back-end Spring Boot devolve (records ProdutoResposta,
 * ClienteResposta e PedidoResposta): BigDecimal vira número, LocalDateTime vira
 * texto sem fuso, campos opcionais vêm como null.
 */
describe('mappers com o JSON real do back-end', () => {
  it('produto', () => {
    const p = produtoDaApi({
      id: 7,
      nome: 'Teclado',
      descricao: null,
      categoria: 'Informática',
      preco: 289.9,
      quantidadeEmEstoque: 14,
      ativo: true,
    });
    expect(p).toMatchObject({ preco: 289.9, estoque: 14, descricao: '' });
  });

  it('cliente com campos nulos vira texto vazio (formulário controlado)', () => {
    const c = clienteDaApi({
      id: 1,
      nome: 'Ana',
      documento: '529.982.247-25',
      email: 'ana@x.com',
      telefone: null,
      enderecos: [
        {
          id: 3,
          apelido: null,
          cep: '13560-000',
          logradouro: 'Rua A',
          numero: '1',
          complemento: null,
          bairro: 'Centro',
          cidade: 'São Carlos',
          uf: 'SP',
        },
      ],
    });
    expect(c.cpf).toBe('529.982.247-25');
    expect(c.telefone).toBe('');
    expect(c.enderecos[0]).toMatchObject({ id: 3, apelido: '', complemento: '' });
  });

  it('pedido', () => {
    const p = pedidoDaApi({
      id: 5,
      cliente: { id: 1, nome: 'Ana' },
      enderecoEntrega: {
        id: null,
        apelido: null,
        cep: '13560-000',
        logradouro: 'Rua A',
        numero: '1',
        complemento: null,
        bairro: 'Centro',
        cidade: 'São Carlos',
        uf: 'SP',
      },
      itens: [
        {
          id: 9,
          produto: { id: 4, nome: 'Mouse' },
          quantidade: 2,
          precoUnitario: 129.9,
          precoTotal: 259.8,
        },
      ],
      situacao: 'ABERTO',
      dataCriacao: '2026-11-24T19:10:00.123456',
      valorTotal: 259.8,
      frete: 0,
      totalComFrete: 259.8,
      historico: [{ situacao: 'ABERTO', data: '2026-11-24T19:10:00.123456' }],
      pagamento: {
        forma: 'CARTAO',
        resultado: 'RECUSADO',
        comprovante: 'CARD-1',
        descricao: 'Cartão final 0000 (1x)',
      },
    });
    expect(p.itens[0]).toMatchObject({ produtoId: 4, nome: 'Mouse', precoTotal: 259.8 });
    expect(p).toMatchObject({ subtotal: 259.8, frete: 0, total: 259.8, situacao: 'ABERTO' });
    expect(p.pagamento.resultado).toBe('RECUSADO');
  });
});
