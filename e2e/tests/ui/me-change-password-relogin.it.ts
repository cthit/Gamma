import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { uniqueCid } from "../../helpers/strings";

test("given a signed in user when changing password then old password fails and new password works", async ({ page, gamma }) => {

  await login(page, gamma.url, "pbeesly", "password1337", "Pam-Pam");

  const newPassword = `${uniqueCid("newpass")}value`;

  await page.getByRole("button", { name: "Change password" }).click();
  await expect(page.getByText("Creating a new password")).toBeVisible({
    timeout: 10000,
  });

  await page.fill('input[name="currentPassword"]', "password1337");
  await page.fill('input[name="newPassword"]', newPassword);
  await page.fill('input[name="confirmNewPassword"]', newPassword);

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

  await expect(page.getByText("You have created a new password")).toBeVisible(
    {
      timeout: 10000,
    },
  );

  await logout(page);

  await page.fill('input[name="username"]', "pbeesly");
  await page.fill('input[name="password"]', "password1337");

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
