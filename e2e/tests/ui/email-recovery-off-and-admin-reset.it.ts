import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { saveRecoveryKey } from "../../helpers/recovery-key";
import { waitForMailLink } from "../../helpers/mail";
import { addPasskey, signInWithPasskey } from "../../helpers/passkeys";
import {
  issueAdminRecoveryLink,
  startAdminRecovery,
} from "../../helpers/admin-recovery";
import { csrfTokenOf } from "../../helpers/a11y";

test("admin recovery works without old email and only permits email recovery after verifying a current address", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  await login(page, gamma.url, "amartin", "password1337", "Pumpkin");
  await page.goto(`${gamma.url}/me/security`);
  await page.getByRole("button", { name: "Turn off email recovery" }).click();
  await saveRecoveryKey(page);
  await expect(page.getByText(/Email recovery is off\. Keep/)).toBeVisible();
  await logout(page);
  const link = await issueAdminRecoveryLink(page, gamma, "amartin");
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await context.newPage();
    await user.goto(`${gamma.url}/forgot-password`);
    await user
      .getByLabel("Cid or Email", { exact: true })
      .fill("amartin@example.org");
    await user.getByRole("button", { name: /reset|send/i }).click();
    await expect(
      waitForMailLink(
        env.gotify,
        gamma.url,
        "amartin@example.org",
        "/forgot-password/finalize",
        3000,
      ),
    ).rejects.toThrow();
    await startAdminRecovery(user, link);
    const csrf = await csrfTokenOf(
      user.getByRole("button", { name: "Use a password" }),
    );
    const blocked = await user.request.post(
      `${gamma.url}/me/security/email-recovery/enable`,
      { form: { _csrf: csrf }, maxRedirects: 0 },
    );
    expect(blocked.status()).toBe(302);
    await addPasskey(user, "Recovered phone");
    await user.getByRole("button", { name: "Create a recovery key" }).click();
    await saveRecoveryKey(user);
    await user.getByRole("button", { name: "Continue to signing in" }).click();
    await expect(user).toHaveURL(/\/login\?sign-in-ready/);
    await signInWithPasskey(user);
    await expect(user.getByText("Hey, Pumpkin!")).toBeVisible();
    await user.goto(`${gamma.url}/me/security`);
    const rejected = await user.request.post(
      `${gamma.url}/me/security/email-recovery/enable`,
      {
        form: {
          _csrf: await csrfTokenOf(
            user.getByRole("button", { name: "Create a new recovery key" }),
          ),
        },
        maxRedirects: 0,
      },
    );
    expect(rejected.headers().location).toContain("recovery-email-unverified");
    await user.goto(gamma.url);
    await user.getByRole("button", { name: "Change email" }).click();
    const newEmail = "pumpkin.current@example.org";
    await user.getByLabel("New email", { exact: true }).fill(newEmail);
    await user.getByRole("button", { name: "Send confirmation link" }).click();
    await expect(
      user.getByText(`We sent a confirmation link to ${newEmail}`),
    ).toBeVisible();
    await user.goto(
      await waitForMailLink(
        env.gotify,
        gamma.url,
        newEmail,
        "/me/confirm-email",
      ),
    );
    await user.getByRole("button", { name: "Confirm new email" }).click();
    await expect(user.getByText("Your email has been changed")).toBeVisible();
    await user.goto(`${gamma.url}/me/security`);
    await user.getByRole("button", { name: "Turn on email recovery" }).click();
    await expect(
      user.getByText("Email recovery is on.", { exact: true }),
    ).toBeVisible();
    const result = await env.postgres.exec([
      "psql",
      "-U",
      "postgres",
      "-d",
      "postgres",
      "-At",
      "-c",
      "SELECT email, email_recovery, email_recovery_requires_verification FROM g_user WHERE cid='amartin'",
    ]);
    expect(result.stdout.trim()).toBe(`${newEmail}|t|f`);
  } finally {
    await context.close();
  }
});
