import { Modal } from './Modal';
import { Button } from './Button';

/** Confirmação antes de ações destrutivas (ex.: excluir produto). */
export function ConfirmDialog({
  aberto,
  titulo = 'Confirmar ação',
  mensagem,
  textoConfirmar = 'Confirmar',
  variante = 'perigo',
  carregando = false,
  aoConfirmar,
  aoCancelar,
}) {
  return (
    <Modal
      aberto={aberto}
      aoFechar={aoCancelar}
      titulo={titulo}
      rodape={
        <>
          <Button variante="secundario" onClick={aoCancelar} disabled={carregando}>
            Cancelar
          </Button>
          <Button variante={variante} onClick={aoConfirmar} carregando={carregando}>
            {textoConfirmar}
          </Button>
        </>
      }
    >
      <p>{mensagem}</p>
    </Modal>
  );
}
