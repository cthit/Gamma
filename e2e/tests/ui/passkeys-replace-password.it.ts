import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import {
  addPasskey,
  signInWithPasskey,
  useAnotherVirtualAuthenticator,
} from "../../helpers/passkeys";
import { currentTimeStep, totpCode } from "../../helpers/totp";
import { saveRecoveryKey } from "../../helpers/recovery-key";

test("adding a passkey replaces the password and authenticator app", async ({
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
  await page
    .getByLabel("Code from the app", { exact: true })
    .fill(totpCode(secret, currentTimeStep()));
  await page.getByRole("button", { name: "Turn on" }).click();
  await saveRecoveryKey(page);
  await expect(
    page.getByText("Your authenticator app is set up."),
  ).toBeVisible();

  await expect(
    page.getByText(
      "Adding a passkey removes your password and authenticator app.",
    ),
  ).toBeVisible();
  page.once("dialog", (dialog) => dialog.accept());
  await addPasskey(page, "Laptop");
  await expect(
    page.getByText("The passkey was added. You sign in with passkeys only."),
  ).toBeVisible();
  await expect(
    page.getByText("You sign in with passkeys only.", { exact: true }),
  ).toBeVisible();
  await expect(page.getByRole("heading", { name: "Password" })).toHaveCount(0);
  await expect(
    page.getByRole("heading", { name: "Authenticator app" }),
  ).toHaveCount(0);

  await useAnotherVirtualAuthenticator(page);
  await addPasskey(page, "Phone");
  await expect(
    page.getByText("The passkey was added. You sign in with passkeys only."),
  ).toBeVisible();
  const passkeys = page.getByRole("table", { name: "Your passkeys" });
  await expect(passkeys.getByRole("row")).toHaveCount(3);

  await page.goto(`${gamma.url}/`);
  await expect(
    page.getByRole("button", { name: "Change password" }),
  ).toHaveCount(0);
  await page.getByRole("button", { name: "Change email" }).click();
  await expect(page.getByLabel("New email", { exact: true })).toBeVisible();
  await expect(
    page.getByLabel("Current password", { exact: true }),
  ).toHaveCount(0);

  await page.goto(`${gamma.url}/me/security`);
  page.once("dialog", (dialog) => dialog.accept());
  await passkeys
    .getByRole("row", { name: /Laptop/ })
    .getByRole("button", { name: "Remove" })
    .click();
  await expect(page.getByText("The passkey was removed.")).toBeVisible();
  page.once("dialog", (dialog) => dialog.accept());
  await passkeys
    .getByRole("row", { name: /Phone/ })
    .getByRole("button", { name: "Remove" })
    .click();
  await expect(
    page.getByText("You can't remove your only passkey"),
  ).toBeVisible();

  await logout(page);
  await page.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
  await page.getByLabel("Password", { exact: true }).fill("password1337");
  await page.getByRole("button", { name: "Login" }).click();
  await expect(page).toHaveURL(/\/login\?error/);

  await signInWithPasskey(page);
  await expect(page.getByText("Hey, Big Tuna!")).toBeVisible();
});
