import { expect, test } from "@playwright/test";
import { login, logout } from "../../helpers/auth";
import { startMockGamma } from "../../helpers/gamma";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given a user account when deleting it with wrong password then deletion is rejected and account remains", async ({
  page,
}) => {
  const env = await startDependencies();
  const gamma = await startMockGamma(env);

  try {
    await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

    await page.goto(`${gamma.url}/delete-your-account`, { timeout: 30000 });
    await page.fill('input[name="password"]', "wrong-password");
    await page.getByRole("button", { name: "Delete the account" }).click();

    await expect(page.getByText("Incorrect password")).toBeVisible({
      timeout: 10000,
    });

    await logout(page);

    await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
    await expect(page.getByText("Hey, Big Tuna!")).toBeVisible({
      timeout: 10000,
    });
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
