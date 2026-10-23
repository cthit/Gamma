import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
  exchangeCode,
  type TokenResponse,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";

test("given a valid api key when requesting an unknown api path then a 404 error is returned without redirect", async ({
  request,
  gamma,
}) => {
  const credentials = gamma.apiKeys?.INFO;
  if (!credentials) {
    throw new Error("Bootstrap INFO api key credentials were not captured");
  }
  const authorization = `pre-shared ${credentials.id}:${credentials.token}`;

  const jsonResponse = await request.get(
    `${gamma.url}/api/info/v1/does-not-exist`,
    {
      maxRedirects: 0,
      headers: { Authorization: authorization, Accept: "application/json" },
    },
  );
  expect(jsonResponse.status()).toBe(404);
  expect(jsonResponse.headers()["content-type"]).toContain("application/json");
  expect(await jsonResponse.json()).toMatchObject({
    status: 404,
    error: "Not Found",
  });

  const htmlResponse = await request.get(
    `${gamma.url}/api/info/v1/does-not-exist`,
    {
      maxRedirects: 0,
      headers: { Authorization: authorization, Accept: "text/html" },
    },
  );
  expect(htmlResponse.status()).toBe(404);
  const htmlBody = await htmlResponse.text();
  expect(htmlBody).toContain("Page not found.");
  expect(htmlBody).not.toContain("Hey,");

  const missingKeyResponse = await request.get(
    `${gamma.url}/api/info/v1/does-not-exist`,
    { maxRedirects: 0 },
  );
  expect(missingKeyResponse.status()).toBe(401);
});

test("given an OAuth access token when an OAuth endpoint fails then the error status is returned without redirect", async ({
  page,
  request,
  gamma,
}) => {
  await login(page, gamma.url, "mscott", "password1337", "Boss");
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    uniqueLabel("Error responses"),
  );
  const grant = await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "required" },
  );
  const tokenResponse = await exchangeCode(request, gamma.url, client, grant);
  expect(tokenResponse.status()).toBe(200);
  const tokens = (await tokenResponse.json()) as TokenResponse;
  const authorization = `Bearer ${tokens.access_token}`;

  const bogusAuthorizeUrl = new URL("/oauth2/authorize", gamma.url);
  bogusAuthorizeUrl.search = new URLSearchParams({
    client_id: "bogus",
    response_type: "code",
    redirect_uri: client.redirectUri,
    scope: "openid",
  }).toString();

  const jsonResponse = await request.get(bogusAuthorizeUrl.toString(), {
    maxRedirects: 0,
    headers: { Authorization: authorization, Accept: "application/json" },
  });
  expect(jsonResponse.status()).toBe(400);
  expect(jsonResponse.headers()["content-type"]).toContain("application/json");
  expect(await jsonResponse.json()).toMatchObject({
    status: 400,
    error: "Bad Request",
  });

  const htmlResponse = await request.get(bogusAuthorizeUrl.toString(), {
    maxRedirects: 0,
    headers: { Authorization: authorization, Accept: "text/html" },
  });
  expect(htmlResponse.status()).toBe(400);
  const htmlBody = await htmlResponse.text();
  expect(htmlBody).toContain("400 - Bad Request");
  expect(htmlBody).not.toContain("Hey,");

  const userInfoResponse = await request.get(`${gamma.url}/oauth2/userinfo`, {
    maxRedirects: 0,
    headers: { Authorization: authorization },
  });
  expect(userInfoResponse.status()).toBe(200);

  const browserResponse = await page.goto(bogusAuthorizeUrl.toString(), {
    timeout: 30000,
  });
  expect(browserResponse?.status()).toBe(400);
  await expect(page.locator("main > article > header")).toHaveText(
    "400 - Bad Request",
  );
  await expect(page.getByText("Hey, Boss!")).toBeVisible();
});
