import { somenteDigitos } from './formatters';

export function validarEmail(email = '') {
  return /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/.test(email.trim());
}

/** CPF com cálculo dos dois dígitos verificadores. */
export function validarCpf(cpf = '') {
  const d = somenteDigitos(cpf);
  if (d.length !== 11 || /^(\d)\1{10}$/.test(d)) return false;

  const calcularDigito = (base) => {
    const soma = base
      .split('')
      .reduce((acc, num, i) => acc + Number(num) * (base.length + 1 - i), 0);
    const resto = (soma * 10) % 11;
    return resto === 10 ? 0 : resto;
  };

  const d1 = calcularDigito(d.slice(0, 9));
  const d2 = calcularDigito(d.slice(0, 10));
  return d1 === Number(d[9]) && d2 === Number(d[10]);
}

export function validarCep(cep = '') {
  return somenteDigitos(cep).length === 8;
}

/** Fixo (10 dígitos) ou celular (11 dígitos, começando com 9 após o DDD). */
export function validarTelefone(tel = '') {
  const d = somenteDigitos(tel);
  if (d.length === 10) return true;
  return d.length === 11 && d[2] === '9';
}

export const UFS = [
  'AC',
  'AL',
  'AP',
  'AM',
  'BA',
  'CE',
  'DF',
  'ES',
  'GO',
  'MA',
  'MT',
  'MS',
  'MG',
  'PA',
  'PB',
  'PR',
  'PE',
  'PI',
  'RJ',
  'RN',
  'RS',
  'RO',
  'RR',
  'SC',
  'SP',
  'SE',
  'TO',
];

/* ---------- Validações de formulário (retornam objeto { campo: mensagem }) ---------- */

export function validarProduto(p) {
  const erros = {};
  if (!p.nome?.trim()) erros.nome = 'Informe o nome do produto.';
  else if (p.nome.trim().length < 3) erros.nome = 'O nome deve ter pelo menos 3 caracteres.';
  if (!p.categoria) erros.categoria = 'Selecione uma categoria.';
  const preco = Number(p.preco);
  if (p.preco === '' || Number.isNaN(preco)) erros.preco = 'Informe o preço.';
  else if (preco <= 0) erros.preco = 'O preço deve ser maior que zero.';
  const estoque = Number(p.estoque);
  if (p.estoque === '' || !Number.isInteger(estoque)) erros.estoque = 'Informe um número inteiro.';
  else if (estoque < 0) erros.estoque = 'O estoque não pode ser negativo.';
  return erros;
}

export function validarEndereco(e) {
  const erros = {};
  if (!validarCep(e.cep)) erros.cep = 'CEP deve ter 8 dígitos.';
  if (!e.logradouro?.trim()) erros.logradouro = 'Informe o logradouro.';
  if (!e.numero?.trim()) erros.numero = 'Informe o número.';
  if (!e.bairro?.trim()) erros.bairro = 'Informe o bairro.';
  if (!e.cidade?.trim()) erros.cidade = 'Informe a cidade.';
  if (!UFS.includes(e.uf)) erros.uf = 'Selecione a UF.';
  return erros;
}

export function validarCliente(c) {
  const erros = {};
  if (!c.nome?.trim()) erros.nome = 'Informe o nome completo.';
  if (!validarEmail(c.email)) erros.email = 'Informe um e-mail válido (ex.: nome@dominio.com).';
  if (!validarCpf(c.cpf)) erros.cpf = 'CPF inválido. Confira os dígitos.';
  if (!validarTelefone(c.telefone)) erros.telefone = 'Telefone inválido. Use DDD + número.';
  const enderecos = (c.enderecos || []).map(validarEndereco);
  if (enderecos.some((e) => Object.keys(e).length)) erros.enderecos = enderecos;
  return erros;
}

export const temErros = (erros) => Object.keys(erros).length > 0;

/** Cartão (simulado): 16 dígitos, titular, validade MM/AA não vencida e CVV. */
export function validarCartao(c) {
  const erros = {};
  if (somenteDigitos(c.numero).length !== 16)
    erros.numero = 'O número do cartão deve ter 16 dígitos.';
  if (!c.titular?.trim()) erros.titular = 'Informe o nome impresso no cartão.';
  const [mm, aa] = (c.validade || '').split('/').map(Number);
  const hoje = new Date();
  const fimDoMes = new Date(2000 + aa, mm, 0, 23, 59);
  if (!(mm >= 1 && mm <= 12) || !aa || fimDoMes < hoje)
    erros.validade = 'Validade inválida ou vencida (MM/AA).';
  if (!/^\d{3,4}$/.test(c.cvv || '')) erros.cvv = 'CVV deve ter 3 ou 4 dígitos.';
  return erros;
}
