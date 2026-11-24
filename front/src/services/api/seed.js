/**
 * Dados iniciais da API simulada, já no FORMATO DO BACK-END
 * (quantidadeEmEstoque, documento, situacao...), para que os mappers
 * sejam exercitados exatamente como serão com a API real.
 */
const agora = Date.now();
const diasAtras = (d, h = 0) => new Date(agora - d * 86400000 - h * 3600000).toISOString();

export function criarSeed() {
  const produtos = [
    {
      id: 1,
      nome: 'Fone Bluetooth ProSound',
      descricao: 'Fone sem fio com cancelamento de ruído e 30 h de bateria.',
      categoria: 'Eletrônicos',
      preco: '349.90',
      quantidadeEmEstoque: 25,
      ativo: true,
    },
    {
      id: 2,
      nome: 'Smartwatch Fit 2',
      descricao: 'Monitor cardíaco, GPS integrado e resistência à água.',
      categoria: 'Eletrônicos',
      preco: '599.00',
      quantidadeEmEstoque: 3,
      ativo: true,
    },
    {
      id: 3,
      nome: 'Teclado Mecânico RGB',
      descricao: 'Switches táteis, layout ABNT2 e iluminação configurável.',
      categoria: 'Informática',
      preco: '289.90',
      quantidadeEmEstoque: 14,
      ativo: true,
    },
    {
      id: 4,
      nome: 'Mouse Sem Fio Ergo',
      descricao: 'Design ergonômico, 6 botões e sensor de 4000 DPI.',
      categoria: 'Informática',
      preco: '129.90',
      quantidadeEmEstoque: 40,
      ativo: true,
    },
    {
      id: 5,
      nome: 'Monitor 27" QHD',
      descricao: 'Painel IPS 2560×1440, 75 Hz, ajuste de altura.',
      categoria: 'Informática',
      preco: '1599.00',
      quantidadeEmEstoque: 0,
      ativo: true,
    },
    {
      id: 6,
      nome: 'Cafeteira Expresso Barista',
      descricao: 'Pressão de 15 bar e vaporizador para leite.',
      categoria: 'Casa',
      preco: '799.90',
      quantidadeEmEstoque: 8,
      ativo: true,
    },
    {
      id: 7,
      nome: 'Jogo de Panelas Antiaderente',
      descricao: 'Cinco peças com revestimento cerâmico.',
      categoria: 'Casa',
      preco: '459.00',
      quantidadeEmEstoque: 12,
      ativo: true,
    },
    {
      id: 8,
      nome: 'Livro Código Limpo',
      descricao: 'Clássico sobre boas práticas de programação.',
      categoria: 'Livros',
      preco: '89.90',
      quantidadeEmEstoque: 30,
      ativo: true,
    },
    {
      id: 9,
      nome: 'Livro Entendendo Algoritmos',
      descricao: 'Guia ilustrado para iniciantes em algoritmos.',
      categoria: 'Livros',
      preco: '64.90',
      quantidadeEmEstoque: 2,
      ativo: true,
    },
    {
      id: 10,
      nome: 'Bola de Futebol Oficial',
      descricao: 'Tamanho 5, costurada à máquina.',
      categoria: 'Esportes',
      preco: '119.90',
      quantidadeEmEstoque: 20,
      ativo: true,
    },
    {
      id: 11,
      nome: 'Tapete de Yoga Premium',
      descricao: 'Material antiderrapante, 6 mm de espessura.',
      categoria: 'Esportes',
      preco: '149.00',
      quantidadeEmEstoque: 16,
      ativo: true,
    },
    {
      id: 12,
      nome: 'Caixa de Som Portátil',
      descricao: 'Modelo descontinuado (exclusão lógica: ativo = false).',
      categoria: 'Eletrônicos',
      preco: '199.90',
      quantidadeEmEstoque: 5,
      ativo: false,
    },
  ];

  const clientes = [
    {
      id: 1,
      nome: 'Ana Souza',
      documento: '529.982.247-25',
      email: 'ana.souza@email.com',
      telefone: '(16) 99123-4567',
      enderecos: [
        {
          id: 1,
          apelido: 'Casa',
          cep: '13560-000',
          logradouro: 'Rua Episcopal',
          numero: '1200',
          complemento: 'Apto 12',
          bairro: 'Centro',
          cidade: 'São Carlos',
          uf: 'SP',
        },
        {
          id: 2,
          apelido: 'Trabalho',
          cep: '13566-590',
          logradouro: 'Av. Trabalhador São-carlense',
          numero: '400',
          complemento: '',
          bairro: 'Parque Arnold Schimidt',
          cidade: 'São Carlos',
          uf: 'SP',
        },
      ],
    },
    {
      id: 2,
      nome: 'Bruno Lima',
      documento: '111.444.777-35',
      email: 'bruno.lima@email.com',
      telefone: '(16) 3371-2233',
      enderecos: [
        {
          id: 3,
          apelido: 'Casa',
          cep: '14801-020',
          logradouro: 'Rua Voluntários da Pátria',
          numero: '85',
          complemento: '',
          bairro: 'Centro',
          cidade: 'Araraquara',
          uf: 'SP',
        },
      ],
    },
    {
      id: 3,
      nome: 'Carla Mendes',
      documento: '123.456.789-09',
      email: 'carla.mendes@email.com',
      telefone: '(11) 98888-7777',
      enderecos: [
        {
          id: 4,
          apelido: 'Casa',
          cep: '01310-100',
          logradouro: 'Av. Paulista',
          numero: '1578',
          complemento: 'Bloco B',
          bairro: 'Bela Vista',
          cidade: 'São Paulo',
          uf: 'SP',
        },
      ],
    },
  ];

  const item = (id, produto, quantidade) => ({
    id,
    produto: { id: produto.id, nome: produto.nome },
    quantidade,
    precoUnitario: produto.preco,
  });

  const pedidos = [
    {
      id: 1,
      cliente: { id: 1, nome: 'Ana Souza' },
      enderecoEntrega: clientes[0].enderecos[0],
      itens: [item(1, produtos[0], 1), item(2, produtos[7], 2)],
      situacao: 'ENTREGUE',
      dataCriacao: diasAtras(12),
      historico: [
        { situacao: 'ABERTO', data: diasAtras(12) },
        { situacao: 'PAGO', data: diasAtras(12) },
        { situacao: 'ENVIADO', data: diasAtras(10) },
        { situacao: 'ENTREGUE', data: diasAtras(7) },
      ],
      pagamento: {
        forma: 'CARTAO',
        resultado: 'APROVADO',
        comprovante: 'CARD-1001',
        descricao: 'Cartão final 4242 (2x)',
      },
    },
    {
      id: 2,
      cliente: { id: 2, nome: 'Bruno Lima' },
      enderecoEntrega: clientes[1].enderecos[0],
      itens: [item(3, produtos[2], 1)],
      situacao: 'ENVIADO',
      dataCriacao: diasAtras(3),
      historico: [
        { situacao: 'ABERTO', data: diasAtras(3) },
        { situacao: 'PAGO', data: diasAtras(3) },
        { situacao: 'ENVIADO', data: diasAtras(1) },
      ],
      pagamento: {
        forma: 'PIX',
        resultado: 'APROVADO',
        comprovante: 'PIX-2002',
        descricao: 'Pix chave bruno.lima@email.com',
      },
    },
    {
      id: 3,
      cliente: { id: 3, nome: 'Carla Mendes' },
      enderecoEntrega: clientes[2].enderecos[0],
      itens: [item(4, produtos[9], 1), item(5, produtos[10], 1)],
      situacao: 'ABERTO',
      dataCriacao: diasAtras(0, 5),
      historico: [{ situacao: 'ABERTO', data: diasAtras(0, 5) }],
      pagamento: {
        forma: 'BOLETO',
        resultado: 'PENDENTE',
        comprovante: 'BOL-3003',
        descricao: 'Boleto com vencimento em 3 dias',
      },
    },
    {
      id: 4,
      cliente: { id: 1, nome: 'Ana Souza' },
      enderecoEntrega: clientes[0].enderecos[1],
      itens: [item(6, produtos[5], 1)],
      situacao: 'CANCELADO',
      dataCriacao: diasAtras(20),
      historico: [
        { situacao: 'ABERTO', data: diasAtras(20) },
        { situacao: 'CANCELADO', data: diasAtras(19) },
      ],
      pagamento: null,
    },
  ];

  return {
    produtos,
    clientes,
    pedidos,
    sequencias: { produtos: 12, clientes: 3, pedidos: 4, itens: 6, enderecos: 4 },
  };
}
