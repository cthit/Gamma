import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";

test("given an admin user when toggling gdpr trained for a user then the new gdpr state is saved", async ({
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

  await page.goto(`${gamma.url}/gdpr`, { timeout: 30000 });

  const userRow = page.locator("tr", { hasText: "Big Tuna" }).first();
  const checkbox = userRow.locator('input[type="checkbox"]');

  await expect(checkbox).toBeVisible({ timeout: 10000 });

  const wasChecked = await checkbox.isChecked();
  await checkbox.click();

  await Promise.all([
    page.waitForURL("**/gdpr", { timeout: 15000 }),
    page.getByRole("button", { name: "Save" }).click(),
  ]);

  const refreshedCheckbox = page
    .locator("tr", { hasText: "Big Tuna" })
    .first()
    .locator('input[type="checkbox"]');

  await expect(refreshedCheckbox).toBeVisible({ timeout: 10000 });
  expect(await refreshedCheckbox.isChecked()).toBe(!wasChecked);
});
