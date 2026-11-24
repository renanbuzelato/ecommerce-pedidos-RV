import { describe, expect, it } from 'vitest';
import { screen, waitFor, within } from '@testing-library/react';
import { renderApp } from '../test/renderApp';

/** Testes de integração das telas: montam o App inteiro com rotas reais. */
describe('Catálogo', () => {
  it('filtra por nome e por categoria', async () => {
    const { user } = renderApp('/');
    expect(await screen.findByText('11 produtos encontrados')).toBeInTheDocument();

    await user.type(screen.getByLabelText('Buscar por nome'), 'livro');
    expect(screen.getByText('2 produtos encontrados')).toBeInTheDocument();

    await user.clear(screen.getByLabelText('Buscar por nome'));
    await user.selectOptions(screen.getByLabelText('Categoria'), 'Informática');
    expect(screen.getByText('3 produtos encontrados')).toBeInTheDocument();
  });

  it('filtra por faixa de preço', async () => {
    const { user } = renderApp('/');
    await screen.findByText('11 produtos encontrados');
    await user.type(screen.getByLabelText('Preço máx. (R$)'), '100');
    expect(screen.getByText('2 produtos encontrados')).toBeInTheDocument();
  });

  it('adiciona ao carrinho e atualiza o contador do cabeçalho', async () => {
    const { user } = renderApp('/');
    await user.click(
      await screen.findByRole('button', { name: 'Adicionar ao carrinho: Livro Código Limpo' }),
    );
    expect(screen.getByRole('link', { name: 'Carrinho, 1 item' })).toBeInTheDocument();
    expect(
      await screen.findByText('Livro Código Limpo foi adicionado ao carrinho.'),
    ).toBeInTheDocument();
  });
});

describe('Rotas', () => {
  it('mostra a página 404 para rota inexistente', async () => {
    renderApp('/nao-existe');
    expect(
      await screen.findByRole('heading', { name: 'Página não encontrada' }),
    ).toBeInTheDocument();
  });

  it('rota dinâmica de produto inexistente cai no 404', async () => {
    renderApp('/produtos/999');
    expect(await screen.findByText('O produto 999 não existe.')).toBeInTheDocument();
  });

  it('abre o detalhe do produto com indicação de estoque', async () => {
    renderApp('/produtos/2');
    expect(
      await screen.findByRole('heading', { level: 1, name: 'Smartwatch Fit 2' }),
    ).toBeInTheDocument();
    expect(screen.getByText('Últimas 3 unidades')).toBeInTheDocument();
  });
});

describe('Gestão de produtos', () => {
  it('valida o formulário e cadastra um produto', async () => {
    const { user } = renderApp('/admin/produtos/novo');
    await user.click(await screen.findByRole('button', { name: 'Cadastrar produto' }));
    expect(screen.getByText('Informe o nome do produto.')).toBeInTheDocument();
    expect(screen.getByLabelText(/Nome/)).toHaveAttribute('aria-invalid', 'true');

    await user.type(screen.getByLabelText(/Nome/), 'Caneca Térmica');
    await user.selectOptions(screen.getByLabelText(/Categoria/), 'Casa');
    await user.type(screen.getByLabelText(/Preço/), '59.9');
    await user.type(screen.getByLabelText(/Estoque/), '10');
    await user.click(screen.getByRole('button', { name: 'Cadastrar produto' }));

    expect(await screen.findByRole('heading', { name: 'Gestão de produtos' })).toBeInTheDocument();
    expect(await screen.findByText('Caneca Térmica')).toBeInTheDocument();
  });

  it('pede confirmação antes de excluir', async () => {
    const { user } = renderApp('/admin/produtos');
    await user.click(await screen.findByRole('button', { name: 'Excluir Smartwatch Fit 2' }));
    const dialogo = screen.getByRole('dialog', { name: 'Excluir produto?' });
    await user.click(within(dialogo).getByRole('button', { name: 'Sim, excluir' }));
    await waitFor(() => expect(screen.queryByText('Smartwatch Fit 2')).not.toBeInTheDocument());
  });

  it('avisa que produto já vendido não pode ser excluído', async () => {
    const { user } = renderApp('/admin/produtos');
    await user.click(await screen.findByRole('button', { name: 'Excluir Tapete de Yoga Premium' }));
    await user.click(screen.getByRole('button', { name: 'Sim, excluir' }));
    expect(await screen.findByText(/já aparece em pedidos/)).toBeInTheDocument();
  });
});

describe('Clientes', () => {
  it('rejeita CPF inválido e e-mail duplicado', async () => {
    const { user } = renderApp('/clientes/novo');
    await user.type(await screen.findByLabelText(/Nome completo/), 'Diego Alves');
    await user.type(screen.getByLabelText(/E-mail/), 'ana.souza@email.com');
    await user.type(screen.getByLabelText(/CPF/), '11111111111');
    await user.type(screen.getByLabelText(/Telefone/), '16991112222');
    await user.type(screen.getByLabelText(/CEP/), '13560000');
    await user.selectOptions(screen.getByLabelText(/UF/), 'SP');
    await user.type(screen.getByLabelText(/Logradouro/), 'Rua B');
    await user.type(screen.getByLabelText(/Número/), '10');
    await user.type(screen.getByLabelText(/Bairro/), 'Centro');
    await user.type(screen.getByLabelText(/Cidade/), 'São Carlos');
    await user.click(screen.getByRole('button', { name: 'Cadastrar cliente' }));
    expect(screen.getByText('CPF inválido. Confira os dígitos.')).toBeInTheDocument();

    await user.clear(screen.getByLabelText(/CPF/));
    await user.type(screen.getByLabelText(/CPF/), '39053344705');
    await user.click(screen.getByRole('button', { name: 'Cadastrar cliente' }));
    expect(await screen.findByText(/Já existe um cliente com o e-mail/)).toBeInTheDocument();
  });
});

describe('Pedidos', () => {
  it('filtra a lista por situação', async () => {
    const { user } = renderApp('/pedidos');
    await screen.findByRole('table');
    await user.click(screen.getByRole('button', { name: /^Cancelado/ }));
    const linhas = within(screen.getByRole('table')).getAllByRole('row');
    expect(linhas).toHaveLength(2); // cabeçalho + 1 pedido
    expect(screen.getByRole('button', { name: /^Cancelado/ })).toHaveAttribute(
      'aria-pressed',
      'true',
    );
  });

  it('avança a situação do pedido e atualiza a linha do tempo', async () => {
    const { user } = renderApp('/pedidos/2');
    await user.click(await screen.findByRole('button', { name: 'Marcar como entregue' }));
    await waitFor(() =>
      expect(screen.getByRole('listitem', { current: 'step' })).toHaveTextContent('Entregue'),
    );
  });
});

describe('Fluxo completo de compra', () => {
  it('carrinho → checkout → pagamento recusado → nova tentativa aprovada', async () => {
    const { user } = renderApp('/');
    await user.click(
      await screen.findByRole('button', { name: 'Adicionar ao carrinho: Mouse Sem Fio Ergo' }),
    );
    await user.click(screen.getByRole('link', { name: /Carrinho, 1 item/ }));

    await user.click(
      await screen.findByRole('button', { name: 'Aumentar quantidade de Mouse Sem Fio Ergo' }),
    );
    expect(screen.getAllByText(/259,80/).length).toBeGreaterThan(0); // 2 × 129,90
    await user.click(screen.getByRole('link', { name: 'Finalizar compra' }));

    await user.selectOptions(await screen.findByLabelText(/Cliente/), '1');
    expect(screen.getByRole('radio', { name: /Casa/ })).toBeChecked();
    await user.click(screen.getByRole('button', { name: 'Continuar para pagamento' }));

    await user.click(screen.getByRole('radio', { name: 'Cartão de crédito' }));
    await user.type(screen.getByLabelText(/Número do cartão/), '4111111111110000');
    await user.type(screen.getByLabelText(/Nome impresso/), 'ANA SOUZA');
    await user.type(screen.getByLabelText(/Validade/), '1230');
    await user.type(screen.getByLabelText(/CVV/), '123');
    await user.click(screen.getByRole('button', { name: /Confirmar e pagar/ }));

    expect(await screen.findByText('Pagamento recusado')).toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Carrinho, 0 itens' })).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Tentar outra forma de pagamento' }));
    await user.click(screen.getByRole('radio', { name: 'Pix' }));
    await user.click(screen.getByRole('button', { name: 'Tentar pagamento' }));
    expect(await screen.findByText('Pagamento aprovado')).toBeInTheDocument();

    await user.click(screen.getByRole('link', { name: 'Acompanhar pedido' }));
    await waitFor(() =>
      expect(screen.getByRole('listitem', { current: 'step' })).toHaveTextContent('Pago'),
    );
  });
});
