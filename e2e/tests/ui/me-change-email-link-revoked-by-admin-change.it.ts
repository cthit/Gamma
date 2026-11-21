import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { waitForMailLink } from "../../helpers/mail";
import { articleByHeading, rowWithText } from "../../helpers/a11y";

test("a pending email change can't be confirmed once an admin has changed the email", async ({
  page,
  env,
  gamma,
}) => {
  const requestedEmail = "dwight.schrute@example.org";
  const adminEmail = "dwight.k.schrute@example.org";

  await login(page, gamma.url, "dschrute", "password1337", "Assistant Manager");
  await page.getByRole("button", { name: "Change email" }).click();
  await page.getByLabel("New email", { exact: true }).fill(requestedEmail);
  await page
    .getByLabel("Current password", { exact: true })
    .fill("password1337");
  await page.getByRole("button", { name: "Send confirmation link" }).click();
  await expect(
    page.getByText(`We sent a confirmation link to ${requestedEmail}`),
  ).toBeVisible({ timeout: 10000 });
  const link = await waitForMailLink(
    env.gotify,
    gamma.url,
    requestedEmail,
    "/me/confirm-email",
  );
  await logout(page);

  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );
  await page.goto(`${gamma.url}/users`, { timeout: 30000 });
  await rowWithText(page, "dschrute")
    .getByRole("link", { name: "Details" })
    .click();
  await page.getByRole("button", { name: "Edit user" }).click();
  await page.getByLabel("Email", { exact: true }).fill(adminEmail);
  await page.getByRole("button", { name: "Save" }).click();
  await expect(page.getByText("User updated")).toBeVisible({
    timeout: 10000,
  });
  await logout(page);

  await login(page, gamma.url, "dschrute", "password1337", "Assistant Manager");
  await page.goto(link);
  await expect(page.getByText("Could not change your email")).toBeVisible();
  await expect(
    page.getByRole("button", { name: "Confirm new email" }),
  ).toHaveCount(0);

  await page.goto(gamma.url);
  await expect(articleByHeading(page, "Your information")).toContainText(
    adminEmail,
  );
});
