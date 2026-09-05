import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { uniqueLabel } from "../../helpers/strings";
import { getGammaE2ERuntime } from "../../gamma-setup";

test("a personal client owner can create rotate inspect and delete credentials", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  const prettyName = uniqueLabel("Rotation");
  await page.goto(`${gamma.url}/my-clients/create`, { timeout: 30000 });
  await page.fill('input[name="prettyName"]', prettyName);
  if (getGammaE2ERuntime() === "kotlin") {
    const csrfToken = await page
      .locator('#create-clients input[name="_csrf"]')
      .inputValue();
    const invalidResponse = await page.request.post(`${gamma.url}/my-clients`, {
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        Origin: gamma.url,
        "X-CSRF-TOKEN": csrfToken,
      },
      data: `prettyName=${encodeURIComponent(prettyName)}`,
    });
    expect(invalidResponse.status()).toBe(400);
    const invalidBody = await invalidResponse.text();
    expect(invalidBody).toContain("Redirect URI must be");
    expect(invalidBody).toContain(`value="${prettyName}"`);
  }

  await page.fill('input[name="prettyName"]', prettyName);
  await page.fill('input[name="svDescription"]', "Svensk beskrivning");
  await page.fill('input[name="enDescription"]', "English description");
  await page.fill('input[name="redirectUrl"]', "https://example.org/callback");
  await page.check('input[name="generateApiKey"]');

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().endsWith("/my-clients") &&
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
  const clientId = (
    await page
      .locator("li", { hasText: "Client id:" })
      .locator("span")
      .innerText()
  ).trim();

  if (getGammaE2ERuntime() === "kotlin") {
    const originalSecret = (
      await page
        .locator('article:has-text("Credentials") code')
        .first()
        .innerText()
    ).trim();
    await expect(
      page
        .locator('article:has-text("Credentials") code')
        .filter({ hasText: "pre-shared" }),
    ).toBeVisible();

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

    const clientSecret = page
      .locator('article:has-text("Credentials") code')
      .first();
    await expect(clientSecret).not.toHaveText(originalSecret, {
      timeout: 10000,
    });
    const replacementSecret = (await clientSecret.innerText()).trim();
    expect(replacementSecret).not.toEqual(originalSecret);

    await expect(
      page.getByRole("button", { name: "Reset API key" }),
    ).toHaveCount(0);
    await expect(
      page
        .locator('article:has-text("Credentials") code')
        .filter({ hasText: "pre-shared" }),
    ).toHaveCount(0);
  }

  await page.goto(`${gamma.url}/my-clients`, { timeout: 30000 });
  if (getGammaE2ERuntime() === "kotlin") {
    await expect(page.locator("tr", { hasText: prettyName })).toContainText(
      clientId,
    );
  }

  await logout(page);
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );
  await page.goto(`${gamma.url}/user-clients`, { timeout: 30000 });
  const row = page.locator("tr", { hasText: prettyName }).first();
  await expect(row).toBeVisible({ timeout: 10000 });
  await expect(row.getByRole("link", { name: /Jim .*Halpert/ })).toBeVisible({
    timeout: 10000,
  });
  if (getGammaE2ERuntime() === "kotlin") {
    await expect(row).toContainText(clientId);
    await expect(
      row.getByRole("link", { name: "jhalpert@example.org" }),
    ).toHaveAttribute("href", "mailto:jhalpert@example.org");
  }

  await logout(page);
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(detailsUrl, { timeout: 30000 });
  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/my-clients", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete" }).click(),
  ]);
  await expect(page.getByText(prettyName)).toHaveCount(0);
});
