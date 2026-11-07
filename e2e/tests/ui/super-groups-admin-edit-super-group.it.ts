import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import { articleByHeading } from "../../helpers/a11y";

test("given an admin user when editing a super group then updated details are shown", async ({
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
  const initialPrettyName = uniqueLabel("E2E Super Group");
  const updatedPrettyName = uniqueLabel("E2E Super Group Updated");
  const updatedEnDescription = uniqueLabel("E2E Updated Description");

  await page.goto(`${gamma.url}/super-groups/create`, { timeout: 30000 });
  await page.getByLabel("Name", { exact: true }).fill(name);
  await page.getByLabel("Pretty name", { exact: true }).fill(initialPrettyName);
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

  await page.getByRole("button", { name: "Edit" }).click();
  await expect(
    page.getByRole("heading", { level: 2, name: /^Edit / }),
  ).toBeVisible({ timeout: 10000 });

  await page.getByLabel("Pretty name", { exact: true }).fill(updatedPrettyName);
  await page
    .getByLabel("English description", { exact: true })
    .fill(updatedEnDescription);

  await page.getByRole("button", { name: "Save" }).click();

  const superGroupInfo = articleByHeading(page, updatedPrettyName);
  await expect(superGroupInfo).toContainText(updatedPrettyName, {
    timeout: 15000,
  });
  await expect(superGroupInfo).toContainText(updatedEnDescription, {
    timeout: 15000,
  });
});
