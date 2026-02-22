import { createHash, randomBytes } from "node:crypto";
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";

test("oauth2 userinfo returns expected claims for mscott", async ({
  page,
  request,
  gamma,
}) => {
  await page.goto(gamma.url, { timeout: 60000 });
  await expect(page.getByText("Gamma")).toBeVisible({ timeout: 60000 });

  await page.fill('input[name="username"]', "mscott");
  await page.fill('input[name="password"]', "password1337");

  await Promise.all([
    page.waitForURL((url) => url.pathname !== "/login", { timeout: 15000 }),
    page.click('button:has-text("Login")'),
  ]);

  await expect(page.getByText("Hey, Boss!")).toBeVisible({ timeout: 10000 });

  await page.goto(`${gamma.url}/my-clients/create`, { timeout: 30000 });
  await expect(page.getByText("Create new client")).toBeVisible({
    timeout: 10000,
  });

  await page.fill('input[name="prettyName"]', "E2E OIDC Client");
  await page.fill('input[name="svDescription"]', "E2E svensk beskrivning");
  await page.fill('input[name="enDescription"]', "E2E english description");
  const redirectUri = `${gamma.url}/oauth2/callback`;
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
    page.click('button:has-text("Create")'),
  ]);

  await expect(page.getByText("Client details")).toBeVisible({
    timeout: 10000,
  });
  await expect(
    page.locator("article header", { hasText: "Credentials" }).first(),
  ).toBeVisible({ timeout: 10000 });

  const clientSecret = (
    await page
      .locator('article:has-text("Credentials") code')
      .first()
      .innerText()
  ).trim();
  const clientId = (
    await page.locator('li:has-text("Client id:") span').first().innerText()
  ).trim();

  expect(clientSecret).not.toEqual("");
  expect(clientId).not.toEqual("");

  const state = "e2e-state";
  const codeVerifier = randomBytes(32).toString("base64url");
  const codeChallenge = createHash("sha256")
    .update(codeVerifier)
    .digest("base64url");

  const authorizeUrl = new URL("/oauth2/authorize", gamma.url);
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
  const authorizationCode = callbackUrl.searchParams.get("code");
  const returnedState = callbackUrl.searchParams.get("state");

  expect(authorizationCode).not.toBeNull();
  expect(returnedState).toBe(state);

  const basicAuth = Buffer.from(`${clientId}:${clientSecret}`).toString(
    "base64",
  );
  const tokenResponse = await request.post(`${gamma.url}/oauth2/token`, {
    headers: {
      Authorization: `Basic ${basicAuth}`,
    },
    form: {
      grant_type: "authorization_code",
      code: authorizationCode ?? "",
      redirect_uri: redirectUri,
      code_verifier: codeVerifier,
    },
  });

  expect(tokenResponse.ok()).toBe(true);

  const tokenJson = (await tokenResponse.json()) as {
    access_token: string;
    token_type: string;
    scope: string;
  };

  expect(tokenJson.access_token).toBeTruthy();
  expect(tokenJson.token_type).toEqual("Bearer");
  expect(tokenJson.scope).toContain("openid");
  expect(tokenJson.scope).toContain("profile");
  expect(tokenJson.scope).toContain("email");

  const userInfoResponse = await request.get(`${gamma.url}/oauth2/userinfo`, {
    headers: {
      Authorization: `Bearer ${tokenJson.access_token}`,
    },
  });

  expect(userInfoResponse.ok()).toBe(true);

  const userInfo = (await userInfoResponse.json()) as Record<string, unknown>;
  expect(userInfo.cid).toEqual("mscott");
  expect(userInfo.given_name).toEqual("Michael");
  expect(userInfo.family_name).toEqual("Scott");
  expect(userInfo.nickname).toEqual("Boss");
  expect(userInfo.name).toEqual("Michael 'Boss' Scott");
  expect(userInfo.email).toEqual("mscott@example.org");
  expect(userInfo.locale).toEqual("en");
  expect(String(userInfo.picture)).toContain("/images/user/avatar/");
  expect(userInfo.sub).toBeTruthy();
});
