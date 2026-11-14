import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { currentTimeStep, totpCode } from "../../helpers/totp";
import { saveRecoveryKey } from "../../helpers/recovery-key";

async function signInWithPassword(
  page: import("@playwright/test").Page,
  url: string,
  password: string,
) {
  await page.goto(`${url}/login`);
  await page.getByLabel("Cid or email", { exact: true }).fill("pbeesly");
  await page.getByLabel("Password", { exact: true }).fill(password);
  await Promise.all([
    page.waitForURL((u) => u.pathname !== "/login", { timeout: 15000 }),
    page.getByRole("button", { name: "Login" }).click(),
  ]);
}

async function useRecoveryKey(
  page: import("@playwright/test").Page,
  url: string,
  key: string,
) {
  await page.goto(`${url}/login/recovery`);
  await page.getByLabel("Cid or email", { exact: true }).fill("pbeesly");
  await page.getByLabel("Recovery key", { exact: true }).fill(key);
  await page.getByRole("button", { name: "Continue" }).click();
}

test("a user signs in with their password and authenticator app, and gets back in with their recovery key", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "pbeesly", "password1337", "Pam-Pam");

  await page.goto(`${gamma.url}/me/security`);
  await page
    .getByRole("button", { name: "Set up an authenticator app" })
    .click();
  await expect(
    page.getByRole("img", {
      name: "QR code for setting up your authenticator app",
    }),
  ).toBeVisible();
  const secret = (
    await page
      .getByLabel("Key to enter in the app", { exact: true })
      .innerText()
  ).replace(/\s/g, "");

  await page.getByLabel("Code from the app", { exact: true }).fill("000000");
  await page.getByRole("button", { name: "Turn on" }).click();
  await expect(page.getByText("That code wasn't right")).toBeVisible();

  const setupStep = currentTimeStep();
  await page
    .getByLabel("Code from the app", { exact: true })
    .fill(totpCode(secret, setupStep));
  await page.getByRole("button", { name: "Turn on" }).click();

  const key = (
    await page.getByLabel("Your recovery key", { exact: true }).innerText()
  ).trim();
  await page.getByRole("link", { name: "I have saved it" }).click();
  await expect(
    page.getByLabel("Your recovery key", { exact: true }),
  ).toHaveCount(0);
  await page
    .getByLabel("Recovery key", { exact: true })
    .fill("0000 0000 0000 0000");
  await page.getByRole("button", { name: "Confirm" }).click();
  await expect(page.getByText("That isn't the recovery key.")).toBeVisible();
  await page.getByRole("link", { name: "Show the key again" }).click();
  expect(await saveRecoveryKey(page)).toBe(key);
  await expect(
    page.getByText("Your authenticator app is set up."),
  ).toBeVisible();

  await logout(page);
  await signInWithPassword(page, gamma.url, "password1337");
  await expect(page).toHaveURL(/\/login\/totp/);
  await page
    .getByLabel("Six digit code", { exact: true })
    .fill(totpCode(secret, setupStep));
  await page.getByRole("button", { name: "Continue" }).click();
  await expect(page.getByText("That code wasn't right")).toBeVisible();
  await page
    .getByLabel("Six digit code", { exact: true })
    .fill(totpCode(secret, Math.max(setupStep, currentTimeStep()) + 1));
  await Promise.all([
    page.waitForURL((u) => u.pathname === "/", { timeout: 15000 }),
    page.getByRole("button", { name: "Continue" }).click(),
  ]);
  await expect(page.getByText("Hey, Pam-Pam!")).toBeVisible();
  await logout(page);

  await page.goto(`${gamma.url}/login`);
  await page.getByRole("link", { name: "Use a recovery key" }).click();
  await page.getByLabel("Cid or email", { exact: true }).fill("pbeesly");
  await page.getByLabel("Recovery key", { exact: true }).fill(key);
  await page.getByRole("button", { name: "Continue" }).click();
  await expect(page).toHaveURL(/\/sign-in-setup/);
  await expect(
    page.getByText("Your recovery key keeps working until you have."),
  ).toBeVisible();

  await page.context().clearCookies();
  await useRecoveryKey(page, gamma.url, key);
  await expect(page).toHaveURL(/\/sign-in-setup/);

  await page.goto(`${gamma.url}/users`);
  await expect(page).toHaveURL(/\/sign-in-setup/);

  await page.getByLabel("Password", { exact: true }).fill("New-password-1337");
  await page
    .getByLabel("Confirm password", { exact: true })
    .fill("New-password-1337");
  await page.getByRole("button", { name: "Use a password" }).click();
  await page.getByRole("button", { name: "Continue to signing in" }).click();
  await expect(page).toHaveURL(/\/login\?sign-in-ready/);

  await login(page, gamma.url, "pbeesly", "New-password-1337", "Pam-Pam");
  await logout(page);

  await useRecoveryKey(page, gamma.url, key);
  await expect(page).toHaveURL(/\/login\/recovery\?error/);
});
