import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { waitForMailLink } from "../../helpers/mail";
import { login, logout } from "../../helpers/auth";
import { uniqueCid, uniqueEmail } from "../../helpers/strings";
import { rowWithText } from "../../helpers/a11y";
import { addPasskey, signInWithPasskey } from "../../helpers/passkeys";

test("a new user who chooses a passkey sets it up right after creating their account", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const cid = uniqueCid("reg").slice(0, 10);

  await page.goto(`${gamma.url}/allow-list`, { timeout: 30000 });
  await page.getByLabel("Cid", { exact: true }).fill(cid);

  await Promise.all([
    expect(rowWithText(page, cid)).toBeVisible({
      timeout: 15000,
    }),
    page.getByRole("button", { name: "Active", exact: true }).click(),
  ]);

  await logout(page);

  await page.goto(`${gamma.url}/activate-cid`, { timeout: 30000 });
  await page.getByLabel("Cid", { exact: true }).fill(cid);

  await Promise.all([
    page.waitForURL("**/email-sent", { timeout: 20000 }),
    page.getByRole("button", { name: "Activate cid" }).click(),
  ]);

  await expect(
    page.getByText("An email should be sent to your student email"),
  ).toBeVisible({ timeout: 10000 });

  const link = await waitForMailLink(
    env.gotify,
    gamma.url,
    `${cid}@chalmers.se`,
    "/register",
  );

  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  const userPage = await context.newPage();
  try {
    await userPage.goto(link, { timeout: 30000 });
    await expect(
      userPage.getByText("Finish setting up your account"),
    ).toBeVisible({ timeout: 10000 });

    const nick = uniqueCid("nick");

    await userPage
      .getByLabel("Non student email", { exact: true })
      .fill(uniqueEmail("newuser"));
    await userPage.getByLabel("Nick", { exact: true }).fill(nick);
    await userPage.getByLabel("First name", { exact: true }).fill("Register");
    await userPage.getByLabel("Last name", { exact: true }).fill("Tester");
    await expect(
      userPage.getByRole("radio", { name: /With a passkey \(recommended\)/ }),
    ).toBeChecked();
    await expect(userPage.getByLabel("Password", { exact: true })).toBeHidden();
    await userPage
      .getByLabel("I accept the user agreement", { exact: true })
      .check();

    await Promise.all([
      userPage.waitForURL("**/sign-in-setup", { timeout: 15000 }),
      userPage.getByRole("button", { name: "Create account" }).click(),
    ]);
    await expect(
      userPage.getByText(
        "Your account has been created. Now choose how you sign in.",
      ),
    ).toBeVisible();

    await addPasskey(userPage, "Laptop");
    await userPage
      .getByRole("button", { name: "Continue to signing in" })
      .click();
    await expect(userPage).toHaveURL(/\/login\?sign-in-ready/);

    await signInWithPasskey(userPage);
    await expect(userPage.getByText(`Hey, ${nick}!`)).toBeVisible();
  } finally {
    await context.close();
  }
});
