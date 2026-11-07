import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";

test("given a user account when deleting it with correct password then login access is removed", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  await page.goto(`${gamma.url}/delete-your-account`, { timeout: 30000 });
  await expect(
    page.getByRole("heading", {
      level: 2,
      name: "Deleting your account",
      exact: true,
    }),
  ).toBeVisible({ timeout: 10000 });

  await page
    .getByLabel("Confirm password", { exact: true })
    .fill("password1337");

  await Promise.all([
    page.waitForURL("**/login?deleted", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete the account" }).click(),
  ]);

  await expect(page.getByText("Your account has been deleted.")).toBeVisible({
    timeout: 10000,
  });

  await page.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
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
});
