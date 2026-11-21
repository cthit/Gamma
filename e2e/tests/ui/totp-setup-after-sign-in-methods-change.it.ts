import type { Page } from "@playwright/test";
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { addPasskey, finishAdminSignIn } from "../../helpers/passkeys";
import { currentTimeStep, totpCode } from "../../helpers/totp";

const totpUnavailable =
  "An authenticator app is only used together with a password and not by admins, so it wasn't set up.";
const recoveryKeyUnavailable =
  "Admins sign in with passkeys only and don't use a recovery key.";

async function startTotpSetup(page: Page, url: string): Promise<string> {
  await page.goto(`${url}/me/security`);
  await page
    .getByRole("button", { name: "Set up an authenticator app" })
    .click();
  return (
    await page
      .getByLabel("Key to enter in the app", { exact: true })
      .innerText()
  ).replace(/\s/g, "");
}

async function replacePasswordWithPasskey(page: Page, url: string) {
  await page.goto(`${url}/me/security`);
  await addPasskey(page, "Laptop");
  await expect(
    page.getByText("The passkey was added. You sign in with passkeys only."),
  ).toBeVisible();
}

async function csrfOf(page: Page, url: string): Promise<string> {
  await page.goto(`${url}/me/security`);
  const token = await page
    .getByRole("button", { name: "Add a passkey" })
    .getAttribute("data-csrf-token");
  if (!token) {
    throw new Error("The security page has no CSRF token");
  }
  return token;
}

async function post(
  page: Page,
  url: string,
  path: string,
  form: Record<string, string>,
) {
  const response = await page.request.post(`${url}${path}`, {
    form,
    maxRedirects: 0,
  });
  expect(response.status(), path).toBe(302);
  return response.headers().location;
}

async function expectNoAuthenticatorApp(page: Page, url: string) {
  await page.goto(`${url}/me/security`);
  await expect(
    page.getByText("You sign in with passkeys only.", { exact: true }),
  ).toBeVisible();
  await expect(
    page.getByRole("heading", { name: "Authenticator app" }),
  ).toHaveCount(0);
  await page.goto(`${url}/me/security/totp`);
  await expect(page).toHaveURL(/\/me\/security$/);
}

test("an authenticator app setup started before a passkey replaced the password is dropped instead of failing", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "pbeesly", "password1337", "Pam-Pam");
  await startTotpSetup(page, gamma.url);

  const other = await page.context().newPage();
  await replacePasswordWithPasskey(other, gamma.url);

  await page.reload();
  await expect(page).toHaveURL(/\/me\/security\?error=totp-unavailable$/);
  await expect(page.getByText(totpUnavailable)).toBeVisible();

  const csrf = await csrfOf(other, gamma.url);
  expect(
    await post(other, gamma.url, "/me/security/totp/start", { _csrf: csrf }),
  ).toMatch(/\/me\/security\?error=totp-unavailable$/);
  await expectNoAuthenticatorApp(other, gamma.url);
});

test("a stale recovery key step of an authenticator app setup is dropped once the password is gone", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "pbeesly", "password1337", "Pam-Pam");
  const secret = await startTotpSetup(page, gamma.url);
  await page
    .getByLabel("Code from the app", { exact: true })
    .fill(totpCode(secret, currentTimeStep()));
  await page.getByRole("button", { name: "Turn on" }).click();
  await expect(
    page.getByRole("heading", { name: "Save your recovery key" }),
  ).toBeVisible();
  const key = (
    await page.getByLabel("Your recovery key", { exact: true }).innerText()
  ).trim();
  await page.getByRole("link", { name: "I have saved it" }).click();
  await expect(
    page.getByRole("heading", { name: "Type your recovery key" }),
  ).toBeVisible();

  const other = await page.context().newPage();
  await replacePasswordWithPasskey(other, gamma.url);

  const csrf = await csrfOf(other, gamma.url);
  expect(
    await post(other, gamma.url, "/me/security/recovery-key/confirm", {
      _csrf: csrf,
      recoveryKey: key,
    }),
  ).toMatch(/\/me\/security\?error=totp-unavailable$/);

  await page.goto(`${gamma.url}/me/security/recovery-key`);
  await expect(page).toHaveURL(/\/me\/security$/);
  await expectNoAuthenticatorApp(page, gamma.url);
});

test("an admin is told they can't set up an authenticator app or recovery key", async ({
  page,
  gamma,
}) => {
  await page.goto(`${gamma.url}/login`);
  await page.getByLabel("Cid or email", { exact: true }).fill(gamma.adminCid!);
  await page.getByLabel("Password", { exact: true }).fill(gamma.adminPassword!);
  await Promise.all([
    page.waitForURL((u) => u.pathname !== "/login", { timeout: 15000 }),
    page.getByRole("button", { name: "Login" }).click(),
  ]);
  await finishAdminSignIn(page);

  const csrf = await csrfOf(page, gamma.url);
  expect(
    await post(page, gamma.url, "/me/security/totp/start", { _csrf: csrf }),
  ).toMatch(/\/me\/security\?error=totp-unavailable$/);
  for (const path of [
    "/me/security/recovery-key",
    "/me/security/email-recovery/disable",
  ]) {
    expect(await post(page, gamma.url, path, { _csrf: csrf })).toMatch(
      /\/me\/security\?error=recovery-key-unavailable$/,
    );
  }
  await page.goto(`${gamma.url}/me/security?error=recovery-key-unavailable`);
  await expect(page.getByText(recoveryKeyUnavailable)).toBeVisible();
  await page.goto(`${gamma.url}/me/security/recovery-key`);
  await expect(page).toHaveURL(/\/me\/security$/);
  await page.goto(`${gamma.url}/me/security/totp`);
  await expect(page).toHaveURL(/\/me\/security$/);
});
