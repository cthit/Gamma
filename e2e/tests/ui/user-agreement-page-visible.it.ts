import { expect, test } from "@playwright/test";
import { login } from "../../helpers/auth";
import { startMockGamma } from "../../helpers/gamma";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given a signed in user when opening user agreement then agreement content is shown", async ({
  page,
}) => {
  const env = await startDependencies();
  const gamma = await startMockGamma(env);

  try {
    await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

    await page.goto(`${gamma.url}/user-agreement`, { timeout: 30000 });

    await expect(page.locator("main article > header")).toHaveText(
      "User agreement",
      {
        timeout: 10000,
      },
    );
    await expect(page.locator("main article")).toContainText(
      "This agreement refers to IT:s user account system Gamma.",
      {
        timeout: 10000,
      },
    );
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
