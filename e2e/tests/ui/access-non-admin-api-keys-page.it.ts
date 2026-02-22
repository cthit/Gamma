import { expect, test } from "@playwright/test";
import { login } from "../../helpers/auth";
import { startMockGamma } from "../../helpers/gamma";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given a non admin user when opening api keys page then unauthorized is shown", async ({
  page,
}) => {
  const env = await startDependencies();
  const gamma = await startMockGamma(env);

  try {
    await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

    await page.goto(`${gamma.url}/api-keys`, { timeout: 30000 });

    await expect(page.getByText("403 - Unauthorized")).toBeVisible({
      timeout: 10000,
    });
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
