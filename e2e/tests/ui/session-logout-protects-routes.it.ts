import { expect, test } from "@playwright/test";
import { login, logout } from "../../helpers/auth";
import { startDefaultGamma } from "../../helpers/gamma";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given a signed in session when logging out then protected routes require login", async ({
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

    await logout(page);

    await page.goto(`${gamma.url}/users`, { timeout: 30000 });

    await expect(page.locator('input[name="username"]')).toBeVisible({
      timeout: 10000,
    });
    await expect(page).toHaveURL(/\/login/);
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
