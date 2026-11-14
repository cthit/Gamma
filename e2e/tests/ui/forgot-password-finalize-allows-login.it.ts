import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import {
  issueAdminRecoveryLink,
  startAdminRecovery,
} from "../../helpers/admin-recovery";
import { waitForMailLink } from "../../helpers/mail";
import { saveRecoveryKey } from "../../helpers/recovery-key";

test("admin recovery lets a user choose a password and requires saving a recovery key", async ({
  page,
  gamma,
  env,
}) => {
  const link = await issueAdminRecoveryLink(page, gamma, "jhalpert");
  await logout(page);
  await startAdminRecovery(page, link);
  const newPassword = "recovered-password-1337";
  await page.getByLabel("Password", { exact: true }).fill(newPassword);
  await page.getByLabel("Confirm password", { exact: true }).fill(newPassword);
  await page.getByRole("button", { name: "Use a password" }).click();
  await expect(
    page.getByRole("heading", { name: "Save a new recovery key" }),
  ).toBeVisible();
  await page.getByRole("button", { name: "Create a recovery key" }).click();
  await saveRecoveryKey(page);
  await page.getByRole("button", { name: "Continue to signing in" }).click();
  await expect(page).toHaveURL(/\/login\?sign-in-ready/);
  await page.goto(link);
  await expect(
    page.getByRole("button", { name: "Set up new sign-in credentials" }),
  ).toHaveCount(0);
  await login(page, gamma.url, "jhalpert", newPassword, "Big Tuna");
  await page.goto(`${gamma.url}/me/security`);
  await expect(
    page.getByText("Verify a current email address", { exact: false }),
  ).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Turn on email recovery" }),
  ).toHaveCount(0);
  await page.goto(gamma.url);
  await page.getByRole("button", { name: "Change email" }).click();
  await page
    .getByLabel("New email", { exact: true })
    .fill("jhalpert@example.org");
  await page.getByLabel("Current password", { exact: true }).fill(newPassword);
  await page.getByRole("button", { name: "Send confirmation link" }).click();
  await expect(page.getByText("We sent a confirmation link")).toBeVisible();
  await page.goto(
    await waitForMailLink(
      env.gotify,
      gamma.url,
      "jhalpert@example.org",
      "/me/confirm-email",
    ),
  );
  await page.getByRole("button", { name: "Confirm new email" }).click();
  await expect(page.getByText("Your email has been changed")).toBeVisible();
  await page.goto(`${gamma.url}/me/security`);
  await expect(
    page.getByRole("button", { name: "Turn on email recovery" }),
  ).toBeVisible();
});
