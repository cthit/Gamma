import type { Page } from "@playwright/test";
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { waitForMailLink } from "../../helpers/mail";
import { rowWithText } from "../../helpers/a11y";

async function requestPasswordReset(
  page: Page,
  baseUrl: string,
  cidOrEmail: string,
): Promise<void> {
  await page.goto(`${baseUrl}/forgot-password`);
  await page.getByLabel("Cid or Email", { exact: true }).fill(cidOrEmail);
  await page.getByRole("button", { name: /reset|send/i }).click();
}

async function expectResetLinkRejected(page: Page, link: string) {
  await page.goto(link);
  await expect(page.getByLabel("Password", { exact: true })).toHaveCount(0);
  await expect(
    page.getByText(/invalid|expired|already|not valid/i),
  ).toBeVisible();
}

test("a reset link mailed to the old email stays revoked after an admin changes the email and the user verifies the new one", async ({
  page,
  env,
  gamma,
}) => {
  const oldEmail = "jhalpert@example.org";
  const newEmail = "jim.halpert@example.org";

  await requestPasswordReset(page, gamma.url, oldEmail);
  const link = await waitForMailLink(
    env.gotify,
    gamma.url,
    oldEmail,
    "/forgot-password/finalize",
  );

  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );
  await page.goto(`${gamma.url}/users`, { timeout: 30000 });
  await rowWithText(page, "jhalpert")
    .getByRole("link", { name: "Details" })
    .click();
  await page.getByRole("button", { name: "Edit user" }).click();
  await page.getByLabel("Email", { exact: true }).fill(newEmail);
  await page.getByRole("button", { name: "Save" }).click();
  await expect(page.getByText("User updated")).toBeVisible({
    timeout: 10000,
  });
  await logout(page);

  await expectResetLinkRejected(page, link);

  // Verifying the new address turns email recovery back on, but not for the old link.
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.getByRole("button", { name: "Change email" }).click();
  await page.getByLabel("New email", { exact: true }).fill(newEmail);
  await page
    .getByLabel("Current password", { exact: true })
    .fill("password1337");
  await page.getByRole("button", { name: "Send confirmation link" }).click();
  await expect(
    page.getByText(`We sent a confirmation link to ${newEmail}`),
  ).toBeVisible({ timeout: 10000 });
  await page.goto(
    await waitForMailLink(env.gotify, gamma.url, newEmail, "/me/confirm-email"),
  );
  await page.getByRole("button", { name: "Confirm new email" }).click();
  await expect(page.getByText("Your email has been changed")).toBeVisible({
    timeout: 10000,
  });
  await logout(page);

  await expectResetLinkRejected(page, link);

  // The verified address can recover the account.
  await requestPasswordReset(page, gamma.url, newEmail);
  const newLink = await waitForMailLink(
    env.gotify,
    gamma.url,
    newEmail,
    "/forgot-password/finalize",
  );
  await page.goto(newLink);
  await expect(page.getByLabel("Password", { exact: true })).toBeVisible();
});

test("a reset link mailed to the old email stops working once the user confirms a new email", async ({
  page,
  env,
  gamma,
}) => {
  const oldEmail = "dschrute@example.org";
  const newEmail = "dwight.schrute@example.org";

  await requestPasswordReset(page, gamma.url, oldEmail);
  const link = await waitForMailLink(
    env.gotify,
    gamma.url,
    oldEmail,
    "/forgot-password/finalize",
  );

  await login(page, gamma.url, "dschrute", "password1337", "Assistant Manager");
  await page.getByRole("button", { name: "Change email" }).click();
  await page.getByLabel("New email", { exact: true }).fill(newEmail);
  await page
    .getByLabel("Current password", { exact: true })
    .fill("password1337");
  await page.getByRole("button", { name: "Send confirmation link" }).click();
  await expect(
    page.getByText(`We sent a confirmation link to ${newEmail}`),
  ).toBeVisible({ timeout: 10000 });

  await page.goto(
    await waitForMailLink(env.gotify, gamma.url, newEmail, "/me/confirm-email"),
  );
  await page.getByRole("button", { name: "Confirm new email" }).click();
  await expect(page.getByText("Your email has been changed")).toBeVisible({
    timeout: 10000,
  });
  await logout(page);

  await expectResetLinkRejected(page, link);
});
