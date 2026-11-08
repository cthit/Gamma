import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { createUserClientViaUi } from "../../helpers/oauth";
import { finishAdminSignIn, signInWithPasskey } from "../../helpers/passkeys";
import { waitForMailLink } from "../../helpers/mail";

async function signInWithPassword(
  page: import("@playwright/test").Page,
  url: string,
  cid: string,
  password: string,
) {
  await page.goto(`${url}/login`);
  await page.getByLabel("Cid or email", { exact: true }).fill(cid);
  await page.getByLabel("Password", { exact: true }).fill(password);
  await Promise.all([
    page.waitForURL((u) => u.pathname !== "/login", { timeout: 15000 }),
    page.getByRole("button", { name: "Login" }).click(),
  ]);
}

test("an admin can't use Gamma or sign in to clients until they have signed in with a passkey", async ({
  page,
  gamma,
  env,
}) => {
  const admin = gamma.adminCid!;
  const adminPassword = gamma.adminPassword!;

  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const client = await createUserClientViaUi(page, gamma.url, "Admin gate");
  await logout(page);

  // The bootstrapped admin has no passkey, so after the password only adding
  // a passkey is possible.
  await signInWithPassword(page, gamma.url, admin, adminPassword);
  await expect(page).toHaveURL(/\/login\/confirm/);
  await expect(
    page.getByRole("heading", { name: "Add a passkey to continue" }),
  ).toBeVisible();

  await page.goto(`${gamma.url}/users`);
  await expect(page).toHaveURL(/\/login\/confirm/);

  // Not even signing in to another client works.
  const authorizeUrl = new URL("/oauth2/authorize", gamma.url);
  authorizeUrl.search = new URLSearchParams({
    response_type: "code",
    client_id: client.clientId,
    redirect_uri: client.redirectUri,
    scope: "openid profile email",
    state: "admin-gate",
  }).toString();
  await page.goto(authorizeUrl.toString());
  await expect(page).toHaveURL(/\/login\/confirm/);

  // Once the admin has a passkey and signed in with it, they continue to the
  // client they were signing in to.
  await finishAdminSignIn(page);
  const authorizeButton = page.getByRole("button", { name: "Authorize" });
  await expect(authorizeButton).toBeVisible();
  await Promise.all([
    page.waitForURL("**/oauth2/callback**", { timeout: 15000 }),
    authorizeButton.click(),
  ]);
  expect(new URL(page.url()).searchParams.get("code")).not.toBeNull();

  await page.goto(`${gamma.url}/users`);
  await expect(page.getByText("Hey, admin!")).toBeVisible();
  await logout(page);

  // With a passkey, the password alone no longer gets the admin anywhere.
  await signInWithPassword(page, gamma.url, admin, adminPassword);
  await expect(
    page.getByRole("heading", { name: "Admins sign in with a passkey" }),
  ).toBeVisible();
  await page.goto(`${gamma.url}/`);
  await expect(page).toHaveURL(/\/login\/confirm/);
  await signInWithPasskey(page);
  await expect(page.getByText("Hey, admin!")).toBeVisible();
  await logout(page);

  // The passkey works straight from the login page too.
  await page.goto(`${gamma.url}/login`);
  await signInWithPasskey(page);
  await expect(page.getByText("Hey, admin!")).toBeVisible();
  await logout(page);

  // Admins can't reset how they sign in by email.
  await page.goto(`${gamma.url}/forgot-password`);
  await page.getByLabel("Cid or Email", { exact: true }).fill(admin);
  await page.getByRole("button", { name: /reset|send/i }).click();
  await expect(
    page.getByText("You should have received an email"),
  ).toBeVisible();
  await expect(
    waitForMailLink(
      env.gotify,
      gamma.url,
      `${admin}@chalmers.it`,
      "/forgot-password/finalize",
      5000,
    ),
  ).rejects.toThrow();
});
