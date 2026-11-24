import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ClienteForm } from '../../components/clientes/ClienteForm';
import { Alert, ErrorState, Loading, PageHeader } from '../../components/ui';
import { useToast } from '../../contexts/ToastContext';
import { useAsync } from '../../hooks/useAsync';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { clienteService } from '../../services/clienteService';
import NotFoundPage from '../NotFoundPage';

export default function ClienteFormPage() {
  const { id } = useParams();
  const edicao = Boolean(id);
  useDocumentTitle(edicao ? 'Editar cliente' : 'Novo cliente');
  const navigate = useNavigate();
  const toast = useToast();
  const [salvando, setSalvando] = useState(false);
  const [erroApi, setErroApi] = useState(null);
  const cliente = useAsync(
    () => (edicao ? clienteService.buscar(id) : Promise.resolve(null)),
    [id],
  );

  async function salvar(valores) {
    setSalvando(true);
    setErroApi(null);
    try {
      const salvo = edicao
        ? await clienteService.atualizar(id, valores)
        : await clienteService.criar(valores);
      toast.sucesso(`Cliente ${salvo.nome} ${edicao ? 'atualizado' : 'cadastrado'} com sucesso.`);
      navigate('/clientes');
    } catch (err) {
      setErroApi(err.message);
      window.scrollTo(0, 0);
    } finally {
      setSalvando(false);
    }
  }

  if (cliente.loading) return <Loading />;
  if (cliente.error?.status === 404)
    return <NotFoundPage mensagem={`O cliente ${id} não existe.`} />;
  if (cliente.error) return <ErrorState erro={cliente.error} aoTentarNovamente={cliente.reload} />;

  return (
    <div className="mx-auto max-w-3xl">
      <PageHeader titulo={edicao ? `Editar cliente: ${cliente.data.nome}` : 'Novo cliente'} />
      {erroApi && (
        <Alert tipo="erro" titulo="Não foi possível salvar" className="mb-4">
          {erroApi}
        </Alert>
      )}
      <ClienteForm
        key={id ?? 'novo'}
        inicial={cliente.data}
        aoSalvar={salvar}
        salvando={salvando}
      />
    </div>
  );
}
