import { expect, type Page } from "@playwright/test";
import { finishAdminSignIn } from "./passkeys";

export async function login(
  page: Page,
  baseUrl: string,
  username: string,
  password: string,
  expectedNick?: string,
): Promise<void> {
  await page.goto(baseUrl, { timeout: 60000 });
  await expect(page.getByRole("heading", { name: "Gamma" })).toBeVisible({
    timeout: 60000,
  });

  await page.getByLabel("Cid or email", { exact: true }).fill(username);
  await page.getByLabel("Password", { exact: true }).fill(password);

  await Promise.all([
    page.waitForURL((url) => url.pathname !== "/login", { timeout: 15000 }),
    page.getByRole("button", { name: "Login" }).click(),
  ]);

  // Admins sign in with a passkey after their password.
  if (new URL(page.url()).pathname === "/login/confirm") {
    await finishAdminSignIn(page);
  }

  if (expectedNick) {
    await expect(page.getByText(`Hey, ${expectedNick}!`)).toBeVisible({
      timeout: 10000,
    });
  }
}

export async function logout(page: Page): Promise<void> {
  await page.getByRole("button", { name: "Logout" }).click();
  await expect(page.getByLabel("Cid or email", { exact: true })).toBeVisible({
    timeout: 10000,
  });
}
