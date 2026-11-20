import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import {
  issueAdminRecoveryLink,
  saveSetupRecoveryKey,
  startAdminRecovery,
} from "../../helpers/admin-recovery";
import { csrfTokenOf } from "../../helpers/a11y";
import { login, logout } from "../../helpers/auth";
import { waitForMail, waitForMailLink } from "../../helpers/mail";
import { addPasskey } from "../../helpers/passkeys";
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

const userOf = (cid: string) =>
  `(SELECT user_id FROM g_user WHERE cid='${cid}')`;

const signInState = (env: GammaEnvironment, cid: string) =>
  sql(
    env,
    `SELECT coalesce(password, '-'),
       (SELECT COUNT(*) FROM user_credentials c JOIN user_entities e ON c.user_entity_user_id = e.id
          WHERE e.name = ${userOf(cid)}::text),
       coalesce((SELECT key_hash FROM g_user_recovery_key WHERE user_id = ${userOf(cid)}), '-')
     FROM g_user WHERE cid='${cid}'`,
  );

async function expectSetupRefusesChanges(page: Page, baseUrl: string) {
  const done = page.getByRole("button", { name: "Continue to signing in" });
  await expect(done).toBeVisible();
  const csrf = await csrfTokenOf(done);

  const password = await page.request.post(
    `${baseUrl}/sign-in-setup/password`,
    {
      form: {
        _csrf: csrf,
        newPassword: "Leftover-tab-1337",
        confirmPassword: "Leftover-tab-1337",
      },
      maxRedirects: 0,
    },
  );
  expect(password.status()).toBe(302);

  const recoveryKey = await page.request.post(
    `${baseUrl}/sign-in-setup/recovery-key`,
    {
      form: { _csrf: csrf },
      maxRedirects: 0,
    },
  );
  expect(recoveryKey.headers().location).not.toContain("/recovery-key");

  const options = await page.request.post(
    `${baseUrl}/webauthn/register/options`,
    {
      headers: { "X-CSRF-TOKEN": csrf },
      maxRedirects: 0,
    },
  );
  expect(options.status()).not.toBe(200);

  await page.reload();
  await expect(done).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Use a password" }),
  ).toHaveCount(0);
}

async function expectLeftoverPasswordFails(
  page: Page,
  baseUrl: string,
  cid: string,
) {
  await page.goto(`${baseUrl}/login`);
  await page.getByLabel("Cid or email", { exact: true }).fill(cid);
  await page.getByLabel("Password", { exact: true }).fill("Leftover-tab-1337");
  await page.getByRole("button", { name: "Login" }).click();
  await expect(page).toHaveURL(/\/login\?error/);
}

test("a finished email setup left open can't replace the password after the user signs in elsewhere", async ({
  page,
  browser,
  env,
  gamma,
}) => {
  await page.goto(`${gamma.url}/forgot-password`);
  await page.getByLabel("Cid or Email", { exact: true }).fill("kmalone");
  await page.getByRole("button", { name: /reset|send/i }).click();
  await page.goto(
    await waitForMailLink(
      env.gotify,
      gamma.url,
      "kmalone@example.org",
      "/forgot-password/finalize",
    ),
  );
  await page.getByRole("button", { name: "Set up a passkey" }).click();
  await expect(page).toHaveURL(/\/sign-in-setup$/);
  await page
    .getByLabel("Password", { exact: true })
    .fill("Users-password-1337");
  await page
    .getByLabel("Confirm password", { exact: true })
    .fill("Users-password-1337");
  await page.getByRole("button", { name: "Use a password" }).click();
  await expect(
    page.getByRole("heading", { name: "You're all set" }),
  ).toBeVisible();

  const ctx = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await ctx.newPage();
    await login(user, gamma.url, "kmalone", "Users-password-1337");
    const before = await signInState(env, "kmalone");

    await expectSetupRefusesChanges(page, gamma.url);

    expect(await signInState(env, "kmalone")).toBe(before);
    await user.goto(`${gamma.url}/me/security`);
    await expect(user).toHaveURL(/\/me\/security$/);
    const fresh = await browser.newContext({ ignoreHTTPSErrors: true });
    try {
      await expectLeftoverPasswordFails(
        await fresh.newPage(),
        gamma.url,
        "kmalone",
      );
    } finally {
      await fresh.close();
    }
  } finally {
    await ctx.close();
  }
  await expect
    .poll(() =>
      waitForMail(env.gotify, "kmalone@example.org", /sign-in security/),
    )
    .toContain("A password was set for your account.");
});

test("a recovery key setup done with a passkey takes no password or second passkey", async ({
  page,
  env,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(`${gamma.url}/me/security`);
  await page.getByRole("button", { name: "Turn off email recovery" }).click();
  const usedKey = await saveRecoveryKey(page);
  await logout(page);

  await page.goto(`${gamma.url}/login/recovery`);
  await page.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
  await page.getByLabel("Recovery key", { exact: true }).fill(usedKey);
  await page.getByRole("button", { name: "Continue", exact: true }).click();
  await expect(page).toHaveURL(/\/sign-in-setup$/);
  await saveSetupRecoveryKey(page);
  await addPasskey(page, "Recovered phone");
  await expect(
    page.getByRole("heading", { name: "You're all set" }),
  ).toBeVisible();
  const before = await signInState(env, "jhalpert");
  expect(before).toMatch(/^-\|1\|/);

  await expectSetupRefusesChanges(page, gamma.url);

  expect(await signInState(env, "jhalpert")).toBe(before);
  await page.getByRole("button", { name: "Continue to signing in" }).click();
  await expect(page).toHaveURL(/\/login\?sign-in-ready/);
});

test("a finished admin recovery setup takes no new password or recovery key", async ({
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
    await saveSetupRecoveryKey(user);
    await user
      .getByLabel("Password", { exact: true })
      .fill("Recovered-password-1337");
    await user
      .getByLabel("Confirm password", { exact: true })
      .fill("Recovered-password-1337");
    await user.getByRole("button", { name: "Use a password" }).click();
    await expect(
      user.getByRole("heading", { name: "Check your email address" }),
    ).toBeVisible();
    const before = await signInState(env, "jhalpert");

    await expectSetupRefusesChanges(user, gamma.url);

    expect(await signInState(env, "jhalpert")).toBe(before);
    await user.getByRole("button", { name: "Continue to signing in" }).click();
    await expect(user).toHaveURL(/\/login\?sign-in-ready/);
    await expectLeftoverPasswordFails(user, gamma.url, "jhalpert");
    await login(
      user,
      gamma.url,
      "jhalpert",
      "Recovered-password-1337",
      "Big Tuna",
    );
  } finally {
    await ctx.close();
  }
});
