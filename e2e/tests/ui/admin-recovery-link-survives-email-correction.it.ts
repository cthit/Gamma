import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import {
  issueAdminRecoveryLink,
  saveSetupRecoveryKey,
  startAdminRecovery,
} from "../../helpers/admin-recovery";
import { rowWithText } from "../../helpers/a11y";
import { login } from "../../helpers/auth";
import { waitForMailLink } from "../../helpers/mail";

test("an admin recovery link keeps working after the admin corrects the user's email, while mailed reset links don't", async ({
  page,
  browser,
  env,
  gamma,
}) => {
  const mailed = await browser.newContext({ ignoreHTTPSErrors: true });
  let resetLink: string;
  try {
    const other = await mailed.newPage();
    await other.goto(`${gamma.url}/forgot-password`);
    await other.getByLabel("Cid or Email", { exact: true }).fill("pbeesly");
    await other.getByRole("button", { name: /reset|send/i }).click();
    resetLink = await waitForMailLink(
      env.gotify,
      gamma.url,
      "pbeesly@example.org",
      "/forgot-password/finalize",
    );
  } finally {
    await mailed.close();
  }

  const link = await issueAdminRecoveryLink(page, gamma, "jhalpert");
  for (const [cid, email] of [
    ["jhalpert", "tuna.fixed@example.org"],
    ["pbeesly", "pam.fixed@example.org"],
  ] as const) {
    await page.goto(`${gamma.url}/users`);
    await rowWithText(page, cid).getByRole("link", { name: "Details" }).click();
    await page.getByRole("button", { name: "Edit user" }).click();
    await page.getByLabel("Email", { exact: true }).fill(email);
    await page.getByRole("button", { name: "Save" }).click();
    await expect(page.getByText("User updated")).toBeVisible();
  }

  const ctx = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await ctx.newPage();
    await user.goto(resetLink);
    await expect(user.getByLabel("Password", { exact: true })).toHaveCount(0);
    await expect(
      user.getByText(/invalid|expired|already|not valid/i),
    ).toBeVisible();

    await startAdminRecovery(user, link);
    await saveSetupRecoveryKey(user);
    await user
      .getByLabel("Password", { exact: true })
      .fill("Recovered-password-1337");
    await user
      .getByLabel("Confirm password", { exact: true })
      .fill("Recovered-password-1337");
    await user.getByRole("button", { name: "Use a password" }).click();
    await user.getByRole("button", { name: "Continue to signing in" }).click();
    await expect(user).toHaveURL(/\/login\?sign-in-ready/);
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
