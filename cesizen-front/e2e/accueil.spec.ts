import { expect, test } from '@playwright/test';

test.describe('Page d’accueil CESIZen', () => {
  test('affiche la page sans erreur serveur', async ({ page }) => {
    const response = await page.goto('/');

    expect(response).not.toBeNull();
    expect(response?.status()).toBeLessThan(500);

    await expect(page.locator('body')).toBeVisible();
    await expect(page).toHaveTitle(/CESIZen/i);
  });
});
