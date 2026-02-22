import { expect, type Page } from "@playwright/test";

export async function login(
  page: Page,
  baseUrl: string,
  username: string,
  password: string,
  expectedNick?: string,
): Promise<void> {
  await page.goto(baseUrl, { timeout: 60000 });
  await expect(page.getByText("Gamma")).toBeVisible({ timeout: 60000 });

  await page.fill('input[name="username"]', username);
  await page.fill('input[name="password"]', password);

  await Promise.all([
    page.waitForURL((url) => url.pathname !== "/login", { timeout: 15000 }),
    page.click('button:has-text("Login")'),
  ]);

  if (expectedNick) {
    await expect(page.getByText(`Hey, ${expectedNick}!`)).toBeVisible({
      timeout: 10000,
    });
  }
}

export async function logout(page: Page): Promise<void> {
  await page.getByRole("button", { name: "Logout" }).click();
  await expect(page.locator('input[name="username"]')).toBeVisible({
    timeout: 10000,
  });
}
