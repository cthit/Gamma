import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { waitForMail, waitForMailLink } from "../../helpers/mail";
import { articleByHeading } from "../../helpers/a11y";

async function requestEmailChange(
  page: import("@playwright/test").Page,
  newEmail: string,
  password: string,
): Promise<void> {
  await page.getByLabel("New email", { exact: true }).fill(newEmail);
  await page.getByLabel("Current password", { exact: true }).fill(password);
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/me/edit-email"),
    ),
    page.getByRole("button", { name: "Send confirmation link" }).click(),
  ]);
}

test("changing email needs the current password and only applies once the link mailed to the new address is confirmed", async ({
  page,
  env,
  gamma,
}) => {
  const oldEmail = "dschrute@example.org";
  const newEmail = "dwight.schrute@example.org";

  await login(page, gamma.url, "dschrute", "password1337", "Assistant Manager");

  await page.getByRole("button", { name: "Change email" }).click();
  await expect(page.getByText("Changing your email")).toBeVisible({
    timeout: 10000,
  });

  // Shorter than any valid password, so it can't even be checked.
  await requestEmailChange(page, newEmail, "wrong");
  await expect(page.getByText("Password is not correct")).toBeVisible();

  await requestEmailChange(page, newEmail, "wrong-password-1337");
  await expect(page.getByText("Password is not correct")).toBeVisible();

  await requestEmailChange(page, "jhalpert@example.org", "password1337");
  await expect(
    page.getByText("Another account already uses this email"),
  ).toBeVisible();

  await requestEmailChange(page, newEmail, "password1337");
  await expect(
    page.getByText(`We sent a confirmation link to ${newEmail}`),
  ).toBeVisible({ timeout: 10000 });
  // Nothing changes until the link is opened.
  await expect(articleByHeading(page, "Your information")).toContainText(
    oldEmail,
  );

  const link = await waitForMailLink(
    env.gotify,
    gamma.url,
    newEmail,
    "/me/confirm-email",
  );

  // Opening the link while signed out asks to sign in first and then continues.
  await logout(page);
  await page.goto(link);
  await page.getByLabel("Cid or email", { exact: true }).fill("dschrute");
  await page.getByLabel("Password", { exact: true }).fill("password1337");
  await page.getByRole("button", { name: "Login" }).click();

  await expect(page.getByText("Confirm your new email")).toBeVisible({
    timeout: 10000,
  });
  await expect(page.getByText(newEmail)).toBeVisible();
  await page.getByRole("button", { name: "Confirm new email" }).click();
  await expect(page.getByText("Your email has been changed")).toBeVisible({
    timeout: 10000,
  });

  const notification = await waitForMail(env.gotify, oldEmail, /was changed/);
  expect(notification).toContain(newEmail);

  // The link only works once.
  await page.goto(link);
  await expect(page.getByText("Could not change your email")).toBeVisible();

  await page.goto(gamma.url);
  await expect(articleByHeading(page, "Your information")).toContainText(
    newEmail,
  );

  await logout(page);
  await login(page, gamma.url, newEmail, "password1337", "Assistant Manager");
});
