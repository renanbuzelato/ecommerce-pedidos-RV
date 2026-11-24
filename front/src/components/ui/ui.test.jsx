import { describe, expect, it, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { Button, ConfirmDialog, Input, StatusBadge } from '.';

describe('Button', () => {
  it('dispara onClick e fica bloqueado enquanto carrega', async () => {
    const aoClicar = vi.fn();
    const { rerender } = render(<Button onClick={aoClicar}>Salvar</Button>);
    await userEvent.click(screen.getByRole('button', { name: 'Salvar' }));
    expect(aoClicar).toHaveBeenCalledTimes(1);

    rerender(
      <Button onClick={aoClicar} carregando>
        Salvar
      </Button>,
    );
    expect(screen.getByRole('button', { name: 'Salvar' })).toBeDisabled();
    expect(screen.getByRole('button', { name: 'Salvar' })).toHaveAttribute('aria-busy', 'true');
  });
});

describe('Input', () => {
  it('liga rótulo, dica e erro ao campo (acessibilidade)', () => {
    render(<Input label="E-mail" dica="Use seu e-mail pessoal" erro="E-mail inválido" />);
    const campo = screen.getByLabelText('E-mail');
    expect(campo).toHaveAttribute('aria-invalid', 'true');
    expect(campo).toHaveAccessibleDescription('Use seu e-mail pessoal E-mail inválido');
  });
});

describe('StatusBadge', () => {
  it('mostra "Pendente" para a situação ABERTO do back-end', () => {
    render(<StatusBadge situacao="ABERTO" />);
    expect(screen.getByText('Pendente')).toBeInTheDocument();
  });
});

describe('ConfirmDialog', () => {
  it('abre como diálogo modal, foca dentro e fecha com Esc', async () => {
    const aoCancelar = vi.fn();
    const aoConfirmar = vi.fn();
    render(
      <ConfirmDialog
        aberto
        titulo="Excluir produto?"
        mensagem="Tem certeza?"
        aoCancelar={aoCancelar}
        aoConfirmar={aoConfirmar}
      />,
    );
    const dialogo = screen.getByRole('dialog', { name: 'Excluir produto?' });
    expect(dialogo).toHaveAttribute('aria-modal', 'true');
    expect(dialogo).toContainElement(document.activeElement);

    await userEvent.keyboard('{Escape}');
    expect(aoCancelar).toHaveBeenCalled();

    await userEvent.click(screen.getByRole('button', { name: 'Confirmar' }));
    expect(aoConfirmar).toHaveBeenCalled();
  });
});
