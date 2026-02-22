import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";

test("given an admin user when creating a group then it is visible in groups and details", async ({ page, gamma }) => {

  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const groupName = uniqueCid("grp");
  const prettyName = uniqueLabel("E2E Group Pretty");

  await page.goto(`${gamma.url}/groups/create`, { timeout: 30000 });
  await expect(page.getByText("Create group")).toBeVisible({
    timeout: 10000,
  });

  await page.fill('input[name="name"]', groupName);
  await page.fill('input[name="prettyName"]', prettyName);

  await Promise.all([
    page.waitForURL("**/groups/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  await expect(page.getByText(prettyName)).toBeVisible({ timeout: 10000 });

  await page.goto(`${gamma.url}/groups`, { timeout: 30000 });
  await expect(page.getByText(prettyName)).toBeVisible({ timeout: 10000 });
});
