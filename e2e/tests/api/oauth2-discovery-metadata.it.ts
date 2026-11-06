import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";

// Every field shared by both discovery documents, describing only what Gamma supports.
function sharedMetadata(issuer: string) {
  return {
    issuer,
    authorization_endpoint: `${issuer}/oauth2/authorize`,
    token_endpoint: `${issuer}/oauth2/token`,
    token_endpoint_auth_methods_supported: [
      "client_secret_basic",
      "client_secret_post",
    ],
    jwks_uri: `${issuer}/oauth2/jwks`,
    response_types_supported: ["code"],
    grant_types_supported: ["authorization_code"],
    scopes_supported: ["openid", "profile", "email"],
    code_challenge_methods_supported: ["S256"],
  };
}

test("OAuth 2.0 authorization server metadata only advertises what Gamma supports", async ({
  request,
  gamma,
}) => {
  const response = await request.get(
    `${gamma.url}/.well-known/oauth-authorization-server`,
  );
  expect(response.status()).toBe(200);
  expect(await response.json()).toEqual(sharedMetadata(gamma.url));
});

test("OpenID provider configuration only advertises what Gamma supports", async ({
  request,
  gamma,
}) => {
  const response = await request.get(
    `${gamma.url}/.well-known/openid-configuration`,
  );
  expect(response.status()).toBe(200);
  expect(await response.json()).toEqual({
    ...sharedMetadata(gamma.url),
    userinfo_endpoint: `${gamma.url}/oauth2/userinfo`,
    subject_types_supported: ["public"],
    id_token_signing_alg_values_supported: ["RS256"],
    claims_supported: [
      "sub",
      "iss",
      "aud",
      "exp",
      "iat",
      "auth_time",
      "nonce",
      "azp",
      "sid",
      "name",
      "given_name",
      "family_name",
      "nickname",
      "locale",
      "picture",
      "cid",
      "email",
    ],
  });
});

test("Authorization server endpoints that are not advertised are not served", async ({
  request,
  gamma,
}) => {
  for (const path of [
    "/oauth2/revoke",
    "/oauth2/introspect",
    "/oauth2/par",
    "/oauth2/device_authorization",
  ]) {
    const response = await request.post(`${gamma.url}${path}`, {
      form: { token: "some-token", client_id: "some-client" },
      maxRedirects: 0,
    });
    expect(response.status(), path).toBe(404);
  }

  const logout = await request.get(
    `${gamma.url}/connect/logout?id_token_hint=some-token`,
    { maxRedirects: 0 },
  );
  expect(logout.status()).toBe(404);
});
