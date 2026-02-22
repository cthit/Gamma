import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";

test("given a cid when requesting forgot password then reset instructions are shown", async ({
  page,
  gamma,
}) => {
  await page.goto(`${gamma.url}/forgot-password`, { timeout: 30000 });
  await expect(
    page.locator("article > header", { hasText: "Reset password" }),
  ).toBeVisible({ timeout: 10000 });

  await page.fill('input[name="cidOrEmail"]', "jhalpert");

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/forgot-password") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.getByRole("button", { name: "Reset password" }).click(),
  ]);

  await expect(
    page.getByText(
      "You should have received an email with a link for resetting your password.",
    ),
  ).toBeVisible({ timeout: 10000 });
});
