import { useState } from 'react';
import {
  ButtonLink,
  DataTable,
  EmptyState,
  ErrorState,
  Input,
  Loading,
  PageHeader,
} from '../../components/ui';
import { useAsync } from '../../hooks/useAsync';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { clienteService } from '../../services/clienteService';

export default function ClientesPage() {
  useDocumentTitle('Clientes');
  const { data: clientes, loading, error, reload } = useAsync(() => clienteService.listar(), []);
  const [busca, setBusca] = useState('');
  const termo = busca.trim().toLowerCase();
  const visiveis = (clientes ?? []).filter(
    (c) =>
      !termo ||
      c.nome.toLowerCase().includes(termo) ||
      c.email.includes(termo) ||
      c.cpf.includes(termo),
  );

  const colunas = [
    {
      chave: 'nome',
      titulo: 'Nome',
      render: (c) => <span className="font-semibold">{c.nome}</span>,
    },
    { chave: 'email', titulo: 'E-mail' },
    { chave: 'cpf', titulo: 'CPF', className: 'whitespace-nowrap' },
    { chave: 'telefone', titulo: 'Telefone', className: 'whitespace-nowrap' },
    {
      chave: 'cidade',
      titulo: 'Cidade',
      render: (c) => (c.enderecos[0] ? `${c.enderecos[0].cidade}/${c.enderecos[0].uf}` : '—'),
    },
    {
      chave: 'acoes',
      titulo: 'Ações',
      render: (c) => (
        <ButtonLink
          to={`/clientes/${c.id}/editar`}
          variante="secundario"
          tamanho="sm"
          aria-label={`Editar ${c.nome}`}
        >
          Editar
        </ButtonLink>
      ),
    },
  ];

  return (
    <>
      <PageHeader
        titulo="Clientes"
        subtitulo="Cadastro e gerenciamento de clientes."
        acoes={<ButtonLink to="/clientes/novo">+ Novo cliente</ButtonLink>}
      />
      <div className="mb-4 max-w-sm">
        <Input
          label="Buscar cliente"
          type="search"
          placeholder="Nome, e-mail ou CPF"
          value={busca}
          onChange={(e) => setBusca(e.target.value)}
        />
      </div>
      {loading && <Loading texto="Carregando clientes..." />}
      {error && <ErrorState erro={error} aoTentarNovamente={reload} />}
      {clientes &&
        (visiveis.length ? (
          <DataTable
            colunas={colunas}
            linhas={visiveis}
            legenda={`Clientes cadastrados (${visiveis.length})`}
          />
        ) : (
          <EmptyState titulo="Nenhum cliente encontrado" />
        ))}
    </>
  );
}
