import { expect, test } from "@playwright/test";
import { login } from "../../helpers/auth";
import { startMockGamma } from "../../helpers/gamma";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given an admin user when deleting a created group then the group is removed", async ({
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

    const groupName = uniqueCid("grp");
    const prettyName = uniqueLabel("E2E Group Delete");

    await page.goto(`${gamma.url}/groups/create`, { timeout: 30000 });
    await page.fill('input[name="name"]', groupName);
    await page.fill('input[name="prettyName"]', prettyName);

    await Promise.all([
      page.waitForURL("**/groups/*", { timeout: 15000 }),
      page.getByRole("button", { name: "Create" }).click(),
    ]);

    await expect(page.getByText(prettyName)).toBeVisible({ timeout: 10000 });

    page.once("dialog", async (dialog) => dialog.accept());
    await Promise.all([
      page.waitForURL("**/groups", { timeout: 15000 }),
      page.getByRole("button", { name: "Delete" }).click(),
    ]);

    await expect(page.getByText(prettyName)).toHaveCount(0);
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
