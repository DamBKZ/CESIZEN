import { expect, test } from '@playwright/test';

test.describe('Authentification CESIZen', () => {
  test('affiche la page de connexion', async ({ page }) => {
    await page.goto('/login');

    await expect(
      page.getByRole('heading', { name: 'Connexion' }),
    ).toBeVisible();

    await expect(page.getByLabel('Email')).toBeVisible();
    await expect(page.getByLabel('Mot de passe')).toBeVisible();

    await expect(
      page.getByRole('button', { name: 'Se connecter' }),
    ).toBeVisible();
  });

  test('refuse des identifiants incorrects', async ({ page }) => {
    await page.goto('/login');

    await page.getByLabel('Email').fill('utilisateur-inexistant@example.test');
    await page.getByLabel('Mot de passe').fill('MotDePasseInvalide123!');

    const loginResponsePromise = page.waitForResponse(
      (response) =>
        response.request().method() === 'POST' &&
        response.url().includes('/auth/login'),
    );

    await page.getByRole('button', { name: 'Se connecter' }).click();

    const loginResponse = await loginResponsePromise;

    expect([400, 401, 403]).toContain(loginResponse.status());
    await expect(page.getByText('Identifiants invalides')).toBeVisible();
    await expect(page).toHaveURL(/\/login/);
  });
});
