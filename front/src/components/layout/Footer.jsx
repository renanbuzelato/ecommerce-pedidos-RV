import { usandoApiSimulada } from '../../services/api/client';

export function Footer() {
  return (
    <footer className="mt-16 border-t border-slate-200 bg-white">
      <div className="mx-auto flex max-w-7xl flex-col gap-2 px-4 py-6 text-sm text-slate-600 sm:flex-row sm:items-center sm:justify-between sm:px-6">
        <p>Sistema de Gestão de Pedidos — Squad RV · ADS SENAI São Carlos</p>
        <p>
          Fonte dos dados:{' '}
          <strong className="text-slate-800">
            {usandoApiSimulada ? 'API simulada' : 'API REST do back-end'}
          </strong>
        </p>
      </div>
    </footer>
  );
}
