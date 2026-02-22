import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueEmail } from "../../helpers/strings";

test("given an admin user when creating a user then the new user is visible in users and details", async ({
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

  const cid = uniqueCid("usr");
  const email = uniqueEmail("usr");

  await page.goto(`${gamma.url}/users/create`, { timeout: 30000 });
  await expect(page.locator("article > header")).toHaveText("Create user", {
    timeout: 10000,
  });

  await page.fill('input[name="firstName"]', "E2E");
  await page.fill('input[name="lastName"]', "Created");
  await page.fill('input[name="nick"]', "E2ENick");
  await page.fill('input[name="cid"]', cid);
  await page.fill('input[name="email"]', email);
  await page.fill('input[name="password"]', "password1337");
  await page.selectOption('select[name="language"]', "EN");

  await Promise.all([
    page.waitForURL("**/users/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create user" }).click(),
  ]);

  await expect(page.getByText(cid)).toBeVisible({ timeout: 10000 });

  await page.goto(`${gamma.url}/users`, { timeout: 30000 });
  await expect(page.getByText(cid)).toBeVisible({ timeout: 10000 });
});
