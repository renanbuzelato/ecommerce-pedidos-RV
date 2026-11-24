import { describe, expect, it } from 'vitest';
import { render, screen, within } from '@testing-library/react';
import { StatusTimeline } from './StatusTimeline';

describe('StatusTimeline', () => {
  it('marca a etapa atual com aria-current="step"', () => {
    render(
      <StatusTimeline
        situacao="PAGO"
        historico={[
          { situacao: 'ABERTO', data: '2026-11-01T10:00:00Z' },
          { situacao: 'PAGO', data: '2026-11-01T10:05:00Z' },
        ]}
      />,
    );
    const etapas = within(
      screen.getByRole('list', { name: 'Linha do tempo do pedido' }),
    ).getAllByRole('listitem');
    expect(etapas).toHaveLength(4);
    expect(etapas[1]).toHaveAttribute('aria-current', 'step');
    expect(etapas[1]).toHaveTextContent('Pago (etapa atual)');
    expect(etapas[3]).toHaveTextContent('Entregue (pendente)');
  });

  it('em pedido cancelado mostra só as etapas percorridas + Cancelado', () => {
    render(
      <StatusTimeline
        situacao="CANCELADO"
        historico={[
          { situacao: 'ABERTO', data: '2026-11-01T10:00:00Z' },
          { situacao: 'CANCELADO', data: '2026-11-02T10:00:00Z' },
        ]}
      />,
    );
    const etapas = screen.getAllByRole('listitem');
    expect(etapas.map((e) => e.textContent)).toEqual([
      expect.stringContaining('Pendente'),
      expect.stringContaining('Cancelado'),
    ]);
  });
});
