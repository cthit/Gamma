import { createHash, randomBytes } from "node:crypto";
import { expect, type Page } from "@playwright/test";

export interface CreatedUserClient {
  clientId: string;
  clientSecret: string;
  redirectUri: string;
}

export async function createUserClientViaUi(
  page: Page,
  baseUrl: string,
  prettyName: string,
): Promise<CreatedUserClient> {
  await page.goto(`${baseUrl}/my-clients/create`, { timeout: 30000 });
  await expect(page.getByText("Create new client")).toBeVisible({
    timeout: 10000,
  });

  const redirectUri = `${baseUrl}/oauth2/callback`;

  await page.fill('input[name="prettyName"]', prettyName);
  await page.fill('input[name="svDescription"]', "E2E svensk beskrivning");
  await page.fill('input[name="enDescription"]', "E2E english description");
  await page.fill('input[name="redirectUrl"]', redirectUri);
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

  const clientSecret = (
    await page
      .locator('article:has-text("Credentials") code')
      .first()
      .innerText()
  ).trim();
  const clientId = (
    await page.locator('li:has-text("Client id:") span').first().innerText()
  ).trim();

  return {
    clientId,
    clientSecret,
    redirectUri,
  };
}

export async function authorizeClientWithPkce(
  page: Page,
  baseUrl: string,
  clientId: string,
  redirectUri: string,
): Promise<{ code: string; state: string; codeVerifier: string }> {
  const state = `state-${Date.now()}`;
  const codeVerifier = randomBytes(32).toString("base64url");
  const codeChallenge = createHash("sha256")
    .update(codeVerifier)
    .digest("base64url");

  const authorizeUrl = new URL("/oauth2/authorize", baseUrl);
  authorizeUrl.search = new URLSearchParams({
    response_type: "code",
    client_id: clientId,
    redirect_uri: redirectUri,
    scope: "openid profile email",
    state,
    code_challenge: codeChallenge,
    code_challenge_method: "S256",
  }).toString();

  await page.goto(authorizeUrl.toString(), { timeout: 30000 });

  const authorizeButton = page.getByRole("button", { name: "Authorize" });
  if (await authorizeButton.isVisible().catch(() => false)) {
    await Promise.all([
      page.waitForURL("**/oauth2/callback**", { timeout: 15000 }),
      authorizeButton.click(),
    ]);
  } else {
    await page.waitForURL("**/oauth2/callback**", { timeout: 15000 });
  }

  const callbackUrl = new URL(page.url());
  const code = callbackUrl.searchParams.get("code");
  const returnedState = callbackUrl.searchParams.get("state");

  expect(code).not.toBeNull();
  expect(returnedState).toBe(state);

  return {
    code: code ?? "",
    state,
    codeVerifier,
  };
}
