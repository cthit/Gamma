import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";

test("given a signed in session when logging out then protected routes require login", async ({
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

  await logout(page);

  await page.goto(`${gamma.url}/users`, { timeout: 30000 });

  await expect(page.locator('input[name="username"]')).toBeVisible({
    timeout: 10000,
  });
  await expect(page).toHaveURL(/\/login/);
});
