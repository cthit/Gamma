import { expect, type Page } from "@playwright/test";
import { articleByHeading } from "./a11y";

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
  await expect(
    page.getByRole("heading", { level: 2, name: "Create api key" }),
  ).toBeVisible({ timeout: 10000 });

  await page.getByLabel("Pretty name", { exact: true }).fill(input.prettyName);
  await page
    .getByLabel("Swedish description", { exact: true })
    .fill(input.svDescription);
  await page
    .getByLabel("English description", { exact: true })
    .fill(input.enDescription);
  await page
    .getByRole("combobox", { name: "Key type", exact: true })
    .selectOption(input.keyType);

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
  const credentials = articleByHeading(page, "Credentials");
  const apiKeyToken = (
    await credentials.getByRole("code").first().innerText()
  ).trim();

  const authorizationHeader = (
    await credentials
      .getByRole("code")
      .filter({ hasText: "pre-shared" })
      .first()
      .innerText()
  ).trim();

  const apiKeyIdMatch = /pre-shared\s+([0-9a-fA-F-]+):/.exec(
    authorizationHeader,
  );
  if (!apiKeyIdMatch) {
    throw new Error("Could not parse api key id from the authorization header");
  }
  const apiKeyId = apiKeyIdMatch[1];
  if (!apiKeyId) {
    throw new Error("Could not parse api key id from the authorization header");
  }

  return {
    apiKeyId,
    apiKeyToken,
  };
}
