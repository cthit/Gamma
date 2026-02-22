import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";

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
  await page.fill('input[name="name"]', initialName);
  await page.fill('input[name="prettyName"]', initialPrettyName);

  await Promise.all([
    page.waitForURL("**/groups/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  await expect(page.locator("main > article").first()).toContainText(
    "Group details",
    {
      timeout: 10000,
    },
  );

  await page.getByRole("button", { name: "Edit" }).click();
  await expect(page.locator("main > article").first()).toContainText(
    "Edit group details",
    {
      timeout: 10000,
    },
  );

  await page.fill('input[name="name"]', updatedName);
  await page.fill('input[name="prettyName"]', updatedPrettyName);

  await Promise.all([
    page.waitForURL("**/groups/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Save" }).click(),
  ]);

  await expect(page.locator("article .tuple")).toContainText(updatedName, {
    timeout: 10000,
  });
  await expect(page.locator("article .tuple")).toContainText(
    updatedPrettyName,
    {
      timeout: 10000,
    },
  );
});
