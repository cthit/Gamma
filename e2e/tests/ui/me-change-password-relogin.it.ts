import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { uniqueCid } from "../../helpers/strings";
import { Buffer } from "node:buffer";

test("a user can edit their profile and rotate their password", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "pbeesly", "password1337", "Pam-Pam");

  await expect(page.locator('img[alt="Me avatar"]')).toHaveAttribute(
    "src",
    /\?v=0$/,
  );

  await page.locator('form#update-me-avatar input[name="file"]').setInputFiles({
    name: "avatar.png",
    mimeType: "image/png",
    buffer: Buffer.from(
      "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=",
      "base64",
    ),
  });
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().endsWith("/me/avatar"),
    ),
    page.getByRole("button", { name: "Upload avatar" }).click(),
  ]);

  await page.reload();
  await expect(page.locator('img[alt="Me avatar"]')).toHaveAttribute(
    "src",
    /\?v=1$/,
  );

  const updatedNick = uniqueCid("nick");
  await page.getByRole("button", { name: "Edit" }).click();
  await page.fill('input[name="nick"]', updatedNick);
  await page.getByRole("button", { name: "Save" }).click();
  await expect(
    page.getByText("You have successfully edited your information"),
  ).toBeVisible({ timeout: 10000 });
  await expect(
    page.locator("article", { hasText: "Your information" }),
  ).toContainText(updatedNick, { timeout: 10000 });

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

  await expect(page.getByText("You have created a new password")).toBeVisible({
    timeout: 10000,
  });

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

  await login(page, gamma.url, "pbeesly", newPassword, updatedNick);
});
