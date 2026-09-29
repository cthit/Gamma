import {
  createHash,
  randomBytes,
  createPublicKey,
  verify,
  type JsonWebKey,
} from "node:crypto";
import { expect, type Page, type APIRequestContext } from "@playwright/test";

export interface CreatedUserClient {
  clientId: string;
  clientSecret: string;
  redirectUri: string;
  clientUid: string;
}

export async function createUserClientViaUi(
  page: Page,
  baseUrl: string,
  prettyName: string,
  options: {
    emailScope?: boolean;
    official?: boolean;
    superGroupId?: string;
  } = {},
): Promise<CreatedUserClient> {
  await page.goto(
    `${baseUrl}/${options.official ? "clients" : "my-clients"}/create`,
    { timeout: 30000 },
  );
  await expect(page.locator('input[name="prettyName"]')).toBeVisible();

  const redirectUri = `${baseUrl}/oauth2/callback`;

  await page.fill('input[name="prettyName"]', prettyName);
  await page.fill('input[name="svDescription"]', "E2E svensk beskrivning");
  await page.fill('input[name="enDescription"]', "E2E english description");
  await page.fill('input[name="redirectUrl"]', redirectUri);
  await page
    .locator('input[name="emailScope"]')
    .setChecked(options.emailScope ?? true);

  if (options.superGroupId) {
    await page.getByRole("button", { name: "Add restriction" }).click();
    await page.locator("select.restriction").selectOption(options.superGroupId);
  }

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response
          .url()
          .includes(options.official ? "/clients/create" : "/my-clients") &&
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

  await page.waitForURL(/\/clients\/[0-9a-f-]+$/);
  return {
    clientUid: new URL(page.url()).pathname.split("/").at(-1)!,
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
  options: {
    pkce?: boolean;
    scopes?: string;
    consent?: "required" | "none";
  } = {},
): Promise<{
  code: string;
  state: string;
  codeVerifier: string;
  nonce: string;
}> {
  const state = randomBytes(16).toString("hex");
  const nonce = randomBytes(16).toString("hex");
  const codeVerifier = randomBytes(32).toString("base64url");
  const codeChallenge = createHash("sha256")
    .update(codeVerifier)
    .digest("base64url");

  const authorizeUrl = new URL("/oauth2/authorize", baseUrl);
  authorizeUrl.search = new URLSearchParams({
    response_type: "code",
    client_id: clientId,
    redirect_uri: redirectUri,
    scope: options.scopes ?? "openid profile email",
    state,
    nonce,
    ...(options.pkce === false
      ? {}
      : { code_challenge: codeChallenge, code_challenge_method: "S256" }),
  }).toString();

  await page.goto(authorizeUrl.toString(), { timeout: 30000 });

  const authorizeButton = page.getByRole("button", { name: "Authorize" });
  if (options.consent === "required") {
    await expect(authorizeButton).toBeVisible();
  }
  if (options.consent === "none") {
    await page.waitForURL("**/oauth2/callback**");
    await expect(authorizeButton).toHaveCount(0);
  } else if (await authorizeButton.isVisible().catch(() => false)) {
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
    nonce,
  };
}

export async function exchangeCode(
  request: APIRequestContext,
  baseUrl: string,
  client: Pick<CreatedUserClient, "clientId" | "clientSecret" | "redirectUri">,
  grant: { code: string; codeVerifier?: string },
  options: {
    method?: "basic" | "post";
    secret?: string;
    redirectUri?: string;
  } = {},
) {
  const secret = options.secret ?? client.clientSecret;
  return request.post(`${baseUrl}/oauth2/token`, {
    maxRedirects: 0,
    headers:
      options.method === "post"
        ? {}
        : {
            Authorization: `Basic ${Buffer.from(`${client.clientId}:${secret}`).toString("base64")}`,
          },
    form: {
      grant_type: "authorization_code",
      code: grant.code,
      redirect_uri: options.redirectUri ?? client.redirectUri,
      ...(grant.codeVerifier ? { code_verifier: grant.codeVerifier } : {}),
      ...(options.method === "post"
        ? { client_id: client.clientId, client_secret: secret }
        : {}),
    },
  });
}

export interface TokenResponse {
  access_token: string;
  id_token: string;
  token_type: string;
  scope: string;
}

export async function verifyIdentity(
  request: APIRequestContext,
  baseUrl: string,
  clientId: string,
  tokens: TokenResponse,
  nonce: string,
  email: string | null,
) {
  expect(tokens.token_type).toBe("Bearer");
  const discoveryResponse = await request.get(
    `${baseUrl}/.well-known/openid-configuration`,
  );
  expect(discoveryResponse.status()).toBe(200);
  const discovery = (await discoveryResponse.json()) as {
    issuer: string;
    jwks_uri: string;
  };
  const keysResponse = await request.get(discovery.jwks_uri);
  expect(keysResponse.status()).toBe(200);
  const { keys } = (await keysResponse.json()) as {
    keys: (JsonWebKey & { kid: string })[];
  };
  const [headerPart, payloadPart, signature] = tokens.id_token.split(".");
  expect(headerPart && payloadPart && signature).toBeTruthy();
  const header = JSON.parse(
    Buffer.from(headerPart!, "base64url").toString(),
  ) as { kid: string; alg: string };
  expect(header.alg).toBe("RS256");
  const jwk = keys.find((key) => key.kid === header.kid);
  expect(jwk).toBeDefined();
  expect(jwk).not.toHaveProperty("d");
  expect(
    verify(
      "RSA-SHA256",
      Buffer.from(`${headerPart}.${payloadPart}`),
      createPublicKey({ key: jwk!, format: "jwk" }),
      Buffer.from(signature!, "base64url"),
    ),
  ).toBe(true);
  const claims = JSON.parse(
    Buffer.from(payloadPart!, "base64url").toString(),
  ) as Record<string, unknown>;
  expect(claims.iss).toBe(discovery.issuer);
  expect(Array.isArray(claims.aud) ? claims.aud : [claims.aud]).toContain(
    clientId,
  );
  expect(claims.exp).toBeGreaterThan(Date.now() / 1000);
  expect(claims.nonce).toBe(nonce);
  expect(claims.sub).toBeTruthy();
  expect(claims.cid).toBe("mscott");
  expect(claims.nickname).toBe("Boss");
  const infoResponse = await request.get(`${baseUrl}/oauth2/userinfo`, {
    headers: { Authorization: `Bearer ${tokens.access_token}` },
    maxRedirects: 0,
  });
  expect(infoResponse.status()).toBe(200);
  const info = (await infoResponse.json()) as Record<string, unknown>;
  expect(info.sub).toBe(claims.sub);
  expect(info.given_name).toBe("Michael");
  expect(info.family_name).toBe("Scott");
  expect(info.cid).toBe("mscott");
  if (email) {
    expect(claims.email).toBe(email);
    expect(info.email).toBe(email);
  } else {
    expect(claims).not.toHaveProperty("email");
    expect(info).not.toHaveProperty("email");
  }
}
