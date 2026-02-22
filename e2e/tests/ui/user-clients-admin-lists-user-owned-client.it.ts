import { expect, test } from "@playwright/test";
import { login, logout } from "../../helpers/auth";
import { startMockGamma } from "../../helpers/gamma";
import { createUserClientViaUi } from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given a user-owned client when admin opens user clients page then the client is listed", async ({
  page,
}) => {
  const env = await startDependencies();
  const gamma = await startMockGamma(env);

  try {
    await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

    const prettyName = uniqueLabel("E2E User Client");
    await createUserClientViaUi(page, gamma.url, prettyName);

    await logout(page);

    await login(
      page,
      gamma.url,
      gamma.adminCid ?? "",
      gamma.adminPassword ?? "",
      "admin",
    );

    await page.goto(`${gamma.url}/user-clients`, { timeout: 30000 });

    const row = page.locator("tr", { hasText: prettyName }).first();
    await expect(row).toBeVisible({ timeout: 10000 });
    await expect(row.getByRole("link", { name: /Jim .*Halpert/ })).toBeVisible({
      timeout: 10000,
    });
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
