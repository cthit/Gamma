import { expect, test } from "@playwright/test";
import { login } from "../../helpers/auth";
import { startMockGamma } from "../../helpers/gamma";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given an admin user when creating and deleting a super group then it is removed", async ({
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

    const name = uniqueCid("sgp");
    const prettyName = uniqueLabel("E2E Super Group");

    await page.goto(`${gamma.url}/super-groups/create`, { timeout: 30000 });
    await expect(page.locator("article > header")).toHaveText(
      "Create super group",
      {
        timeout: 10000,
      },
    );

    await page.fill('input[name="name"]', name);
    await page.fill('input[name="prettyName"]', prettyName);
    await page.fill('input[name="svDescription"]', "E2E svensk beskrivning");
    await page.fill('input[name="enDescription"]', "E2E english description");

    const firstType = await page
      .locator('select[name="type"] option')
      .first()
      .getAttribute("value");
    await page.selectOption('select[name="type"]', firstType ?? "");

    await Promise.all([
      page.waitForURL("**/super-groups/*", { timeout: 15000 }),
      page.getByRole("button", { name: "Create" }).click(),
    ]);

    await expect(page.locator("main > article").first()).toContainText(
      prettyName,
      {
        timeout: 10000,
      },
    );

    page.once("dialog", async (dialog) => dialog.accept());
    await Promise.all([
      page.waitForURL("**/super-groups", { timeout: 15000 }),
      page.getByRole("button", { name: "Delete" }).click(),
    ]);

    await expect(page.locator("tbody tr", { hasText: prettyName })).toHaveCount(
      0,
    );
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
