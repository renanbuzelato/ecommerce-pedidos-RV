import { useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ProductForm } from '../../components/produtos/ProductForm';
import { Alert, ErrorState, Loading, PageHeader } from '../../components/ui';
import { useToast } from '../../contexts/ToastContext';
import { useAsync } from '../../hooks/useAsync';
import { useDocumentTitle } from '../../hooks/useDocumentTitle';
import { produtoService } from '../../services/produtoService';
import NotFoundPage from '../NotFoundPage';

/** Mesma página para /admin/produtos/novo e /admin/produtos/:id/editar. */
export default function ProdutoFormPage() {
  const { id } = useParams();
  const edicao = Boolean(id);
  useDocumentTitle(edicao ? 'Editar produto' : 'Novo produto');
  const navigate = useNavigate();
  const toast = useToast();
  const [salvando, setSalvando] = useState(false);
  const [erroApi, setErroApi] = useState(null);
  const produto = useAsync(
    () => (edicao ? produtoService.buscar(id) : Promise.resolve(null)),
    [id],
  );

  async function salvar(valores) {
    setSalvando(true);
    setErroApi(null);
    try {
      const salvo = edicao
        ? await produtoService.atualizar(id, valores)
        : await produtoService.criar(valores);
      toast.sucesso(`Produto "${salvo.nome}" ${edicao ? 'atualizado' : 'cadastrado'} com sucesso.`);
      navigate('/admin/produtos');
    } catch (err) {
      setErroApi(err.message);
    } finally {
      setSalvando(false);
    }
  }

  if (produto.loading) return <Loading />;
  if (produto.error?.status === 404)
    return <NotFoundPage mensagem={`O produto ${id} não existe.`} />;
  if (produto.error) return <ErrorState erro={produto.error} aoTentarNovamente={produto.reload} />;

  return (
    <div className="mx-auto max-w-3xl">
      <PageHeader titulo={edicao ? `Editar produto #${id}` : 'Novo produto'} />
      {erroApi && (
        <Alert tipo="erro" titulo="Não foi possível salvar" className="mb-4">
          {erroApi}
        </Alert>
      )}
      <ProductForm
        key={id ?? 'novo'}
        inicial={produto.data}
        aoSalvar={salvar}
        salvando={salvando}
      />
    </div>
  );
}
