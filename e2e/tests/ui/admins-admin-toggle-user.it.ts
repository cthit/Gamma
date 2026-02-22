import { expect, test } from "@playwright/test";
import { login } from "../../helpers/auth";
import { startMockGamma } from "../../helpers/gamma";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given an admin user when toggling another admin then the new admin state is saved", async ({
  page,
}) => {
  const env = await startDependencies();
  const gamma = await startMockGamma(env);

  try {
    await login(
      page,
      gamma.url,
      gamma.adminCid ?? "",
      gamma.adminPassword ?? "",
      "admin",
    );

    await page.goto(`${gamma.url}/admins`, { timeout: 30000 });

    const userRow = page.locator("tr", { hasText: "Big Tuna" }).first();
    const checkbox = userRow.locator('input[type="checkbox"]');

    await expect(checkbox).toBeVisible({ timeout: 10000 });

    const wasChecked = await checkbox.isChecked();
    await checkbox.click();

    await Promise.all([
      page.waitForURL("**/admins", { timeout: 15000 }),
      page.getByRole("button", { name: "Save" }).click(),
    ]);

    const refreshedCheckbox = page
      .locator("tr", { hasText: "Big Tuna" })
      .first()
      .locator('input[type="checkbox"]');

    await expect(refreshedCheckbox).toBeVisible({ timeout: 10000 });
    expect(await refreshedCheckbox.isChecked()).toBe(!wasChecked);
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
