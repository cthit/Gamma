import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import { rowWithText } from "../../helpers/a11y";

test("given an admin user when creating and deleting a super group then it is removed", async ({
  page,
  gamma,
}) => {
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
  await expect(
    page.getByRole("heading", {
      level: 2,
      name: "Create super group",
      exact: true,
    }),
  ).toBeVisible({
    timeout: 10000,
  });

  await page.getByLabel("Name", { exact: true }).fill(name);
  await page.getByLabel("Pretty name", { exact: true }).fill(prettyName);
  await page
    .getByLabel("Swedish description", { exact: true })
    .fill("E2E svensk beskrivning");
  await page
    .getByLabel("English description", { exact: true })
    .fill("E2E english description");

  await page
    .getByRole("combobox", { name: "Type", exact: true })
    .selectOption({ index: 0 });

  await Promise.all([
    page.waitForURL("**/super-groups/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  await expect(
    page.getByRole("heading", { level: 2, name: prettyName, exact: true }),
  ).toBeVisible({ timeout: 10000 });

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/super-groups", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete" }).click(),
  ]);

  await expect(rowWithText(page, prettyName)).toHaveCount(0);
});
