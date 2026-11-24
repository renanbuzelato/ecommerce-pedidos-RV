const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' });
const dataHora = new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' });

export const formatarMoeda = (valor) => moeda.format(Number(valor) || 0);

export const formatarDataHora = (iso) => (iso ? dataHora.format(new Date(iso)) : '—');

export const somenteDigitos = (texto = '') => String(texto).replace(/\D/g, '');

/** Máscaras simples aplicadas enquanto o usuário digita. */
export function mascararCpf(valor) {
  const d = somenteDigitos(valor).slice(0, 11);
  return d
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2');
}

export function mascararCep(valor) {
  const d = somenteDigitos(valor).slice(0, 8);
  return d.replace(/(\d{5})(\d)/, '$1-$2');
}

export function mascararTelefone(valor) {
  const d = somenteDigitos(valor).slice(0, 11);
  if (d.length <= 10) {
    return d.replace(/(\d{2})(\d)/, '($1) $2').replace(/(\d{4})(\d)/, '$1-$2');
  }
  return d.replace(/(\d{2})(\d)/, '($1) $2').replace(/(\d{5})(\d)/, '$1-$2');
}

export function mascararCartao(valor) {
  return somenteDigitos(valor)
    .slice(0, 16)
    .replace(/(\d{4})(?=\d)/g, '$1 ');
}
