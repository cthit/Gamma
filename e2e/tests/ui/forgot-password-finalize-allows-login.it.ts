import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { uniqueCid } from "../../helpers/strings";

test("given a generated password reset link when finalizing reset then the user can login with new password", async ({
  page,
  gamma,
}) => {
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  await page.goto(`${gamma.url}/users`, { timeout: 30000 });
  await page
    .locator("tr", { hasText: "jhalpert" })
    .getByRole("link", { name: "Details" })
    .click();

  const passwordLinkArticle = page.locator(
    "article:has-text('Generate reset password link')",
  );

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/generate-password-link") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    passwordLinkArticle.getByRole("button", { name: "Generate" }).click(),
  ]);

  const resetLink = (
    await passwordLinkArticle.locator("code").innerText()
  ).trim();
  const resetUrl = new URL(resetLink);
  const reachableResetLink = `${gamma.url}${resetUrl.pathname}${resetUrl.search}`;

  expect(resetLink).toContain("/forgot-password/finalize?token=");

  const newPassword = `${uniqueCid("reset")}-password`;

  await logout(page);

  await page.goto(reachableResetLink, { timeout: 30000 });
  await expect(page.getByText("Finalize resetting password")).toBeVisible({
    timeout: 10000,
  });

  await page.fill('input[name="password"]', newPassword);
  await page.fill('input[name="confirmPassword"]', newPassword);

  await Promise.all([
    page.waitForURL("**/login?password-reset", { timeout: 15000 }),
    page.getByRole("button", { name: "Reset password" }).click(),
  ]);

  await expect(page.getByText("Your password was reset.")).toBeVisible({
    timeout: 10000,
  });

  await login(page, gamma.url, "jhalpert", newPassword, "Big Tuna");
});
