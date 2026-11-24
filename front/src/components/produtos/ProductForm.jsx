import { useState } from 'react';
import { Button, ButtonLink, Card, Input, Select, Textarea } from '../ui';
import { CATEGORIAS } from '../../utils/constants';
import { temErros, validarProduto } from '../../utils/validators';
import { focarPrimeiroErro } from '../../utils/foco';

const VAZIO = { nome: '', descricao: '', categoria: '', preco: '', estoque: '', ativo: true };

/**
 * Formulário CONTROLADO de produto (cadastro e edição).
 * @param {object} [inicial]  produto a editar; vazio = cadastro
 * @param {(produto) => Promise} aoSalvar
 */
export function ProductForm({ inicial, aoSalvar, salvando = false }) {
  const [valores, setValores] = useState(() => (inicial ? { ...VAZIO, ...inicial } : VAZIO));
  const [erros, setErros] = useState({});
  const [tentouEnviar, setTentouEnviar] = useState(false);

  function alterar(campo, valor) {
    const novos = { ...valores, [campo]: valor };
    setValores(novos);
    if (tentouEnviar) setErros(validarProduto(novos)); // revalida ao corrigir
  }

  function enviar(e) {
    e.preventDefault();
    const form = e.currentTarget;
    setTentouEnviar(true);
    const encontrados = validarProduto(valores);
    setErros(encontrados);
    if (temErros(encontrados)) {
      focarPrimeiroErro(form);
      return;
    }
    aoSalvar(valores);
  }

  return (
    <Card
      as="form"
      noValidate
      onSubmit={enviar}
      className="space-y-5 p-6"
      aria-label="Dados do produto"
    >
      <p className="text-sm text-slate-600">
        Campos marcados com <span className="text-red-700">*</span> são obrigatórios.
      </p>
      <Input
        label="Nome"
        obrigatorio
        value={valores.nome}
        erro={erros.nome}
        onChange={(e) => alterar('nome', e.target.value)}
        maxLength={120}
      />
      <Textarea
        label="Descrição"
        value={valores.descricao}
        onChange={(e) => alterar('descricao', e.target.value)}
        maxLength={300}
      />
      <div className="grid grid-cols-1 gap-5 sm:grid-cols-3">
        <Select
          label="Categoria"
          obrigatorio
          placeholder="Selecione..."
          opcoes={CATEGORIAS}
          value={valores.categoria}
          erro={erros.categoria}
          onChange={(e) => alterar('categoria', e.target.value)}
        />
        <Input
          label="Preço (R$)"
          obrigatorio
          type="number"
          step="0.01"
          min="0"
          inputMode="decimal"
          value={valores.preco}
          erro={erros.preco}
          onChange={(e) => alterar('preco', e.target.value)}
        />
        <Input
          label="Estoque (unidades)"
          obrigatorio
          type="number"
          step="1"
          min="0"
          inputMode="numeric"
          value={valores.estoque}
          erro={erros.estoque}
          onChange={(e) => alterar('estoque', e.target.value)}
        />
      </div>
      <div className="flex items-center gap-3">
        <input
          id="produto-ativo"
          type="checkbox"
          className="h-5 w-5 rounded border-slate-400 accent-marca-700"
          checked={valores.ativo}
          onChange={(e) => alterar('ativo', e.target.checked)}
        />
        <label htmlFor="produto-ativo" className="text-sm font-medium text-slate-800">
          Produto ativo (aparece disponível no catálogo)
        </label>
      </div>
      <div className="flex flex-col-reverse gap-2 border-t border-slate-200 pt-5 sm:flex-row sm:justify-end">
        <ButtonLink to="/admin/produtos" variante="secundario">
          Cancelar
        </ButtonLink>
        <Button type="submit" carregando={salvando}>
          {inicial ? 'Salvar alterações' : 'Cadastrar produto'}
        </Button>
      </div>
    </Card>
  );
}
