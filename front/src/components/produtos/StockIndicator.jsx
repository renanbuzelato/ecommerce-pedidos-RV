import { Badge } from '../ui';
import { ESTOQUE_BAIXO } from '../../utils/constants';

/** Indicação de estoque com texto explícito (não depende só de cor). */
export function StockIndicator({ estoque, ativo = true }) {
  if (!ativo) return <Badge cor="slate">Indisponível</Badge>;
  if (estoque <= 0) return <Badge cor="red">Esgotado</Badge>;
  if (estoque <= ESTOQUE_BAIXO) return <Badge cor="amber">Últimas {estoque} unidades</Badge>;
  return <Badge cor="green">Em estoque ({estoque})</Badge>;
}
