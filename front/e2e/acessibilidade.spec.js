import AxeBuilder from '@axe-core/playwright';
import { expect, test } from '@playwright/test';

/**
 * Auditoria automatizada de acessibilidade (axe-core, regras WCAG 2.0/2.1 A e AA)
 * em todas as telas principais. O teste falha se houver qualquer violação.
 */
const telas = [
  ['Catálogo', '/'],
  ['Detalhe do produto', '/produtos/1'],
  ['Carrinho vazio', '/carrinho'],
  ['Pedidos', '/pedidos'],
  ['Detalhe do pedido', '/pedidos/1'],
  ['Clientes', '/clientes'],
  ['Cadastro de cliente', '/clientes/novo'],
  ['Gestão de produtos', '/admin/produtos'],
  ['Cadastro de produto', '/admin/produtos/novo'],
  ['Página 404', '/xyz'],
];

for (const [nome, url] of telas) {
  test(`sem violações WCAG A/AA: ${nome}`, async ({ page }) => {
    await page.goto(url);
    await expect(page.locator('h1')).toBeVisible();
    await page.waitForLoadState('networkidle');
    const { violations } = await new AxeBuilder({ page })
      .withTags(['wcag2a', 'wcag2aa', 'wcag21a', 'wcag21aa'])
      .analyze();
    expect(violations.map((v) => `${v.id}: ${v.help} (${v.nodes.length})`)).toEqual([]);
  });
}

test('sem violações no checkout com itens', async ({ page }) => {
  await page.goto('/produtos/4');
  await page.getByRole('button', { name: 'Adicionar ao carrinho' }).click();
  await page.goto('/checkout');
  await page.getByLabel('Cliente').selectOption('1');
  const { violations } = await new AxeBuilder({ page }).withTags(['wcag2a', 'wcag2aa']).analyze();
  expect(violations.map((v) => v.id)).toEqual([]);
});
