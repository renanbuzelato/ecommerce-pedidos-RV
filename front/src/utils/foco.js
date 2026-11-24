/**
 * Depois de uma validação com erro, leva o foco ao primeiro campo inválido
 * (o setTimeout espera o React pintar os aria-invalid na tela).
 */
export function focarPrimeiroErro(form) {
  setTimeout(() => form?.querySelector('[aria-invalid="true"]')?.focus(), 0);
}
