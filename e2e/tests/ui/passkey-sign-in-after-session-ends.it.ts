import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { addPasskey, signInWithPasskey } from "../../helpers/passkeys";

test("the passkey button works again after the session behind an open sign-in page ends", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(`${gamma.url}/me/security`);
  await addPasskey(page, "Phone");
  await expect(page).toHaveURL(/done=passkey-added/, { timeout: 15000 });
  await logout(page);

  await page.goto(`${gamma.url}/login`);
  await page.context().clearCookies();

  const reloaded = page.waitForEvent("load");
  await page.getByRole("button", { name: "Sign in with a passkey" }).click();
  await reloaded;
  await expect(page).toHaveURL(/\/login$/);

  await signInWithPasskey(page);
  await expect(page.getByText("Hey, Big Tuna!")).toBeVisible({
    timeout: 10000,
  });
});
