import { expect, type Page } from "@playwright/test";

export interface ClientApiCredentials {
  clientId: string;
  redirectUri: string;
  apiKeyId: string;
  apiKeyToken: string;
}

export async function createUserClientWithApiKeyViaUi(
  page: Page,
  baseUrl: string,
  prettyName: string,
): Promise<ClientApiCredentials> {
  await page.goto(`${baseUrl}/my-clients/create`, { timeout: 30000 });
  await expect(page.getByText("Create new client")).toBeVisible({
    timeout: 10000,
  });

  const redirectUri = `${baseUrl}/oauth2/callback`;

  await page.fill('input[name="prettyName"]', prettyName);
  await page.fill('input[name="svDescription"]', "API snapshot sv description");
  await page.fill('input[name="enDescription"]', "API snapshot en description");
  await page.fill('input[name="redirectUrl"]', redirectUri);
  await page.check('input[name="generateApiKey"]');
  await page.check('input[name="emailScope"]');

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/my-clients") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  await expect(page.getByText("Client details")).toBeVisible({
    timeout: 10000,
  });

  const clientId = (
    await page.locator('li:has-text("Client id:") span').first().innerText()
  ).trim();
  const authorizationHeader = (
    await page
      .locator('article:has-text("Credentials") code')
      .filter({ hasText: "pre-shared" })
      .first()
      .innerText()
  ).trim();

  const credentialsMatch = /pre-shared\s+([0-9a-fA-F-]+):(\S+)/.exec(
    authorizationHeader,
  );
  if (!credentialsMatch) {
    throw new Error("Could not parse client api key credentials from page");
  }

  const apiKeyId = credentialsMatch[1];
  const apiKeyToken = credentialsMatch[2];

  if (!apiKeyId || !apiKeyToken) {
    throw new Error("Parsed client api key credentials were empty");
  }

  return {
    clientId,
    redirectUri,
    apiKeyId,
    apiKeyToken,
  };
}
