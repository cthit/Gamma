import { setTimeout as delay } from "node:timers/promises";
import type { Page } from "@playwright/test";
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { currentTimeStep, totpCode } from "../../helpers/totp";
import { saveRecoveryKey } from "../../helpers/recovery-key";
import { csrfTokenOf } from "../../helpers/a11y";
import { waitForMail, waitForMailLink } from "../../helpers/mail";
import type { GammaEnvironment } from "../../gamma-setup";

const MAX_WRONG_CODES = 10;
const MAX_SIGN_IN_ATTEMPTS = 50;

async function enableTotp(page: Page, baseUrl: string) {
  await page.goto(`${baseUrl}/me/security`);
  await page
    .getByRole("button", { name: "Set up an authenticator app" })
    .click();
  const secret = (
    await page
      .getByLabel("Key to enter in the app", { exact: true })
      .innerText()
  ).replace(/\s/g, "");
  const step = currentTimeStep();
  await page
    .getByLabel("Code from the app", { exact: true })
    .fill(totpCode(secret, step));
  await page.getByRole("button", { name: "Turn on", exact: true }).click();
  await saveRecoveryKey(page);
  await expect(
    page.getByText("Your authenticator app is set up."),
  ).toBeVisible();
  return { secret, step };
}

async function signInWithPassword(
  page: Page,
  baseUrl: string,
  password = "password1337",
) {
  await page.goto(`${baseUrl}/login`);
  await page.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
  await page.getByLabel("Password", { exact: true }).fill(password);
  await page.getByRole("button", { name: "Login", exact: true }).click();
}

// A code for a time step after the last used one, waiting until the server accepts it.
async function unusedCode(secret: string, lastUsed: number) {
  while (currentTimeStep() + 1 <= lastUsed) await delay(500);
  const step = Math.max(lastUsed + 1, currentTimeStep());
  return { code: totpCode(secret, step), step };
}

async function enterCode(page: Page, code: string) {
  await page.getByLabel("Six digit code", { exact: true }).fill(code);
  await page.getByRole("button", { name: "Continue", exact: true }).click();
}

async function redisCount(env: GammaEnvironment, pattern: string) {
  const keys = await env.redis.exec(["redis-cli", "--raw", "KEYS", pattern]);
  const key = keys.output.trim();
  expect(key.split("\n")).toHaveLength(1);
  const value = await env.redis.exec(["redis-cli", "--raw", "GET", key]);
  return Number(value.output.trim());
}

test("wrong authenticator codes are limited per user, correct ones don't count, and a password reset lifts the limit", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const enabled = await enableTotp(page, gamma.url);
  await logout(page);

  await signInWithPassword(page, gamma.url);
  await expect(page).toHaveURL(/\/login\/totp/);
  const correct = await unusedCode(enabled.secret, enabled.step);
  await enterCode(page, correct.code);
  await expect(page.getByText("Hey, Big Tuna!")).toBeVisible();
  await logout(page);

  await signInWithPassword(page, gamma.url);
  await expect(page).toHaveURL(/\/login\/totp/);
  const csrf = await csrfTokenOf(
    page.getByLabel("Six digit code", { exact: true }),
  );
  const wrong = totpCode(enabled.secret, correct.step - 1000);
  for (let i = 0; i < MAX_WRONG_CODES; i++) {
    const response = await page.request.post(`${gamma.url}/login/totp`, {
      form: { _csrf: csrf, code: wrong },
      maxRedirects: 0,
    });
    expect(response.status()).toBe(302);
    // Had the correct code counted, the last wrong one would be refused unchecked.
    expect(response.headers()["location"]).toMatch(/\/login\/totp\?error$/);
  }

  const mail = await waitForMail(
    env.gotify,
    "jhalpert@example.org",
    /Someone may know your password/,
  );
  expect(mail).toContain("jhalpert");
  expect(mail).toContain("Change it, or reset it");

  // The correct code is refused without being checked, so it stays usable.
  const blocked = await unusedCode(enabled.secret, correct.step);
  await page.reload();
  await enterCode(page, blocked.code);
  await expect(page).toHaveURL(/\/login\/totp\?too-many-codes/);
  await expect(
    page.getByText(
      "Too many wrong codes. Try again later or use your recovery key. Resetting your password also lifts this limit.",
    ),
  ).toBeVisible();
  expect(await redisCount(env, "throttle:totp:*")).toBe(MAX_WRONG_CODES);

  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const owner = await context.newPage();
    await owner.goto(`${gamma.url}/forgot-password`);
    await owner.getByLabel("Cid or Email", { exact: true }).fill("jhalpert");
    await owner.getByRole("button", { name: "Reset password" }).click();
    await owner.goto(
      await waitForMailLink(
        env.gotify,
        gamma.url,
        "jhalpert@example.org",
        "/forgot-password/finalize",
      ),
    );
    await owner
      .getByLabel("Password", { exact: true })
      .fill("New-password-1337");
    await owner
      .getByLabel("Confirm password", { exact: true })
      .fill("New-password-1337");
    await owner.getByRole("button", { name: "Reset password" }).click();
    await expect(owner).toHaveURL(/\/login\?password-reset/);

    // The guesser's half signed in session was revoked and gets no new guesses.
    const stale = await page.request.post(`${gamma.url}/login/totp`, {
      form: { _csrf: csrf, code: wrong },
      maxRedirects: 0,
    });
    expect(stale.headers()["location"]).toMatch(/\/login\/totp\?error$/);
    const keys = await env.redis.exec([
      "redis-cli",
      "--raw",
      "KEYS",
      "throttle:totp:*",
    ]);
    expect(keys.output.trim()).toBe("");

    await signInWithPassword(owner, gamma.url, "New-password-1337");
    await expect(owner).toHaveURL(/\/login\/totp/);
    await enterCode(owner, blocked.code);
    await expect(owner.getByText("Hey, Big Tuna!")).toBeVisible();
  } finally {
    await context.close();
  }
});

test("the sign-in steps after the password show their throttling, and a correct code doesn't use up an attempt", async ({
  page,
  gamma,
  env,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const enabled = await enableTotp(page, gamma.url);
  await logout(page);

  const ipKey = "throttle:login:*";
  const csrf = await csrfTokenOf(
    page.getByLabel("Cid or email", { exact: true }),
  );
  const used = await redisCount(env, ipKey);
  for (let i = used; i < MAX_SIGN_IN_ATTEMPTS - 2; i++) {
    const response = await page.request.post(`${gamma.url}/login`, {
      form: { _csrf: csrf, username: "jhalpert", password: "wrong" },
      maxRedirects: 0,
    });
    expect(response.headers()["location"]).toMatch(/\/login\?error/);
  }

  await signInWithPassword(page, gamma.url);
  await expect(page).toHaveURL(/\/login\/totp/);
  const correct = await unusedCode(enabled.secret, enabled.step);
  await enterCode(page, correct.code);
  await expect(page.getByText("Hey, Big Tuna!")).toBeVisible();
  expect(await redisCount(env, ipKey)).toBe(MAX_SIGN_IN_ATTEMPTS - 1);
  await logout(page);

  await signInWithPassword(page, gamma.url);
  await expect(page).toHaveURL(/\/login\/totp/);
  await enterCode(page, totpCode(enabled.secret, currentTimeStep()));
  await expect(page).toHaveURL(/\/login\/totp\?throttle/);
  await expect(
    page.getByText(
      "Too many sign-in attempts have been made from your network. Try again later.",
    ),
  ).toBeVisible();

  await page.getByRole("link", { name: /Use your recovery key/ }).click();
  await page.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
  await page
    .getByLabel("Recovery key", { exact: true })
    .fill("0000 0000 0000 0000");
  await page.getByRole("button", { name: "Continue" }).click();
  await expect(page).toHaveURL(/\/login\/recovery\?throttle/);
  await expect(
    page.getByText(
      "Too many sign-in attempts have been made from your network. Try again later.",
    ),
  ).toBeVisible();

  await page.context().clearCookies();
  await signInWithPassword(page, gamma.url);
  await expect(page).toHaveURL(/\/login\?throttle=true/);
  await expect(
    page.getByText("You have been throttled for attempting to sign in"),
  ).toBeVisible();
});
