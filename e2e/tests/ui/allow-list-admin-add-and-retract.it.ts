import { expect, test } from "@playwright/test";
import { login } from "../../helpers/auth";
import { startDefaultGamma } from "../../helpers/gamma";
import { uniqueCid } from "../../helpers/strings";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given an admin user when adding and retracting allow-list access then the cid is removed", async ({
  page,
}) => {
  const env = await startDependencies();
  const gamma = await startDefaultGamma(env);

  try {
    await login(
      page,
      gamma.url,
      gamma.adminCid ?? "",
      gamma.adminPassword ?? "",
      "admin",
    );

    const cid = uniqueCid("allow").slice(0, 10);

    await page.goto(`${gamma.url}/allow-list`, { timeout: 30000 });
    await page.fill('input[name="cid"]', cid);

    await Promise.all([
      page.waitForURL("**/allow-list", { timeout: 15000 }),
      page.locator('button[form="allow-cid-form"]').click(),
    ]);

    const cidRow = page.locator("tr", { hasText: cid }).first();
    await expect(cidRow).toBeVisible({ timeout: 10000 });

    await cidRow.getByRole("button", { name: "Retract approval" }).click();

    await expect(page.locator("tr", { hasText: cid })).toHaveCount(0);
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
