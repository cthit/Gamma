/// <reference lib="dom" />
import type { Browser, Page } from "@playwright/test";
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import type { GammaEnvironment } from "../../gamma-setup";
import { login } from "../../helpers/auth";
import { finishAdminSignIn } from "../../helpers/passkeys";
import { saveRecoveryKey } from "../../helpers/recovery-key";
import { currentTimeStep, totpCode } from "../../helpers/totp";

// An htmx request that is sent to a sign-in step saves the page to return to. After the step
// that page must be served as a full page, not as the htmx fragment it was requested as.

async function signInWithPassword(page: Page, baseUrl: string) {
  await page.goto(`${baseUrl}/login`);
  await page.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
  await page.getByLabel("Password", { exact: true }).fill("password1337");
  await page.getByRole("button", { name: "Login", exact: true }).click();
  await expect(page.getByText("Hey, Big Tuna!")).toBeVisible();
}

async function turnOnTotpInAnotherSession(browser: Browser, baseUrl: string) {
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const page = await context.newPage();
    await login(page, baseUrl, "jhalpert", "password1337", "Big Tuna");
    await page.goto(`${baseUrl}/me/security`);
    await page
      .getByRole("button", { name: "Set up an authenticator app" })
      .click();
    const secret = (
      await page
        .getByLabel("Key to enter in the app", { exact: true })
        .innerText()
    ).replace(/\s/g, "");
    await page
      .getByLabel("Code from the app", { exact: true })
      .fill(totpCode(secret, currentTimeStep()));
    await page.getByRole("button", { name: "Turn on", exact: true }).click();
    await saveRecoveryKey(page);
    await expect(
      page.getByText("Your authenticator app is set up."),
    ).toBeVisible();
    return secret;
  } finally {
    await context.close();
  }
}

async function enterTotp(page: Page, secret: string) {
  await expect(page).toHaveURL(/\/login\/totp$/);
  // The code used to turn the app on can't be used again.
  await page
    .getByLabel("Six digit code", { exact: true })
    .fill(totpCode(secret, currentTimeStep() + 1));
  await page.getByRole("button", { name: "Continue", exact: true }).click();
}

async function promoteToAdmin(env: GammaEnvironment) {
  const result = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-c",
    "INSERT INTO g_admin_user(user_id,created_at) SELECT user_id,NOW() FROM g_user WHERE cid='jhalpert'",
  ]);
  expect(result.exitCode).toBe(0);
}

async function expectFullPage(page: Page, path: string) {
  await expect(page).toHaveURL((url) => url.pathname === path);
  await page.waitForLoadState("load");
  const state = await page.evaluate(() => ({
    stylesheets: document.styleSheets.length > 0,
    htmx: typeof (window as unknown as { htmx?: unknown }).htmx,
  }));
  expect(state).toEqual({ stylesheets: true, htmx: "object" });
}

test("an htmx button that needs the authenticator code returns to the full page", async ({
  page,
  browser,
  gamma,
}) => {
  await signInWithPassword(page, gamma.url);
  const secret = await turnOnTotpInAnotherSession(browser, gamma.url);

  await page.getByRole("button", { name: "Change password" }).click();
  await enterTotp(page, secret);

  await expectFullPage(page, "/");
  await page.getByRole("button", { name: "Change password" }).click();
  await expect(
    page.getByLabel("Current password", { exact: true }),
  ).toBeVisible();
});

test("a boosted link that needs the authenticator code returns to the full page", async ({
  page,
  browser,
  gamma,
}) => {
  await signInWithPassword(page, gamma.url);
  const secret = await turnOnTotpInAnotherSession(browser, gamma.url);

  await page.getByRole("link", { name: "Your clients" }).click();
  await enterTotp(page, secret);

  await expectFullPage(page, "/my-clients");
});

test("an htmx button that needs a passkey returns to the full page", async ({
  page,
  gamma,
  env,
}) => {
  await signInWithPassword(page, gamma.url);
  await promoteToAdmin(env);

  await page.getByRole("button", { name: "Change password" }).click();
  await expect(
    page.getByRole("heading", { name: "Add a passkey to continue" }),
  ).toBeVisible();
  await finishAdminSignIn(page);

  await expectFullPage(page, "/");
  await page.getByRole("button", { name: "Edit", exact: true }).click();
  await expect(
    page.getByRole("heading", { name: "Editing your information" }),
  ).toBeVisible();
});
