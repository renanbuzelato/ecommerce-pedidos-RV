import { useState } from 'react';
import { Button, ButtonLink, Card, Input, Select } from '../ui';
import { mascararCep, mascararCpf, mascararTelefone } from '../../utils/formatters';
import { temErros, UFS, validarCliente } from '../../utils/validators';
import { focarPrimeiroErro } from '../../utils/foco';

const ENDERECO_VAZIO = {
  apelido: '',
  cep: '',
  logradouro: '',
  numero: '',
  complemento: '',
  bairro: '',
  cidade: '',
  uf: '',
};
const VAZIO = {
  nome: '',
  email: '',
  cpf: '',
  telefone: '',
  enderecos: [{ ...ENDERECO_VAZIO, apelido: 'Casa' }],
};

/**
 * Formulário controlado de cliente, com lista dinâmica de endereços
 * (o checkout permite escolher entre eles).
 */
export function ClienteForm({ inicial, aoSalvar, salvando = false }) {
  const [valores, setValores] = useState(() =>
    inicial ? structuredClone(inicial) : structuredClone(VAZIO),
  );
  const [erros, setErros] = useState({});
  const [tentouEnviar, setTentouEnviar] = useState(false);

  function atualizar(novos) {
    setValores(novos);
    if (tentouEnviar) setErros(validarCliente(novos));
  }

  const alterar = (campo, mascara) => (e) =>
    atualizar({ ...valores, [campo]: mascara ? mascara(e.target.value) : e.target.value });

  const alterarEndereco = (i, campo, mascara) => (e) => {
    const enderecos = valores.enderecos.map((end, idx) =>
      idx === i ? { ...end, [campo]: mascara ? mascara(e.target.value) : e.target.value } : end,
    );
    atualizar({ ...valores, enderecos });
  };

  const adicionarEndereco = () =>
    atualizar({ ...valores, enderecos: [...valores.enderecos, { ...ENDERECO_VAZIO }] });

  const removerEndereco = (i) =>
    atualizar({ ...valores, enderecos: valores.enderecos.filter((_, idx) => idx !== i) });

  function enviar(e) {
    e.preventDefault();
    const form = e.currentTarget;
    setTentouEnviar(true);
    const encontrados = validarCliente(valores);
    setErros(encontrados);
    if (temErros(encontrados)) return focarPrimeiroErro(form);
    aoSalvar(valores);
  }

  const erroEnd = (i, campo) => erros.enderecos?.[i]?.[campo];

  return (
    <Card
      as="form"
      noValidate
      onSubmit={enviar}
      className="space-y-6 p-6"
      aria-label="Dados do cliente"
    >
      <p className="text-sm text-slate-600">
        Campos marcados com <span className="text-red-700">*</span> são obrigatórios.
      </p>
      <fieldset className="grid gap-5 sm:grid-cols-2">
        <legend className="mb-2 text-lg font-bold text-slate-900">Dados pessoais</legend>
        <Input
          label="Nome completo"
          obrigatorio
          autoComplete="name"
          value={valores.nome}
          erro={erros.nome}
          onChange={alterar('nome')}
          className="sm:col-span-2"
        />
        <Input
          label="E-mail"
          obrigatorio
          type="email"
          autoComplete="email"
          value={valores.email}
          erro={erros.email}
          onChange={alterar('email')}
        />
        <Input
          label="CPF"
          obrigatorio
          inputMode="numeric"
          placeholder="000.000.000-00"
          value={valores.cpf}
          erro={erros.cpf}
          onChange={alterar('cpf', mascararCpf)}
        />
        <Input
          label="Telefone"
          obrigatorio
          type="tel"
          autoComplete="tel"
          placeholder="(16) 99999-9999"
          value={valores.telefone}
          erro={erros.telefone}
          onChange={alterar('telefone', mascararTelefone)}
        />
      </fieldset>

      {valores.enderecos.map((end, i) => (
        <fieldset
          key={end.id ?? `novo-${i}`}
          className="grid gap-4 rounded-lg border border-slate-200 p-4 sm:grid-cols-6"
        >
          <legend className="px-1 font-semibold text-slate-900">Endereço {i + 1}</legend>
          <Input
            label="Identificação"
            placeholder="Casa, Trabalho..."
            value={end.apelido}
            onChange={alterarEndereco(i, 'apelido')}
            className="sm:col-span-2"
          />
          <Input
            label="CEP"
            obrigatorio
            inputMode="numeric"
            autoComplete="postal-code"
            placeholder="00000-000"
            value={end.cep}
            erro={erroEnd(i, 'cep')}
            onChange={alterarEndereco(i, 'cep', mascararCep)}
            className="sm:col-span-2"
          />
          <Select
            label="UF"
            obrigatorio
            placeholder="UF"
            opcoes={UFS}
            value={end.uf}
            erro={erroEnd(i, 'uf')}
            onChange={alterarEndereco(i, 'uf')}
            className="sm:col-span-2"
          />
          <Input
            label="Logradouro"
            obrigatorio
            autoComplete="address-line1"
            value={end.logradouro}
            erro={erroEnd(i, 'logradouro')}
            onChange={alterarEndereco(i, 'logradouro')}
            className="sm:col-span-4"
          />
          <Input
            label="Número"
            obrigatorio
            value={end.numero}
            erro={erroEnd(i, 'numero')}
            onChange={alterarEndereco(i, 'numero')}
            className="sm:col-span-2"
          />
          <Input
            label="Complemento"
            value={end.complemento}
            onChange={alterarEndereco(i, 'complemento')}
            className="sm:col-span-2"
          />
          <Input
            label="Bairro"
            obrigatorio
            value={end.bairro}
            erro={erroEnd(i, 'bairro')}
            onChange={alterarEndereco(i, 'bairro')}
            className="sm:col-span-2"
          />
          <Input
            label="Cidade"
            obrigatorio
            autoComplete="address-level2"
            value={end.cidade}
            erro={erroEnd(i, 'cidade')}
            onChange={alterarEndereco(i, 'cidade')}
            className="sm:col-span-2"
          />
          {valores.enderecos.length > 1 && (
            <div className="sm:col-span-6">
              <Button variante="fantasma" tamanho="sm" onClick={() => removerEndereco(i)}>
                Remover endereço {i + 1}
              </Button>
            </div>
          )}
        </fieldset>
      ))}

      <Button variante="secundario" onClick={adicionarEndereco}>
        + Adicionar outro endereço
      </Button>

      <div className="flex flex-col-reverse gap-2 border-t border-slate-200 pt-5 sm:flex-row sm:justify-end">
        <ButtonLink to="/clientes" variante="secundario">
          Cancelar
        </ButtonLink>
        <Button type="submit" carregando={salvando}>
          {inicial ? 'Salvar alterações' : 'Cadastrar cliente'}
        </Button>
      </div>
    </Card>
  );
}
