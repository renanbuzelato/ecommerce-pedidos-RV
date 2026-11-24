import { ButtonLink } from '../components/ui';
import { useDocumentTitle } from '../hooks/useDocumentTitle';

export default function NotFoundPage({ mensagem }) {
  useDocumentTitle('Página não encontrada');
  return (
    <section className="mx-auto max-w-lg py-16 text-center">
      <p className="text-7xl font-extrabold text-marca-700" aria-hidden="true">
        404
      </p>
      <h1 className="mt-4 text-2xl font-bold text-slate-900">Página não encontrada</h1>
      <p className="mt-2 text-slate-600">
        {mensagem ?? 'O endereço que você tentou acessar não existe ou foi removido.'}
      </p>
      <ButtonLink to="/" className="mt-8">
        Ir para o catálogo
      </ButtonLink>
    </section>
  );
}
