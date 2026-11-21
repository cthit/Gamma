import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid } from "../../helpers/strings";

test("given a signed in user when changing password then old password fails and new password works", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "pbeesly", "password1337", "Pam-Pam");

  const newPassword = `${uniqueCid("newpass")}value`;

  await page.getByRole("button", { name: "Change password" }).click();
  await expect(page.getByText("Creating a new password")).toBeVisible({
    timeout: 10000,
  });

  await page
    .getByLabel("Current password", { exact: true })
    .fill("password1337");
  await page.getByLabel("New password", { exact: true }).fill(newPassword);
  await page
    .getByLabel("Confirm new password", { exact: true })
    .fill(newPassword);

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/me/edit-password") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.getByRole("button", { name: "Save new password" }).click(),
  ]);

  await expect(page).toHaveURL(/\/login\?security-changed/);

  await page.getByLabel("Cid or email", { exact: true }).fill("pbeesly");
  await page.getByLabel("Password", { exact: true }).fill("password1337");

  await Promise.all([
    page.waitForURL("**/login?error**", { timeout: 15000 }),
    page.getByRole("button", { name: "Login" }).click(),
  ]);

  await expect(
    page.getByText(
      "Invalid credentials or locked account due to system migration. Password reset may be needed.",
    ),
  ).toBeVisible({ timeout: 10000 });

  await login(page, gamma.url, "pbeesly", newPassword, "Pam-Pam");
});
