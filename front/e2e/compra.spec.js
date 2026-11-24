import { expect, test } from '@playwright/test';

test.beforeEach(async ({ page }) => {
  // começa sempre com o banco simulado e o carrinho limpos
  await page.goto('/');
  await page.evaluate(() => localStorage.clear());
  await page.reload();
});

test('fluxo completo: catálogo → carrinho → checkout com Pix → pedido pago', async ({ page }) => {
  await page.getByLabel('Buscar por nome').fill('teclado');
  await expect(page.getByText('1 produto encontrado')).toBeVisible();
  await page.getByRole('button', { name: /Adicionar ao carrinho.*Teclado Mecânico RGB/ }).click();
  await expect(page.getByRole('link', { name: 'Carrinho, 1 item' })).toBeVisible();

  await page.getByRole('link', { name: 'Carrinho, 1 item' }).click();
  await expect(page.getByRole('heading', { name: 'Carrinho', level: 1 })).toBeVisible();
  await page.getByRole('link', { name: 'Finalizar compra' }).click();

  await page.getByLabel('Cliente').selectOption({ label: 'Bruno Lima — bruno.lima@email.com' });
  await page.getByRole('button', { name: 'Continuar para pagamento' }).click();

  await page.getByRole('radio', { name: 'Pix' }).check();
  await page.getByRole('button', { name: /Confirmar e pagar/ }).click();
  await expect(page.getByText('Pagamento aprovado')).toBeVisible();

  await page.getByRole('link', { name: 'Acompanhar pedido' }).click();
  await expect(page).toHaveURL(/\/pedidos\/5$/);
  await expect(page.getByRole('listitem').filter({ hasText: 'Pago (etapa atual)' })).toBeVisible();
});

test('estoque baixa depois da compra e o carrinho sobrevive ao F5', async ({ page }) => {
  await page.goto('/produtos/9');
  await expect(page.getByText('Últimas 2 unidades')).toBeVisible();
  await page.getByLabel('Quantidade').fill('2');
  await page.getByRole('button', { name: 'Adicionar ao carrinho' }).click();
  await page.reload();
  await expect(page.getByRole('link', { name: 'Carrinho, 2 itens' })).toBeVisible();
  await expect(page.getByText('Sem estoque disponível no momento.')).toBeVisible();
});

test('navegação por teclado: pular para o conteúdo', async ({ page, isMobile }) => {
  test.skip(isMobile, 'teclado físico só faz sentido no desktop');
  await page.keyboard.press('Tab');
  const pular = page.getByRole('link', { name: 'Pular para o conteúdo' });
  await expect(pular).toBeFocused();
  await page.keyboard.press('Enter');
  await expect(page.locator('#conteudo')).toBeFocused();
});

test('rota inexistente mostra 404', async ({ page }) => {
  await page.goto('/pagina/que/nao/existe');
  await expect(page.getByRole('heading', { name: 'Página não encontrada' })).toBeVisible();
});
