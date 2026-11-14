import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import {
  issueAdminRecoveryLink,
  startAdminRecovery,
} from "../../helpers/admin-recovery";
import { csrfTokenOf } from "../../helpers/a11y";
import { saveRecoveryKey } from "../../helpers/recovery-key";
import { waitForMailLink } from "../../helpers/mail";
import type { GammaEnvironment } from "../../gamma-setup";
import type { Page } from "@playwright/test";

async function sql(env: GammaEnvironment, statement: string) {
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

async function setPassword(page: Page, password = "Recovered-password-1337") {
  await page.getByLabel("Password", { exact: true }).fill(password);
  await page.getByLabel("Confirm password", { exact: true }).fill(password);
  await page.getByRole("button", { name: "Use a password" }).click();
  await expect(
    page.getByRole("heading", { name: "Save a new recovery key" }),
  ).toBeVisible();
}

async function finishSetup(page: Page) {
  await setPassword(page);
  await page.getByRole("button", { name: "Create a recovery key" }).click();
  await saveRecoveryKey(page);
  await page.getByRole("button", { name: "Continue to signing in" }).click();
  await expect(page).toHaveURL(/\/login\?sign-in-ready/);
}

for (const stage of ["password form", "recovery key confirmation"] as const) {
  test(`a second admin recovery revokes the unfinished ${stage} and cannot be overwritten by it`, async ({
    page,
    browser,
    gamma,
    env,
  }) => {
    const firstLink = await issueAdminRecoveryLink(page, gamma, "jhalpert");
    const context = await browser.newContext({ ignoreHTTPSErrors: true });
    try {
      const user = await context.newPage();
      await startAdminRecovery(user, firstLink);
      let key = "";
      if (stage === "recovery key confirmation") {
        await setPassword(user);
        await user
          .getByRole("button", { name: "Create a recovery key" })
          .click();
        key = (
          await user
            .getByLabel("Your recovery key", { exact: true })
            .innerText()
        ).trim();
        await user.getByRole("link", { name: "I have saved it" }).click();
      }
      const csrf = await csrfTokenOf(
        user.getByLabel(
          stage === "password form" ? "Password" : "Recovery key",
          { exact: true },
        ),
      );
      await logout(page);
      const newLink = await issueAdminRecoveryLink(page, gamma, "jhalpert");
      expect(newLink).not.toBe(firstLink);
      for (const [endpoint, form] of [
        [
          "password",
          {
            newPassword: "Stale-password-1337",
            confirmPassword: "Stale-password-1337",
          },
        ],
        ["recovery-key/confirm", { recoveryKey: key }],
        ["finish", {}],
      ] as const) {
        const response = await user.request.post(
          `${gamma.url}/sign-in-setup/${endpoint}`,
          {
            form: { ...form, _csrf: csrf },
            maxRedirects: 0,
          },
        );
        expect([302, 403]).toContain(response.status());
      }
      expect(
        await sql(
          env,
          "SELECT password IS NULL, email_recovery, email_recovery_requires_verification FROM g_user WHERE cid='jhalpert'",
        ),
      ).toBe("t|f|t");
      expect(
        await sql(
          env,
          "SELECT COUNT(*) FROM g_user_recovery_key WHERE user_id=(SELECT user_id FROM g_user WHERE cid='jhalpert')",
        ),
      ).toBe("0");
      await user.goto(`${gamma.url}/login`);
      await startAdminRecovery(user, newLink);
      await finishSetup(user);
      await login(
        user,
        gamma.url,
        "jhalpert",
        "Recovered-password-1337",
        "Big Tuna",
      );
    } finally {
      await context.close();
    }
  });
}

test("opening or scanning an admin recovery link does not consume it and responses prevent caching and referrer leakage", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  const link = await issueAdminRecoveryLink(page, gamma, "jhalpert");
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await context.newPage();
    for (let i = 0; i < 3; i++) {
      const response = await user.goto(link);
      expect(response!.headers()["cache-control"]).toContain("no-store");
      expect(response!.headers()["referrer-policy"]).toBe("no-referrer");
      await expect(
        user.getByRole("button", { name: "Set up new sign-in credentials" }),
      ).toBeVisible();
    }
    expect(
      await sql(
        env,
        "SELECT COUNT(*) FROM g_password_reset WHERE admin_recovery",
      ),
    ).toBe("1");
    await user
      .getByRole("button", { name: "Set up new sign-in credentials" })
      .click();
    await expect(user).toHaveURL(/\/sign-in-setup$/);
    expect(
      await sql(
        env,
        "SELECT COUNT(*) FROM g_password_reset WHERE admin_recovery",
      ),
    ).toBe("0");
  } finally {
    await context.close();
  }
});

test("recovery key rotation rejects the previous key and a second redemption revokes the earlier setup session", async ({
  page,
  browser,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(`${gamma.url}/me/security`);
  await page.getByRole("button", { name: "Turn off email recovery" }).click();
  const oldKey = await saveRecoveryKey(page);
  await expect(
    page.getByText("Email recovery is off. Keep", { exact: false }),
  ).toBeVisible();
  await page.getByRole("button", { name: "Create a new recovery key" }).click();
  const key = await saveRecoveryKey(page);
  await expect(
    page.getByText("Your new recovery key was saved."),
  ).toBeVisible();
  expect(key).not.toBe(oldKey);
  await logout(page);
  await page.goto(`${gamma.url}/login/recovery`);
  await page.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
  await page.getByLabel("Recovery key", { exact: true }).fill(oldKey);
  await page.getByRole("button", { name: "Continue", exact: true }).click();
  await expect(page).toHaveURL(/\/login\/recovery\?error/);
  await page.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
  await page.getByLabel("Recovery key", { exact: true }).fill(key);
  await page.getByRole("button", { name: "Continue", exact: true }).click();
  await expect(page).toHaveURL(/\/sign-in-setup$/);
  const csrf = await csrfTokenOf(page.getByLabel("Password", { exact: true }));
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const other = await context.newPage();
    await other.goto(`${gamma.url}/login/recovery`);
    await other.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
    await other.getByLabel("Recovery key", { exact: true }).fill(key);
    await other.getByRole("button", { name: "Continue", exact: true }).click();
    await expect(other).toHaveURL(/\/sign-in-setup$/);
    const stale = await page.request.post(
      `${gamma.url}/sign-in-setup/password`,
      {
        form: {
          _csrf: csrf,
          newPassword: "Stale-password-1337",
          confirmPassword: "Stale-password-1337",
        },
        maxRedirects: 0,
      },
    );
    expect(stale.status()).toBe(302);
    expect(stale.headers().location).not.toContain("/sign-in-setup");
    await finishSetup(other);
    await login(
      other,
      gamma.url,
      "jhalpert",
      "Recovered-password-1337",
      "Big Tuna",
    );
    await logout(other);
    await other.goto(`${gamma.url}/login/recovery`);
    await other.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
    await other.getByLabel("Recovery key", { exact: true }).fill(key);
    await other.getByRole("button", { name: "Continue", exact: true }).click();
    await expect(other).toHaveURL(/\/login\/recovery\?error/);
  } finally {
    await context.close();
  }
});

for (const scenario of [
  "another account",
  "expired link",
  "a second admin recovery",
] as const) {
  test(`an unverified recovered email stays protected against ${scenario}`, async ({
    page,
    browser,
    gamma,
    env,
  }) => {
    const link = await issueAdminRecoveryLink(page, gamma, "jhalpert");
    const context = await browser.newContext({ ignoreHTTPSErrors: true });
    try {
      const user = await context.newPage();
      await startAdminRecovery(user, link);
      await finishSetup(user);
      await login(
        user,
        gamma.url,
        "jhalpert",
        "Recovered-password-1337",
        "Big Tuna",
      );
      await user.getByRole("button", { name: "Change email" }).click();
      const newEmail = "current.address@example.org";
      await user.getByLabel("New email", { exact: true }).fill(newEmail);
      await user
        .getByLabel("Current password", { exact: true })
        .fill("Recovered-password-1337");
      await user
        .getByRole("button", { name: "Send confirmation link" })
        .click();
      await expect(user.getByText("We sent a confirmation link")).toBeVisible();
      const confirmation = await waitForMailLink(
        env.gotify,
        gamma.url,
        newEmail,
        "/me/confirm-email",
      );
      const token = new URL(confirmation).searchParams.get("token")!;
      if (scenario === "another account") {
        await logout(user);
        await login(user, gamma.url, "pbeesly", "password1337", "Pam-Pam");
      } else if (scenario === "expired link") {
        await sql(
          env,
          "UPDATE g_email_change SET created_at=now() - interval '61 minutes'",
        );
      } else {
        await logout(page);
        const freshLink = await issueAdminRecoveryLink(page, gamma, "jhalpert");
        await user.goto(`${gamma.url}/login`);
        await startAdminRecovery(user, freshLink);
        await finishSetup(user);
        await login(
          user,
          gamma.url,
          "jhalpert",
          "Recovered-password-1337",
          "Big Tuna",
        );
      }
      await user.goto(confirmation);
      await expect(
        user.getByRole("button", { name: "Confirm new email" }),
      ).toHaveCount(0);
      const csrf = await csrfTokenOf(
        user.getByRole("button", { name: "Logout", exact: true }),
      );
      const forged = await user.request.post(`${gamma.url}/me/confirm-email`, {
        form: { token, _csrf: csrf },
        maxRedirects: 0,
      });
      expect(forged.status()).toBeLessThan(500);
      expect(await forged.text()).not.toContain("Your email has been changed");
      expect(
        await sql(
          env,
          "SELECT email, email_recovery, email_recovery_requires_verification FROM g_user WHERE cid='jhalpert'",
        ),
      ).toBe("jhalpert@example.org|f|t");
      if (scenario === "another account") {
        await logout(user);
        await login(
          user,
          gamma.url,
          "jhalpert",
          "Recovered-password-1337",
          "Big Tuna",
        );
        await user.goto(confirmation);
        await user.getByRole("button", { name: "Confirm new email" }).click();
        await expect(
          user.getByText("Your email has been changed"),
        ).toBeVisible();
      }
    } finally {
      await context.close();
    }
  });
}
