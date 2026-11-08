import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { finishAdminSignIn } from "../../helpers/passkeys";

test("login in with admin account", async ({ page, gamma }) => {
  if (!gamma.adminCid || !gamma.adminPassword) {
    throw new Error(
      "Admin credentials were not captured from Gamma startup logs",
    );
  }

  await page.goto(gamma.url, { timeout: 60000 });
  await expect(page.getByRole("heading", { name: "Gamma" })).toBeVisible({
    timeout: 60000,
  });

  await page.getByLabel("Cid or email", { exact: true }).fill(gamma.adminCid);
  await page.getByLabel("Password", { exact: true }).fill(gamma.adminPassword);

  await Promise.all([
    page.waitForURL((url) => url.pathname !== "/login", { timeout: 15000 }),
    page.getByRole("button", { name: "Login" }).click(),
  ]);

  // The bootstrapped admin has no passkey yet, so they add one first.
  await expect(
    page.getByRole("heading", { name: "Add a passkey to continue" }),
  ).toBeVisible();
  await finishAdminSignIn(page);

  await expect(page.getByText("Hey, admin!")).toBeVisible({ timeout: 10000 });
});
