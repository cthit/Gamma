import { expect, test } from "@playwright/test";
import { login } from "../../helpers/auth";
import { startMockGamma } from "../../helpers/gamma";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given an admin user when editing a super group then updated details are shown", async ({
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
    const initialPrettyName = uniqueLabel("E2E Super Group");
    const updatedPrettyName = uniqueLabel("E2E Super Group Updated");
    const updatedEnDescription = uniqueLabel("E2E Updated Description");

    await page.goto(`${gamma.url}/super-groups/create`, { timeout: 30000 });
    await page.fill('input[name="name"]', name);
    await page.fill('input[name="prettyName"]', initialPrettyName);
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

    await page.getByRole("button", { name: "Edit" }).click();
    await expect(page.locator("article > header").first()).toContainText(
      "Edit",
      {
        timeout: 10000,
      },
    );

    await page.fill('input[name="prettyName"]', updatedPrettyName);
    await page.fill('input[name="enDescription"]', updatedEnDescription);

    await page.getByRole("button", { name: "Save" }).click();

    await expect(page.locator("main > article").first()).toContainText(
      updatedPrettyName,
      {
        timeout: 15000,
      },
    );
    await expect(page.locator("main > article").first()).toContainText(
      updatedEnDescription,
      {
        timeout: 15000,
      },
    );
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
