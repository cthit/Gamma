import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";

test("account deletion rejects a wrong password and removes login access after confirmation", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  await page.goto(`${gamma.url}/delete-your-account`, { timeout: 30000 });
  await page.fill('input[name="password"]', "wrong-password");
  await page.getByRole("button", { name: "Delete the account" }).click();

  await expect(page.getByText("Incorrect password")).toBeVisible({
    timeout: 10000,
  });

  await page.fill('input[name="password"]', "password1337");
  await Promise.all([
    page.waitForURL("**/login?deleted", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete the account" }).click(),
  ]);
  await expect(page.getByText("Your account has been deleted.")).toBeVisible({
    timeout: 10000,
  });

  await page.fill('input[name="username"]', "jhalpert");
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
});
