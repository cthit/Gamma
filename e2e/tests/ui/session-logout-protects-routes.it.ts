import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";

test("given a signed in session when logging out then protected routes require login", async ({
  page,
  gamma,
  request,
}) => {
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const cookies = await page.context().cookies();
  expect(cookies.length).toBeGreaterThan(0);
  const cookieHeader = {
    Cookie: cookies
      .map((cookie) => `${cookie.name}=${cookie.value}`)
      .join("; "),
  };
  const before = await request.get(`${gamma.url}/users`, {
    headers: cookieHeader,
    maxRedirects: 0,
  });
  expect(before.status()).toBe(200);

  await logout(page);

  const after = await request.get(`${gamma.url}/users`, {
    headers: cookieHeader,
    maxRedirects: 0,
  });
  expect(after.status()).toBe(302);
  expect(after.headers().location).toContain("/login");

  await page.goto(`${gamma.url}/users`, { timeout: 30000 });

  await expect(page.locator('input[name="username"]')).toBeVisible({
    timeout: 10000,
  });
  await expect(page).toHaveURL(/\/login/);
});
