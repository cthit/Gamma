import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
  exchangeCode,
  verifyIdentity,
  type TokenResponse,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";

for (const mode of [
  { pkce: true, method: "basic" },
  { pkce: false, method: "post" },
] as const) {
  test(`OAuth code flow with PKCE=${mode.pkce} and ${mode.method} client authentication verifies the ID token`, async ({
    page,
    request,
    gamma,
  }) => {
    await login(page, gamma.url, "mscott", "password1337", "Boss");
    const client = await createUserClientViaUi(
      page,
      gamma.url,
      uniqueLabel("OIDC"),
    );
    const grant = await authorizeClientWithPkce(
      page,
      gamma.url,
      client.clientId,
      client.redirectUri,
      { pkce: mode.pkce, consent: "required" },
    );
    const response = await exchangeCode(
      request,
      gamma.url,
      client,
      {
        code: grant.code,
        ...(mode.pkce ? { codeVerifier: grant.codeVerifier } : {}),
      },
      { method: mode.method },
    );
    expect(response.status()).toBe(200);
    await verifyIdentity(
      request,
      gamma.url,
      client.clientId,
      (await response.json()) as TokenResponse,
      grant.nonce,
      "mscott@example.org",
    );
  });
}

test("OAuth without email scope omits email from the ID token and UserInfo", async ({
  page,
  request,
  gamma,
}) => {
  await login(page, gamma.url, "mscott", "password1337", "Boss");
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    uniqueLabel("Profile only"),
    { emailScope: false },
  );
  const grant = await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { scopes: "openid profile", consent: "required" },
  );
  const response = await exchangeCode(request, gamma.url, client, grant);
  expect(response.status()).toBe(200);
  await verifyIdentity(
    request,
    gamma.url,
    client.clientId,
    (await response.json()) as TokenResponse,
    grant.nonce,
    null,
  );
});

test("OAuth rejects invalid exchanges and authorization code replay", async ({
  page,
  request,
  gamma,
}) => {
  await login(page, gamma.url, "mscott", "password1337", "Boss");
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    uniqueLabel("Invalid exchange"),
  );
  let first = true;
  for (const invalid of ["secret", "verifier", "redirect", "replay"] as const) {
    const grant = await authorizeClientWithPkce(
      page,
      gamma.url,
      client.clientId,
      client.redirectUri,
      { consent: first ? "required" : "none" },
    );
    first = false;
    if (invalid === "replay") {
      const valid = await exchangeCode(request, gamma.url, client, grant);
      expect(valid.status()).toBe(200);
    }
    const response = await exchangeCode(
      request,
      gamma.url,
      client,
      invalid === "verifier"
        ? {
            ...grant,
            codeVerifier:
              "incorrect-verifier-abcdefghijklmnopqrstuvwxyz0123456789",
          }
        : grant,
      invalid === "secret"
        ? { secret: "incorrect-secret" }
        : invalid === "redirect"
          ? { redirectUri: `${gamma.url}/wrong-callback` }
          : {},
    );
    expect([400, 401]).toContain(response.status());
    expect(response.headers()["content-type"]).toContain("application/json");
    const error = (await response.json()) as Record<string, unknown>;
    expect(error.error).toBe(
      invalid === "secret" ? "invalid_client" : "invalid_grant",
    );
    expect(error).not.toHaveProperty("access_token");
    expect(error).not.toHaveProperty("id_token");
  }
  const invalidToken = await request.get(`${gamma.url}/oauth2/userinfo`, {
    headers: { Authorization: "Bearer invalid-token" },
    maxRedirects: 0,
  });
  expect(invalidToken.status()).toBe(401);
});
