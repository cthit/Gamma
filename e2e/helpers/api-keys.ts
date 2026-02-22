import { expect, type Page } from "@playwright/test";

export interface ApiKeyInput {
  prettyName: string;
  svDescription: string;
  enDescription: string;
  keyType: "INFO" | "ACCOUNT_SCAFFOLD" | "ALLOW_LIST";
}

export interface ApiKeyCredentials {
  apiKeyId: string;
  apiKeyToken: string;
}

export async function createApiKeyViaUi(
  page: Page,
  baseUrl: string,
  input: ApiKeyInput,
): Promise<ApiKeyCredentials> {
  await page.goto(`${baseUrl}/api-keys/create`, { timeout: 30000 });
  await expect(page.locator("article > header")).toHaveText("Create api key", {
    timeout: 10000,
  });

  await page.fill('input[name="prettyName"]', input.prettyName);
  await page.fill('input[name="svDescription"]', input.svDescription);
  await page.fill('input[name="enDescription"]', input.enDescription);
  await page.selectOption('select[name="keyType"]', input.keyType);

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/api-keys/create") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.getByRole("button", { name: "Create api key" }).click(),
  ]);

  await expect(page.getByText("Api key details")).toBeVisible({
    timeout: 10000,
  });

  return readApiKeyCredentials(page);
}

export async function readApiKeyCredentials(
  page: Page,
): Promise<ApiKeyCredentials> {
  const apiKeyToken = (
    await page
      .locator('article:has-text("Credentials") code')
      .first()
      .innerText()
  ).trim();

  const resetPath =
    (await page
      .locator('form[data-hx-post$="/reset"]')
      .first()
      .getAttribute("data-hx-post")) ?? "";

  const apiKeyIdMatch = /\/api-keys\/([0-9a-fA-F-]+)\/reset/.exec(resetPath);
  if (!apiKeyIdMatch) {
    throw new Error("Could not parse api key id from reset form path");
  }
  const apiKeyId = apiKeyIdMatch[1];
  if (!apiKeyId) {
    throw new Error("Could not parse api key id from reset form path");
  }

  return {
    apiKeyId,
    apiKeyToken,
  };
}
