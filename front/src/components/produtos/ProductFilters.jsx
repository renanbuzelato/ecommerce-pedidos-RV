import { Button, Input, Select } from '../ui';
import { CATEGORIAS } from '../../utils/constants';

/**
 * Filtros controlados: o estado mora na página (pai) e chega por props.
 * @param {{busca, categoria, precoMin, precoMax}} filtros
 * @param {(filtros) => void} aoMudar
 */
export function ProductFilters({ filtros, aoMudar }) {
  const alterar = (campo) => (e) => aoMudar({ ...filtros, [campo]: e.target.value });
  const limpar = () => aoMudar({ busca: '', categoria: '', precoMin: '', precoMax: '' });

  return (
    <form
      role="search"
      className="grid grid-cols-1 gap-4 rounded-xl border border-slate-200 bg-white p-4 shadow-sm sm:grid-cols-2 lg:grid-cols-5 lg:items-end"
      onSubmit={(e) => e.preventDefault()}
      aria-label="Filtrar produtos"
    >
      <Input
        label="Buscar por nome"
        type="search"
        placeholder="Ex.: teclado"
        value={filtros.busca}
        onChange={alterar('busca')}
        className="sm:col-span-2 lg:col-span-2"
      />
      <Select
        label="Categoria"
        value={filtros.categoria}
        onChange={alterar('categoria')}
        opcoes={CATEGORIAS}
        placeholder="Todas"
      />
      <div className="grid grid-cols-2 gap-2">
        <Input
          label="Preço mín. (R$)"
          type="number"
          min="0"
          inputMode="decimal"
          value={filtros.precoMin}
          onChange={alterar('precoMin')}
        />
        <Input
          label="Preço máx. (R$)"
          type="number"
          min="0"
          inputMode="decimal"
          value={filtros.precoMax}
          onChange={alterar('precoMax')}
        />
      </div>
      <Button variante="secundario" onClick={limpar}>
        Limpar filtros
      </Button>
    </form>
  );
}
