import { describe, expect, it } from 'vitest';
import {
  validarCartao,
  validarCep,
  validarCliente,
  validarCpf,
  validarEmail,
  validarProduto,
  validarTelefone,
} from './validators';
import { mascararCpf, mascararTelefone, mascararCep } from './formatters';

describe('validadores de campo', () => {
  it.each(['529.982.247-25', '11144477735', '123.456.789-09'])('aceita CPF válido %s', (cpf) => {
    expect(validarCpf(cpf)).toBe(true);
  });

  it.each(['111.111.111-11', '529.982.247-26', '123', ''])('rejeita CPF inválido "%s"', (cpf) => {
    expect(validarCpf(cpf)).toBe(false);
  });

  it('valida e-mail, CEP e telefone', () => {
    expect(validarEmail('ana@email.com')).toBe(true);
    expect(validarEmail('ana@email')).toBe(false);
    expect(validarCep('13560-000')).toBe(true);
    expect(validarCep('1356')).toBe(false);
    expect(validarTelefone('(16) 99123-4567')).toBe(true);
    expect(validarTelefone('(16) 3371-2233')).toBe(true);
    expect(validarTelefone('(16) 89123-4567')).toBe(false);
  });

  it('aplica máscaras enquanto o usuário digita', () => {
    expect(mascararCpf('52998224725')).toBe('529.982.247-25');
    expect(mascararCep('13560000')).toBe('13560-000');
    expect(mascararTelefone('16991234567')).toBe('(16) 99123-4567');
  });
});

describe('validação de formulários', () => {
  it('aponta todos os erros do produto', () => {
    const erros = validarProduto({ nome: '', categoria: '', preco: '-1', estoque: '2.5' });
    expect(Object.keys(erros)).toEqual(['nome', 'categoria', 'preco', 'estoque']);
  });

  it('aceita produto válido', () => {
    expect(
      validarProduto({ nome: 'Caneca', categoria: 'Casa', preco: '30', estoque: '0' }),
    ).toEqual({});
  });

  it('valida cliente e cada endereço', () => {
    const erros = validarCliente({
      nome: 'Ana',
      email: 'x',
      cpf: '000',
      telefone: '1',
      enderecos: [{ cep: '', logradouro: '', numero: '', bairro: '', cidade: '', uf: '' }],
    });
    expect(erros).toHaveProperty('email');
    expect(erros).toHaveProperty('cpf');
    expect(erros.enderecos[0]).toHaveProperty('cep');
  });

  it('recusa cartão vencido', () => {
    const erros = validarCartao({
      numero: '4242424242424242',
      titular: 'Ana',
      validade: '01/20',
      cvv: '123',
    });
    expect(erros).toEqual({ validade: expect.any(String) });
  });
});
