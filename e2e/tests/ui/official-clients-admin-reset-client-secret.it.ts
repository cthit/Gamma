import { expect, testWithDefaultGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueLabel } from "../../helpers/strings";

test("given an official client when an admin resets its secret then a new secret is shown", async ({ page, gamma }) => {

  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const prettyName = uniqueLabel("E2E Official Client");

  await page.goto(`${gamma.url}/clients/create`, { timeout: 30000 });
  await page.fill('input[name="prettyName"]', prettyName);
  await page.fill('input[name="svDescription"]', "E2E svensk beskrivning");
  await page.fill('input[name="enDescription"]', "E2E english description");
  await page.fill(
    'input[name="redirectUrl"]',
    "https://example.org/callback",
  );

  await Promise.all([
    page.waitForURL("**/clients/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  const credentialsArticle = page
    .locator("main > article")
    .filter({ hasText: "Credentials" })
    .first();
  await expect(credentialsArticle).toBeVisible({ timeout: 10000 });

  const oldSecret = (
    await credentialsArticle.locator("code").first().innerText()
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

  const newCredentialsArticle = page
    .locator("main > article")
    .filter({ hasText: "Credentials" })
    .first();
  await expect(newCredentialsArticle).toBeVisible({ timeout: 10000 });

  const newSecret = (
    await newCredentialsArticle.locator("code").first().innerText()
  ).trim();
  expect(newSecret.length).toBeGreaterThan(0);
  expect(newSecret).not.toBe(oldSecret);
});
