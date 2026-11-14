import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { currentTimeStep, totpCode } from "../../helpers/totp";
import { saveRecoveryKey } from "../../helpers/recovery-key";
import { csrfTokenOf } from "../../helpers/a11y";

test("a session that has only given the password gets no passkey registration options or sign-in methods", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(`${gamma.url}/me/security`);
  await page
    .getByRole("button", { name: "Set up an authenticator app" })
    .click();
  const secret = (
    await page
      .getByLabel("Key to enter in the app", { exact: true })
      .innerText()
  ).replace(/\s/g, "");
  const setupStep = currentTimeStep();
  await page
    .getByLabel("Code from the app", { exact: true })
    .fill(totpCode(secret, setupStep));
  await page.getByRole("button", { name: "Turn on", exact: true }).click();
  await saveRecoveryKey(page);
  await expect(
    page.getByText("Your authenticator app is set up."),
  ).toBeVisible();
  await logout(page);

  await page.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
  await page.getByLabel("Password", { exact: true }).fill("password1337");
  await page.getByRole("button", { name: "Login", exact: true }).click();
  await expect(page).toHaveURL(/\/login\/totp/);
  const csrf = await csrfTokenOf(
    page.getByLabel("Six digit code", { exact: true }),
  );

  const options = await page.request.post(
    `${gamma.url}/webauthn/register/options`,
    { headers: { "X-CSRF-TOKEN": csrf }, maxRedirects: 0 },
  );
  expect(options.status()).toBe(302);
  expect(options.headers().location).toContain("/login/totp");
  expect(await options.text()).not.toContain("challenge");

  const confirm = await page.request.get(`${gamma.url}/login/confirm`, {
    maxRedirects: 0,
  });
  expect(confirm.status()).toBe(302);
  expect(confirm.headers().location).toContain("/login/totp");

  // Once the code is given, the same session gets registration options.
  await page
    .getByLabel("Six digit code", { exact: true })
    .fill(totpCode(secret, Math.max(setupStep, currentTimeStep()) + 1));
  await Promise.all([
    page.waitForURL((url) => url.pathname === "/", { timeout: 15000 }),
    page.getByRole("button", { name: "Continue" }).click(),
  ]);
  await page.goto(`${gamma.url}/me/security`);
  const token = await page
    .getByRole("button", { name: "Add a passkey", exact: true })
    .getAttribute("data-csrf-token");
  const signedIn = await page.request.post(
    `${gamma.url}/webauthn/register/options`,
    { headers: { "X-CSRF-TOKEN": token! }, maxRedirects: 0 },
  );
  expect(signedIn.status()).toBe(200);
  expect(await signedIn.text()).toContain("challenge");
});
