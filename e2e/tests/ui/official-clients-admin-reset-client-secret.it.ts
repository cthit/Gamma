import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueLabel } from "../../helpers/strings";
import { articleByHeading } from "../../helpers/a11y";

test("given an official client when an admin resets its secret then a new secret is shown", async ({
  page,
  gamma,
}) => {
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const prettyName = uniqueLabel("E2E Official Client");

  await page.goto(`${gamma.url}/clients/create`, { timeout: 30000 });
  await page.getByLabel("Pretty name", { exact: true }).fill(prettyName);
  await page
    .getByLabel("Swedish description", { exact: true })
    .fill("E2E svensk beskrivning");
  await page
    .getByLabel("English description", { exact: true })
    .fill("E2E english description");
  await page
    .getByLabel("Redirect url", { exact: true })
    .fill("https://example.org/callback");

  await Promise.all([
    page.waitForURL("**/clients/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  const credentialsArticle = articleByHeading(page, "Credentials");
  await expect(credentialsArticle).toBeVisible({ timeout: 10000 });

  const oldSecret = (
    await credentialsArticle.getByRole("code").first().innerText()
  ).trim();
  expect(oldSecret.length).toBeGreaterThan(0);

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/clients/") &&
        response.url().includes("/reset") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.getByRole("button", { name: "Reset client secret" }).click(),
  ]);

  const newCredentialsArticle = articleByHeading(page, "Credentials");
  await expect(newCredentialsArticle).toBeVisible({ timeout: 10000 });
  await expect(newCredentialsArticle.getByRole("code").first()).not.toHaveText(
    oldSecret,
  );

  const newSecret = (
    await newCredentialsArticle.getByRole("code").first().innerText()
  ).trim();
  expect(newSecret.length).toBeGreaterThan(0);
  expect(newSecret).not.toBe(oldSecret);
});
