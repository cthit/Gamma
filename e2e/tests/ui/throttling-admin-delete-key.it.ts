import { expect, test } from "@playwright/test";
import { login } from "../../helpers/auth";
import { startMockGamma } from "../../helpers/gamma";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given a throttling key when admin deletes it then it is removed from throttling", async ({
  page,
}) => {
  const env = await startDependencies();
  const gamma = await startMockGamma(env);

  try {
    await page.goto(`${gamma.url}/forgot-password`, { timeout: 30000 });
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

    await login(
      page,
      gamma.url,
      gamma.adminCid ?? "",
      gamma.adminPassword ?? "",
      "admin",
    );

    await page.goto(`${gamma.url}/throttling`, { timeout: 30000 });

    const throttledRow = page.locator("tbody tr").first();
    await expect(throttledRow).toBeVisible({ timeout: 10000 });

    const key = (await throttledRow.locator("td").first().innerText()).trim();

    await throttledRow.getByRole("button", { name: "Delete" }).click();

    await expect(page.locator("tr", { hasText: key })).toHaveCount(0);
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
