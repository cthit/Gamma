import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { waitForMailLink } from "../../helpers/mail";
import { login, logout } from "../../helpers/auth";
import { uniqueCid, uniqueEmail } from "../../helpers/strings";
import { rowWithText } from "../../helpers/a11y";

test("given an allowed cid when completing registration then the new user can sign in", async ({
  page,
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
    // The form updates this same URL; wait for persistence before logging out.
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

  await page.goto(link, { timeout: 30000 });
  await expect(page.getByText("Finish setting up your account")).toBeVisible({
    timeout: 10000,
  });

  const password = `${uniqueCid("register")}pw`;
  const nick = uniqueCid("nick");

  await page
    .getByLabel("Non student email", { exact: true })
    .fill(uniqueEmail("newuser"));
  await page.getByLabel("Nick", { exact: true }).fill(nick);
  await page.getByLabel("First name", { exact: true }).fill("Register");
  await page.getByLabel("Last name", { exact: true }).fill("Tester");
  await page.getByRole("radio", { name: /With a password/ }).check();
  await page.getByLabel("Password", { exact: true }).fill(password);
  await page.getByLabel("Confirm password", { exact: true }).fill(password);
  await page
    .getByRole("combobox", {
      name: "Which year did the user start at IT?",
      exact: true,
    })
    .selectOption("2020");
  await page
    .getByRole("combobox", {
      name: "What language does the user prefer?",
      exact: true,
    })
    .selectOption("EN");
  await page.getByLabel("I accept the user agreement", { exact: true }).check();

  await Promise.all([
    page.waitForURL("**/login?account-created", { timeout: 15000 }),
    page.getByRole("button", { name: "Create account" }).click(),
  ]);

  await expect(page.getByText("Your account has been created.")).toBeVisible({
    timeout: 10000,
  });

  await login(page, gamma.url, cid, password, nick);
});
