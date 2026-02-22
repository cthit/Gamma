import { expect, test } from "@playwright/test";
import { login } from "../../helpers/auth";
import { startMockGamma } from "../../helpers/gamma";
import { uniqueCid } from "../../helpers/strings";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given an admin user when editing another user then updated details are shown", async ({
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

    const updatedNick = uniqueCid("nick");
    const updatedFirstName = uniqueCid("first");

    await page.goto(`${gamma.url}/users`, { timeout: 30000 });
    await page
      .locator("tr", { hasText: "jhalpert" })
      .getByRole("link", { name: "Details" })
      .click();

    await expect(page.getByText("User details")).toBeVisible({
      timeout: 10000,
    });

    await page.getByRole("button", { name: "Edit user" }).click();
    await page.fill('input[name="nick"]', updatedNick);
    await page.fill('input[name="firstName"]', updatedFirstName);

    await Promise.all([
      page.waitForResponse(
        (response) =>
          response.request().method() === "POST" &&
          response.url().includes("/users/") &&
          response.status() >= 200 &&
          response.status() < 400,
      ),
      page.getByRole("button", { name: "Save" }).click(),
    ]);

    await expect(page.getByText("User updated")).toBeVisible({
      timeout: 10000,
    });
    await expect(page.locator("article .tuple")).toContainText(
      updatedFirstName,
      {
        timeout: 10000,
      },
    );
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
