import { expect, test } from "@playwright/test";
import { login } from "../helpers/auth";
import { startMockGamma } from "../helpers/gamma";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../gamma-setup";

test("given a user account when deleting it with correct password then login access is removed", async ({
  page,
}) => {
  const env = await startDependencies();
  const gamma = await startMockGamma(env);

  try {
    await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

    await page.goto(`${gamma.url}/delete-your-account`, { timeout: 30000 });
    await expect(page.locator("article > header")).toHaveText(
      "Deleting your account",
      { timeout: 10000 },
    );

    await page.fill('input[name="password"]', "password1337");

    await Promise.all([
      page.waitForURL("**/login?deleted", { timeout: 15000 }),
      page.getByRole("button", { name: "Delete the account" }).click(),
    ]);

    await expect(page.getByText("Your account has been deleted.")).toBeVisible({
      timeout: 10000,
    });

    await page.fill('input[name="username"]', "jhalpert");
    await page.fill('input[name="password"]', "password1337");

    await Promise.all([
      page.waitForNavigation({ timeout: 15000 }),
      page.getByRole("button", { name: "Login" }).click(),
    ]);

    await expect(
      page.getByText(
        "Invalid credentials or locked account due to system migration. Password reset may be needed.",
      ),
    ).toBeVisible({ timeout: 10000 });
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
