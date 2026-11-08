import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { articleByHeading, rowWithText } from "../../helpers/a11y";

const lockedMessage =
  "Invalid credentials or locked account due to system migration. Password reset may be needed.";

test("given an admin when locking a user then the user is signed out and cannot sign in until unlocked", async ({
  page,
  browser,
  gamma,
}) => {
  const userContext = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await userContext.newPage();
    await login(user, gamma.url, "jhalpert", "password1337", "Big Tuna");

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

    const userDetails = articleByHeading(page, "User details");
    await expect(userDetails).toBeVisible({ timeout: 10000 });
    await expect(
      userDetails.getByRole("button", { name: "Unlock user", exact: true }),
    ).toHaveCount(0);

    page.once("dialog", (dialog) => dialog.accept());
    await userDetails
      .getByRole("button", { name: "Lock user", exact: true })
      .click();

    await expect(page.getByText("User locked")).toBeVisible({
      timeout: 10000,
    });
    await expect(
      userDetails.getByRole("button", { name: "Unlock user", exact: true }),
    ).toBeVisible();
    await expect(
      userDetails.getByRole("button", { name: "Lock user", exact: true }),
    ).toHaveCount(0);

    // The session the user already had has ended.
    await user.goto(`${gamma.url}/users`, { timeout: 30000 });
    await expect(user).toHaveURL(/\/login/);
    await expect(user.getByLabel("Cid or email", { exact: true })).toBeVisible({
      timeout: 10000,
    });

    // And the user can't sign in again.
    await user.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
    await user.getByLabel("Password", { exact: true }).fill("password1337");
    await Promise.all([
      user.waitForURL("**/login?error**", { timeout: 15000 }),
      user.getByRole("button", { name: "Login" }).click(),
    ]);
    await expect(user.getByText(lockedMessage)).toBeVisible({
      timeout: 10000,
    });

    // Admins still see locked users.
    await page.goto(`${gamma.url}/users`, { timeout: 30000 });
    await rowWithText(page, "jhalpert")
      .getByRole("link", { name: "Details" })
      .click();
    await articleByHeading(page, "User details")
      .getByRole("button", { name: "Unlock user", exact: true })
      .click();

    await expect(page.getByText("User unlocked")).toBeVisible({
      timeout: 10000,
    });
    await expect(
      articleByHeading(page, "User details").getByRole("button", {
        name: "Lock user",
        exact: true,
      }),
    ).toBeVisible();

    await login(user, gamma.url, "jhalpert", "password1337", "Big Tuna");
  } finally {
    await userContext.close();
  }
});

test("given an admin when viewing their own user then they cannot lock themselves", async ({
  page,
  gamma,
}) => {
  const adminCid = gamma.adminCid ?? "";
  await login(page, gamma.url, adminCid, gamma.adminPassword ?? "", "admin");

  await page.goto(`${gamma.url}/users`, { timeout: 30000 });
  await rowWithText(page, adminCid)
    .getByRole("link", { name: "Details" })
    .click();

  const userDetails = articleByHeading(page, "User details");
  await expect(
    userDetails.getByRole("button", { name: "Edit user" }),
  ).toBeVisible({ timeout: 10000 });
  await expect(
    userDetails.getByRole("button", { name: "Lock user", exact: true }),
  ).toHaveCount(0);
});

test("given a signed in user when an admin locks and unlocks them before their next request then that session is still signed out", async ({
  page,
  browser,
  gamma,
}) => {
  const userContext = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await userContext.newPage();
    await login(user, gamma.url, "pbeesly", "password1337", "Pam-Pam");

    await login(
      page,
      gamma.url,
      gamma.adminCid ?? "",
      gamma.adminPassword ?? "",
      "admin",
    );
    await page.goto(`${gamma.url}/users`, { timeout: 30000 });
    await rowWithText(page, "pbeesly")
      .getByRole("link", { name: "Details" })
      .click();

    const userDetails = articleByHeading(page, "User details");
    page.once("dialog", (dialog) => dialog.accept());
    await userDetails
      .getByRole("button", { name: "Lock user", exact: true })
      .click();
    await expect(page.getByText("User locked")).toBeVisible({
      timeout: 10000,
    });
    await userDetails
      .getByRole("button", { name: "Unlock user", exact: true })
      .click();
    await expect(page.getByText("User unlocked")).toBeVisible({
      timeout: 10000,
    });

    // The session made no request while the user was locked.
    await user.goto(`${gamma.url}/users`, { timeout: 30000 });
    await expect(user).toHaveURL(/\/login/);
    await expect(user.getByLabel("Cid or email", { exact: true })).toBeVisible({
      timeout: 10000,
    });

    await login(user, gamma.url, "pbeesly", "password1337", "Pam-Pam");
  } finally {
    await userContext.close();
  }
});
