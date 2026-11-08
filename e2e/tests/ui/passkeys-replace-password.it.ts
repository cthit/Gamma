import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import {
  addPasskey,
  signInWithPasskey,
  useAnotherVirtualAuthenticator,
} from "../../helpers/passkeys";

test("a user can add passkeys, sign in with them and remove their password", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  await page.goto(`${gamma.url}/me/security`);
  await expect(
    page.getByRole("heading", { name: "Sign in and security" }),
  ).toBeVisible();
  await expect(page.getByText("You have no passkeys yet.")).toBeVisible();

  await addPasskey(page, "Laptop");
  await expect(page.getByText("The passkey was added.")).toBeVisible();
  await useAnotherVirtualAuthenticator(page);
  await addPasskey(page, "Phone");
  await expect(page.getByText("The passkey was added.")).toBeVisible();
  const passkeys = page.getByRole("table", { name: "Your passkeys" });
  await expect(passkeys.getByRole("row")).toHaveCount(3);
  await expect(passkeys.getByRole("cell", { name: "Laptop" })).toBeVisible();
  await expect(passkeys.getByRole("cell", { name: "Phone" })).toBeVisible();

  await logout(page);
  await page.goto(`${gamma.url}/login`);
  await signInWithPasskey(page);
  await expect(page.getByText("Hey, Big Tuna!")).toBeVisible();

  await page.goto(`${gamma.url}/me/security`);
  page.once("dialog", (dialog) => dialog.accept());
  await page.getByRole("button", { name: "Remove password" }).click();
  await expect(page.getByText("Your password was removed.")).toBeVisible();

  // Without a password the home page offers no password change, and changing
  // the email needs no password since the user just signed in with a passkey.
  await page.goto(`${gamma.url}/`);
  await expect(
    page.getByRole("button", { name: "Change password" }),
  ).toHaveCount(0);
  await page.getByRole("button", { name: "Change email" }).click();
  await expect(page.getByLabel("New email", { exact: true })).toBeVisible();
  await expect(
    page.getByLabel("Current password", { exact: true }),
  ).toHaveCount(0);

  // A passkey can be removed, but not the last one of a user without a
  // password.
  await page.goto(`${gamma.url}/me/security`);
  page.once("dialog", (dialog) => dialog.accept());
  await passkeys
    .getByRole("row", { name: /Phone/ })
    .getByRole("button", { name: "Remove" })
    .click();
  await expect(page.getByText("The passkey was removed.")).toBeVisible();
  page.once("dialog", (dialog) => dialog.accept());
  await passkeys
    .getByRole("row", { name: /Laptop/ })
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
});
