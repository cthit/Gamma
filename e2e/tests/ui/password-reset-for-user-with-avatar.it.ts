import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { waitForMailLink } from "../../helpers/mail";
import { issueAdminRecoveryLink } from "../../helpers/admin-recovery";
import type { Page } from "@playwright/test";

async function uploadAvatar(page: Page) {
  await page.getByLabel("Choose a new avatar", { exact: true }).setInputFiles({
    name: "avatar.png",
    mimeType: "image/png",
    buffer: Buffer.from(
      "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+a9l8AAAAASUVORK5CYII=",
      "base64",
    ),
  });
  await page
    .getByRole("button", { name: "Upload avatar", exact: true })
    .click();
  await expect(
    page.getByText("You have uploaded a new avatar", { exact: true }),
  ).toBeVisible();
}

test("a user with an avatar can reset their password by email", async ({
  page,
  env,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await uploadAvatar(page);
  await logout(page);

  await page.goto(`${gamma.url}/forgot-password`);
  await page.getByLabel("Cid or Email", { exact: true }).fill("jhalpert");
  await page.getByRole("button", { name: /reset|send/i }).click();
  await page.goto(
    await waitForMailLink(
      env.gotify,
      gamma.url,
      "jhalpert@example.org",
      "/forgot-password/finalize",
    ),
  );
  const newPassword = "avatar-password-1337";
  await page.getByLabel("Password", { exact: true }).fill(newPassword);
  await page.getByLabel("Confirm password", { exact: true }).fill(newPassword);
  await page.getByRole("button", { name: "Reset password" }).click();
  await expect(page).toHaveURL(/\/login\?password-reset/);
  await login(page, gamma.url, "jhalpert", newPassword, "Big Tuna");
});

test("an admin can issue a recovery link for a user with an avatar", async ({
  page,
  browser,
  gamma,
}) => {
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await context.newPage();
    await login(user, gamma.url, "jhalpert", "password1337", "Big Tuna");
    await uploadAvatar(user);
  } finally {
    await context.close();
  }

  expect(await issueAdminRecoveryLink(page, gamma, "jhalpert")).toContain(
    "/admin-recovery?token=",
  );
});
