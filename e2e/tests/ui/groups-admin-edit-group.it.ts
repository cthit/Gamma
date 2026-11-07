import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import { articleByHeading } from "../../helpers/a11y";

test("given an admin user when editing a group then updated group details are shown", async ({
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

  const initialName = uniqueCid("grp");
  const initialPrettyName = uniqueLabel("E2E Group");
  const updatedName = uniqueCid("grp");
  const updatedPrettyName = uniqueLabel("E2E Group Updated");

  await page.goto(`${gamma.url}/groups/create`, { timeout: 30000 });
  await page.getByLabel("Name", { exact: true }).fill(initialName);
  await page.getByLabel("Pretty name", { exact: true }).fill(initialPrettyName);

  await Promise.all([
    page.waitForURL("**/groups/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  await expect(
    page.getByRole("heading", { level: 2, name: "Group details", exact: true }),
  ).toBeVisible({
    timeout: 10000,
  });

  await page.getByRole("button", { name: "Edit" }).click();
  await expect(
    page.getByRole("heading", {
      level: 2,
      name: "Edit group details",
      exact: true,
    }),
  ).toBeVisible({
    timeout: 10000,
  });

  await page.getByLabel("Name", { exact: true }).fill(updatedName);
  await page.getByLabel("Pretty name", { exact: true }).fill(updatedPrettyName);

  await Promise.all([
    page.waitForURL("**/groups/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Save" }).click(),
  ]);

  await expect(articleByHeading(page, "Group details")).toContainText(
    updatedName,
    {
      timeout: 10000,
    },
  );
  await expect(articleByHeading(page, "Group details")).toContainText(
    updatedPrettyName,
    {
      timeout: 10000,
    },
  );
});
