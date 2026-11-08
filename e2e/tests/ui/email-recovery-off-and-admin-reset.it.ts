import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { saveRecoveryKey } from "../../helpers/recovery-key";
import { waitForMailLink } from "../../helpers/mail";
import { addPasskey, signInWithPasskey } from "../../helpers/passkeys";
import { articleByHeading } from "../../helpers/a11y";

const AMARTIN_ID = "858e5acc-c289-40d3-9422-d6d317f40299";
const AMARTIN_EMAIL = "amartin@example.org";

async function requestPasswordReset(
  page: import("@playwright/test").Page,
  url: string,
) {
  await page.goto(`${url}/forgot-password`);
  await page.getByLabel("Cid or Email", { exact: true }).fill(AMARTIN_EMAIL);
  await page.getByRole("button", { name: /reset|send/i }).click();
  await expect(
    page.getByText("You should have received an email"),
  ).toBeVisible();
}

test("a user who turns off email recovery gets no reset link, until an admin resets how they sign in", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  await login(page, gamma.url, "amartin", "password1337", "Pumpkin");
  await page.goto(`${gamma.url}/me/security`);
  await page.getByRole("button", { name: "Turn off email recovery" }).click();
  await saveRecoveryKey(page);
  await expect(page.getByText(/Email recovery is off\. Keep/)).toBeVisible();
  await logout(page);

  await requestPasswordReset(page, gamma.url);
  await expect(
    waitForMailLink(
      env.gotify,
      gamma.url,
      AMARTIN_EMAIL,
      "/forgot-password/finalize",
      5000,
    ),
  ).rejects.toThrow();

  // An admin can't hand out a reset link either, only reset how they sign in.
  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!, "admin");
  await page.goto(`${gamma.url}/users/${AMARTIN_ID}`);
  const resetLink = articleByHeading(page, "Generate reset password link");
  await resetLink.getByRole("button", { name: "Generate" }).click();
  await expect(
    resetLink.getByText("This user can't reset their password by email"),
  ).toBeVisible();

  const signIn = articleByHeading(page, "How the user signs in");
  await expect(signIn.getByText("Recovery key", { exact: true })).toBeVisible();
  page.once("dialog", (dialog) => dialog.accept());
  await signIn
    .getByRole("button", { name: "Reset how the user signs in" })
    .click();
  await expect(
    page.getByText("email recovery is on", { exact: false }),
  ).toBeVisible();
  await logout(page);

  // Now the reset link arrives, and offers setting up a passkey.
  await requestPasswordReset(page, gamma.url);
  const link = await waitForMailLink(
    env.gotify,
    gamma.url,
    AMARTIN_EMAIL,
    "/forgot-password/finalize",
  );

  // A new browser, so the admin's passkey isn't in the authenticator.
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  const userPage = await context.newPage();
  try {
    await userPage.goto(link);
    await userPage.getByRole("button", { name: "Set up a passkey" }).click();
    await expect(userPage).toHaveURL(/\/sign-in-setup/);
    await addPasskey(userPage, "Phone");
    await userPage
      .getByRole("button", { name: "Continue to signing in" })
      .click();
    await expect(userPage).toHaveURL(/\/login\?sign-in-ready/);
    await signInWithPasskey(userPage);
    await expect(userPage.getByText("Hey, Pumpkin!")).toBeVisible();
  } finally {
    await context.close();
  }
});
