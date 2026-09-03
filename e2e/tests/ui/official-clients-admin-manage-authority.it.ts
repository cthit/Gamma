import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import { getGammaE2ERuntime } from "../../gamma-setup";

test("an admin can create rotate authorize and delete an official client", async ({
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
  const authorityName = uniqueCid("auth");

  await page.goto(`${gamma.url}/clients/create`, { timeout: 30000 });
  await page.fill('input[name="prettyName"]', prettyName);
  await page.fill('input[name="svDescription"]', "E2E svensk beskrivning");
  await page.fill('input[name="enDescription"]', "E2E english description");
  await page.fill('input[name="redirectUrl"]', "https://example.org/callback");

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().endsWith("/clients/create") &&
        response.status() === 200,
    ),
    page.getByRole("button", { name: "Create" }).click(),
  ]);
  const resetAction = await page
    .locator('form[action^="/clients/"][action$="/reset"]')
    .first()
    .getAttribute("action");
  if (!resetAction) throw new Error("Created client reset action was missing");
  const detailsUrl = new URL(
    resetAction.replace(/\/reset$/, ""),
    gamma.url,
  ).toString();

  await expect(page.getByText("Client details")).toBeVisible({
    timeout: 10000,
  });
  const clientId = (
    await page
      .locator("li", { hasText: "Client id:" })
      .locator("span")
      .innerText()
  ).trim();
  const credentialsArticle = page
    .locator("main > article")
    .filter({ hasText: "Credentials" })
    .first();
  const oldSecret = (
    await credentialsArticle.locator("code").first().innerText()
  ).trim();
  await page.goto(`${gamma.url}/clients`, { timeout: 30000 });
  if (getGammaE2ERuntime() === "kotlin") {
    await expect(page.locator("tr", { hasText: prettyName })).toContainText(
      clientId,
    );
  }
  await page.goto(detailsUrl, { timeout: 30000 });

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().endsWith("/reset") &&
        response.status() === 200,
    ),
    page.getByRole("button", { name: "Reset client secret" }).click(),
  ]);
  const newSecret = (
    await page
      .locator("main > article")
      .filter({ hasText: "Credentials" })
      .first()
      .locator("code")
      .first()
      .innerText()
  ).trim();
  expect(newSecret).not.toBe(oldSecret);

  if (getGammaE2ERuntime() === "kotlin") {
    await page.fill('input[name="authority"]', "invalid authority!");
    const invalidAuthorityResponse = page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().endsWith("/authority"),
    );
    await page
      .locator(
        'button[form="create-client-authority"], form#create-client-authority button[type="submit"]',
      )
      .click();
    expect((await invalidAuthorityResponse).status()).toBe(400);
    await expect(page.locator("p.error")).toContainText(
      "Authority names must contain 2 to 30 lowercase letters or numbers",
    );
    await expect(page.locator('input[name="authority"]')).toHaveValue(
      "invalid authority!",
    );

    await Promise.all([
      page.waitForResponse(
        (response) =>
          response.request().method() === "GET" &&
          response.url().includes("/clients/authority/new-user?query="),
      ),
      page.fill("#authority-user-search", "Michael Scott"),
    ]);
    const userSearchResults = page.locator("#authority-user-search-results");
    await expect(userSearchResults).toContainText("Michael 'Boss' Scott");
    await expect(userSearchResults).not.toContainText("Jim 'Big Tuna' Halpert");
  }

  await page.fill('input[name="authority"]', authorityName);

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/authority") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page
      .locator(
        'button[form="create-client-authority"], form#create-client-authority button[type="submit"]',
      )
      .click(),
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

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/clients", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete" }).click(),
  ]);
  await expect(page.getByText(prettyName)).toHaveCount(0);
});
