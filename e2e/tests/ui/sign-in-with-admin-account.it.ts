import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";

test("login in with admin account", async ({ page, gamma }) => {
  if (!gamma.adminCid || !gamma.adminPassword) {
    throw new Error(
      "Admin credentials were not captured from Gamma startup logs",
    );
  }

  await page.goto(gamma.url, { timeout: 60000 });
  await expect(page.getByText("Gamma")).toBeVisible({ timeout: 60000 });

  await page.fill('input[name="username"]', gamma.adminCid);
  await page.fill('input[name="password"]', gamma.adminPassword);

  await Promise.all([
    page.waitForURL((url) => url.pathname !== "/login", { timeout: 15000 }),
    page.click('button:has-text("Login")'),
  ]);

  await expect(page.getByText("Hey, admin!")).toBeVisible({ timeout: 10000 });
});
