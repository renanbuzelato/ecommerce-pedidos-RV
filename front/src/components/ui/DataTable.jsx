/**
 * Tabela reutilizável.
 * @param {Array<{chave:string, titulo:string, render?:(linha)=>any, className?:string}>} colunas
 * @param {Array<object>} linhas  cada linha precisa de `id`
 * @param {string} legenda  <caption> lido pelo leitor de tela
 */
export function DataTable({ colunas, linhas, legenda, legendaVisivel = false }) {
  return (
    // tabIndex + role="region": no celular a tabela rola na horizontal e precisa
    // ser alcançável pelo teclado (regra axe "scrollable-region-focusable")
    <div
      role="region"
      aria-label={legenda}
      tabIndex={0}
      className="overflow-x-auto rounded-xl border border-slate-200 bg-white shadow-sm"
    >
      <table className="min-w-full divide-y divide-slate-200 text-left text-sm">
        <caption className={legendaVisivel ? 'p-4 text-left font-semibold' : 'sr-only'}>
          {legenda}
        </caption>
        <thead className="bg-slate-100 text-xs font-semibold uppercase tracking-wide text-slate-700">
          <tr>
            {colunas.map((c) => (
              <th key={c.chave} scope="col" className={`px-4 py-3 ${c.className ?? ''}`}>
                {c.titulo}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-100">
          {linhas.map((linha) => (
            <tr key={linha.id} className="hover:bg-slate-50">
              {colunas.map((c) => (
                <td key={c.chave} className={`px-4 py-3 align-middle ${c.className ?? ''}`}>
                  {c.render ? c.render(linha) : linha[c.chave]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
