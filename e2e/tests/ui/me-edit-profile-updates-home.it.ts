import { expect, test } from "@playwright/test";
import { login } from "../../helpers/auth";
import { startMockGamma } from "../../helpers/gamma";
import { uniqueCid } from "../../helpers/strings";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given a signed in user when editing profile info then home page shows updated values", async ({
  page,
}) => {
  const env = await startDependencies();
  const gamma = await startMockGamma(env);

  try {
    await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

    const updatedNick = uniqueCid("nick");

    await page.getByRole("button", { name: "Edit" }).click();
    await expect(page.getByText("Editing your information")).toBeVisible({
      timeout: 10000,
    });

    await page.fill('input[name="nick"]', updatedNick);

    await Promise.all([
      page.waitForResponse(
        (response) =>
          response.request().method() === "POST" &&
          response.url().includes("/me") &&
          response.status() >= 200 &&
          response.status() < 400,
      ),
      page.getByRole("button", { name: "Save" }).click(),
    ]);

    await expect(
      page.getByText("You have successfully edited your information"),
    ).toBeVisible({ timeout: 10000 });

    await expect(
      page.locator("article", { hasText: "Your information" }),
    ).toContainText(updatedNick, { timeout: 10000 });
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
