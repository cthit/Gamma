import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import {
  issueAdminRecoveryLink,
  saveSetupRecoveryKey,
  startAdminRecovery,
} from "../../helpers/admin-recovery";
import { csrfTokenOf } from "../../helpers/a11y";
import { login, logout } from "../../helpers/auth";
import { saveRecoveryKey } from "../../helpers/recovery-key";
import type { GammaEnvironment } from "../../gamma-setup";
import type { Page } from "@playwright/test";

async function sql(env: GammaEnvironment, statement: string): Promise<string> {
  const result = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-v",
    "ON_ERROR_STOP=1",
    "-At",
    "-c",
    statement,
  ]);
  expect(result.exitCode, result.stderr).toBe(0);
  return result.stdout.trim();
}

const jim = "(SELECT user_id FROM g_user WHERE cid='jhalpert')";

async function postPassword(page: Page, baseUrl: string, password: string) {
  const csrf = await csrfTokenOf(
    page
      .getByRole("button", { name: "Create a recovery key" })
      .or(page.getByRole("button", { name: "Use a password" })),
  );
  return page.request.post(`${baseUrl}/sign-in-setup/password`, {
    form: { _csrf: csrf, newPassword: password, confirmPassword: password },
    maxRedirects: 0,
  });
}

async function useRecoveryKey(page: Page, baseUrl: string, key: string) {
  await page.goto(`${baseUrl}/login/recovery`);
  await page.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
  await page.getByLabel("Recovery key", { exact: true }).fill(key);
  await page.getByRole("button", { name: "Continue", exact: true }).click();
}

test("an admin recovery can't set up a password or passkey before a recovery key is saved", async ({
  page,
  browser,
  env,
  gamma,
}) => {
  const link = await issueAdminRecoveryLink(page, gamma, "jhalpert");
  const ctx = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await ctx.newPage();
    await startAdminRecovery(user, link);
    await expect(
      user.getByRole("button", { name: "Use a password" }),
    ).toHaveCount(0);
    await expect(
      user.getByRole("button", { name: "Add a passkey" }),
    ).toHaveCount(0);

    const early = await postPassword(user, gamma.url, "Early-password-1337");
    expect(early.status()).toBe(302);
    expect(
      await sql(
        env,
        `SELECT password IS NULL FROM g_user WHERE user_id=${jim}`,
      ),
    ).toBe("t");

    const csrf = await csrfTokenOf(
      user.getByRole("button", { name: "Create a recovery key" }),
    );
    const options = await user.request.post(
      `${gamma.url}/webauthn/register/options`,
      { headers: { "X-CSRF-TOKEN": csrf }, maxRedirects: 0 },
    );
    expect(options.status()).not.toBe(200);

    const fresh = await browser.newContext({ ignoreHTTPSErrors: true });
    try {
      const other = await fresh.newPage();
      await other.goto(`${gamma.url}/login`);
      await other.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
      await other
        .getByLabel("Password", { exact: true })
        .fill("Early-password-1337");
      await other.getByRole("button", { name: "Login" }).click();
      await expect(other).toHaveURL(/\/login\?error/);
    } finally {
      await fresh.close();
    }

    await saveSetupRecoveryKey(user);
    expect(
      await sql(
        env,
        `SELECT COUNT(*) FROM g_user_recovery_key WHERE user_id=${jim}`,
      ),
    ).toBe("1");
    await user
      .getByLabel("Password", { exact: true })
      .fill("Late-password-1337");
    await user
      .getByLabel("Confirm password", { exact: true })
      .fill("Late-password-1337");
    await user.getByRole("button", { name: "Use a password" }).click();
    await user.getByRole("button", { name: "Continue to signing in" }).click();
    await expect(user).toHaveURL(/\/login\?sign-in-ready/);
    await login(user, gamma.url, "jhalpert", "Late-password-1337", "Big Tuna");
  } finally {
    await ctx.close();
  }
});

test("a recovery key setup keeps the new recovery key however often the password is set", async ({
  page,
  env,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(`${gamma.url}/me/security`);
  await page.getByRole("button", { name: "Turn off email recovery" }).click();
  const usedKey = await saveRecoveryKey(page);
  await logout(page);

  await useRecoveryKey(page, gamma.url, usedKey);
  await expect(page).toHaveURL(/\/sign-in-setup$/);
  const early = await postPassword(page, gamma.url, "Early-password-1337");
  expect(early.status()).toBe(302);
  expect(
    await sql(env, `SELECT password IS NULL FROM g_user WHERE user_id=${jim}`),
  ).toBe("t");

  const newKey = await saveSetupRecoveryKey(page);
  for (const password of ["First-password-1337", "Second-password-1337"]) {
    const set = await postPassword(page, gamma.url, password);
    expect(set.status()).toBe(302);
    expect(
      await sql(
        env,
        `SELECT COUNT(*) FROM g_user_recovery_key WHERE user_id=${jim}`,
      ),
    ).toBe("1");
  }
  await page.reload();
  await page.getByRole("button", { name: "Continue to signing in" }).click();
  await expect(page).toHaveURL(/\/login\?sign-in-ready/);
  await login(page, gamma.url, "jhalpert", "Second-password-1337", "Big Tuna");
  await logout(page);

  await useRecoveryKey(page, gamma.url, usedKey);
  await expect(page).toHaveURL(/\/login\/recovery\?error/);
  await useRecoveryKey(page, gamma.url, newKey);
  await expect(page).toHaveURL(/\/sign-in-setup$/);
});
