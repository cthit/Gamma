import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { waitForMailLink } from "../../helpers/mail";

test("public password recovery follows the email, replaces the password, and rejects link replay", async ({
  page,
  env,
  gamma,
}) => {
  await page.goto(`${gamma.url}/forgot-password`);
  await page.locator('input:not([type="hidden"])').fill("mscott@example.org");
  await page.getByRole("button", { name: /reset|send/i }).click();
  const link = await waitForMailLink(
    env.gotify,
    gamma.url,
    "mscott@example.org",
    "/forgot-password/finalize",
  );
  await page.goto(link);
  await page.locator('input[name="password"]').fill("New-password-1337");
  await page.locator('input[name="confirmPassword"]').fill("New-password-1337");
  await page.getByRole("button", { name: "Reset password" }).click();
  await expect(page).toHaveURL(/\/login\?password-reset/);
  await page.fill('input[name="username"]', "mscott");
  await page.fill('input[name="password"]', "password1337");
  await page.getByRole("button", { name: "Login" }).click();
  await expect(page).toHaveURL(/\/login\?error/);
  await login(page, gamma.url, "mscott", "New-password-1337", "Boss");
  await logout(page);
  await page.goto(link);
  await expect(page.locator('input[name="password"]')).toHaveCount(0);
  await expect(
    page.getByText(/invalid|expired|already|not valid/i),
  ).toBeVisible();
  // A direct submission must not bypass the invalid-link screen.
  await page.goto(`${gamma.url}/forgot-password`);
  const csrf = await page.locator('input[name="_csrf"]').inputValue();
  await page.request.post(link, {
    form: {
      _csrf: csrf,
      password: "Replay-password-1337",
      confirmPassword: "Replay-password-1337",
      token: new URL(link).searchParams.get("token")!,
    },
    maxRedirects: 0,
  });
  await login(page, gamma.url, "mscott", "New-password-1337", "Boss");
});
