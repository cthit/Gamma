import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueEmail } from "../../helpers/strings";

test("an admin can create edit and delete a user", async ({ page, gamma }) => {
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const cid = uniqueCid("usr");
  const email = uniqueEmail("usr");
  const password = "E2e-Harbor-Quartz-47";

  await page.goto(`${gamma.url}/users/create`, { timeout: 30000 });
  await expect(page.locator("article > header")).toHaveText("Create user", {
    timeout: 10000,
  });

  await page.fill('input[name="firstName"]', "E2E");
  await page.fill('input[name="lastName"]', "Created");
  await page.fill('input[name="nick"]', "E2ENick");
  await page.fill('input[name="cid"]', cid);
  await page.fill('input[name="email"]', email);
  await page.fill('input[name="password"]', password);
  await page.selectOption('select[name="language"]', "EN");

  await Promise.all([
    page.waitForURL("**/users/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create user" }).click(),
  ]);

  await expect(page.getByText(cid)).toBeVisible({ timeout: 10000 });

  const updatedNick = uniqueCid("nick");
  await page.getByRole("button", { name: "Edit user" }).click();
  await page.fill('input[name="nick"]', updatedNick);
  await page.fill('input[name="firstName"]', "Updated");
  await page.getByRole("button", { name: "Save" }).click();
  await expect(page.getByText("User updated")).toBeVisible({
    timeout: 10000,
  });
  await expect(page.locator("article .tuple")).toContainText("Updated", {
    timeout: 10000,
  });

  await page.goto(`${gamma.url}/users`, { timeout: 30000 });

  const userSearch = page.locator('form[action="/users"]');
  await userSearch.locator('input[name="query"]').fill("Updated Created");
  await Promise.all([
    page.waitForURL(/\/users\?query=Updated(?:\+|%20)Created/, {
      timeout: 15000,
    }),
    userSearch.getByRole("button", { name: "Search" }).click(),
  ]);

  await expect(page.getByText(cid)).toBeVisible({ timeout: 10000 });

  await page
    .locator("tr", { hasText: cid })
    .getByRole("link", { name: "Details" })
    .click();
  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/users", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete user" }).click(),
  ]);
  await expect(page.getByText(cid)).toHaveCount(0);
});
