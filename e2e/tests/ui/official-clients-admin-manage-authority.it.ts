import { expect, testWithDefaultGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";

test("given an official client when an admin creates and deletes an authority then authority membership is updated", async ({ page, gamma }) => {

  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const prettyName = uniqueLabel("E2E Official Client");
  const authorityName = uniqueCid("auth");

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

  await expect(page.getByText("Client details")).toBeVisible({
    timeout: 10000,
  });

  await page.fill('input[name="authority"]', authorityName);

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/authority") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.locator('button[form="create-client-authority"]').click(),
  ]);

  const authorityArticle = page
    .locator("main > article")
    .filter({ hasText: authorityName })
    .first();

  await expect(authorityArticle).toBeVisible({
    timeout: 10000,
  });

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes(`/authority/${authorityName}`) &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    authorityArticle.getByRole("button", { name: "Delete" }).click(),
  ]);

  await expect(
    page.locator("main > article").filter({ hasText: authorityName }),
  ).toHaveCount(0);
});
