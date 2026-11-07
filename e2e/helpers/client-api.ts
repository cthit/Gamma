import { expect, type Page } from "@playwright/test";
import { articleByHeading } from "./a11y";

export interface ClientApiCredentials {
  clientUid: string;
  clientId: string;
  clientSecret: string;
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

  await page.getByLabel("Pretty name", { exact: true }).fill(prettyName);
  await page
    .getByLabel("Swedish description", { exact: true })
    .fill("API snapshot sv description");
  await page
    .getByLabel("English description", { exact: true })
    .fill("API snapshot en description");
  await page.getByLabel("Redirect url", { exact: true }).fill(redirectUri);
  await page.getByLabel("Generate api key", { exact: true }).check();
  await page.getByLabel("Include email scope", { exact: true }).check();

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
    await articleByHeading(page, "Client details")
      .getByRole("code")
      .first()
      .innerText()
  ).trim();
  const clientSecret = (
    await articleByHeading(page, "Credentials")
      .getByRole("code")
      .first()
      .innerText()
  ).trim();
  await page.waitForURL(/\/clients\/[0-9a-f-]+$/);
  const clientUid = new URL(page.url()).pathname.split("/").at(-1)!;
  const authorizationHeader = (
    await articleByHeading(page, "Credentials")
      .getByRole("code")
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
    clientUid,
    clientId,
    clientSecret,
    redirectUri,
    apiKeyId,
    apiKeyToken,
  };
}
