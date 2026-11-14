import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { waitForMail, waitForMailLink } from "../../helpers/mail";
import { addPasskey, signInWithPasskey } from "../../helpers/passkeys";
import { articleByHeading, rowWithText } from "../../helpers/a11y";
import type { Page } from "@playwright/test";

// The value of the last entry, "Email recovery", in the user's sign-in methods.
function emailRecoveryValue(page: Page) {
  return articleByHeading(page, "How the user signs in")
    .getByRole("definition")
    .last();
}

async function requestPasswordReset(page: Page, gammaUrl: string) {
  await page.goto(`${gammaUrl}/forgot-password`);
  await page.getByLabel("Cid or Email", { exact: true }).fill("jhalpert");
  await page.getByRole("button", { name: /reset|send/i }).click();
}

test("an admin changing a user's email turns email recovery off until the user verifies the address", async ({
  page,
  browser,
  env,
  gamma,
}) => {
  const previousEmail = "jhalpert@example.org";
  const newEmail = "big.tuna.new@example.com";

  const userContext = await browser.newContext({ ignoreHTTPSErrors: true });
  const anonymousContext = await browser.newContext({
    ignoreHTTPSErrors: true,
  });
  try {
    const user = await userContext.newPage();
    await login(user, gamma.url, "jhalpert", "password1337", "Big Tuna");
    await user.goto(`${gamma.url}/me/security`);
    user.once("dialog", (dialog) => dialog.accept());
    await addPasskey(user, "Tuna phone");
    await expect(user.getByRole("row", { name: /Tuna phone/ })).toBeVisible();

    await login(
      page,
      gamma.url,
      gamma.adminCid!,
      gamma.adminPassword!,
      "admin",
    );
    await page.goto(`${gamma.url}/users`);
    await rowWithText(page, "jhalpert")
      .getByRole("link", { name: "Details" })
      .click();
    await expect(emailRecoveryValue(page)).toHaveText("Yes");
    await expect(page).toHaveURL(/\/users\/[0-9a-f-]+$/);
    const detailsUrl = page.url();

    await page.getByRole("button", { name: "Edit user" }).click();
    await expect(
      page.getByText(/Changing the email turns email recovery off/),
    ).toBeVisible();
    await page.getByLabel("Email", { exact: true }).fill(newEmail);
    await page.getByRole("button", { name: "Save" }).click();
    await expect(page.getByText("User updated")).toBeVisible();

    await page.goto(detailsUrl);
    await expect(emailRecoveryValue(page)).toHaveText("No");

    // Both the previous and the new address are told, without any link.
    for (const address of [previousEmail, newEmail]) {
      const notification = await waitForMail(
        env.gotify,
        address,
        /email of your account .* was changed/,
      );
      expect(notification).toContain("An admin changed the email address");
      expect(notification).toContain("jhalpert");
      expect(notification).not.toMatch(/token=|https?:\/\//);
    }

    // The new address can't be used to take over the account.
    const anonymous = await anonymousContext.newPage();
    await requestPasswordReset(anonymous, gamma.url);
    for (const address of [newEmail, previousEmail]) {
      await expect(
        waitForMailLink(
          env.gotify,
          gamma.url,
          address,
          "/forgot-password/finalize",
          3000,
        ),
      ).rejects.toThrow();
    }

    // The user's passkey still works, and verifying the address restores email recovery.
    await logout(user);
    await signInWithPasskey(user);
    await expect(user.getByText("Hey, Big Tuna!")).toBeVisible();
    await expect(
      user.getByRole("heading", { name: "Check your email address" }),
    ).toBeVisible();
    await user.getByRole("button", { name: "Change email" }).click();
    await user.getByLabel("New email", { exact: true }).fill(newEmail);
    await user.getByRole("button", { name: "Send confirmation link" }).click();
    await expect(
      user.getByText(`We sent a confirmation link to ${newEmail}`),
    ).toBeVisible();
    await user.goto(
      await waitForMailLink(
        env.gotify,
        gamma.url,
        newEmail,
        "/me/confirm-email",
      ),
    );
    await user.getByRole("button", { name: "Confirm new email" }).click();
    await expect(user.getByText("Your email has been changed")).toBeVisible();

    await requestPasswordReset(anonymous, gamma.url);
    expect(
      await waitForMailLink(
        env.gotify,
        gamma.url,
        newEmail,
        "/forgot-password/finalize",
      ),
    ).toContain("token=");
  } finally {
    await userContext.close();
    await anonymousContext.close();
  }
});
