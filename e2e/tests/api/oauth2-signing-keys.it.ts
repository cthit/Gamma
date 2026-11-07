import { writeFile } from "node:fs/promises";
import { GenericContainer, Wait } from "testcontainers";
import type { APIRequestContext } from "@playwright/test";
import {
  expect,
  testWithEnvironment as test,
} from "../../helpers/test-fixtures";
import { startMockGamma } from "../../helpers/gamma";
import { stopGammaInstance } from "../../gamma-setup";
import { login } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
  exchangeCode,
  verifyTokenSignature,
  type TokenResponse,
} from "../../helpers/oauth";
import { generateSigningKey, publishedJwk } from "../../helpers/signing-keys";
import { uniqueLabel } from "../../helpers/strings";

async function publishedKeys(request: APIRequestContext, baseUrl: string) {
  const response = await request.get(`${baseUrl}/oauth2/jwks`);
  expect(response.status()).toBe(200);
  return ((await response.json()) as { keys: unknown[] }).keys;
}

test("Gamma does not start in production without an OAuth2 signing key", async ({
  env,
}) => {
  const container = await new GenericContainer(
    process.env.GAMMA_TEST_IMAGE ?? "gamma-app:test",
  )
    .withNetwork(env.network)
    .withEnvironment({ PRODUCTION: "true", OAUTH2_SIGNING_KEY: "" })
    .withWaitStrategy(Wait.forLogMessage("No OAuth2 signing key is configured"))
    .withStartupTimeout(120000)
    .start();
  await container.stop();
});

test("rotating the OAuth2 signing key keeps tokens signed by the previous key valid", async ({
  env,
  page,
  browser,
  request,
}, testInfo) => {
  const previousKey = generateSigningKey("pkcs1");
  const nextKey = generateSigningKey();

  const before = await startMockGamma(env, {
    env: { OAUTH2_SIGNING_KEY: previousKey.privateKeyPem },
  });
  let tokens: TokenResponse;
  try {
    expect(await publishedKeys(request, before.url)).toEqual([
      publishedJwk(previousKey),
    ]);

    await login(page, before.url, "mscott", "password1337", "Boss");
    const client = await createUserClientViaUi(
      page,
      before.url,
      uniqueLabel("Rotation"),
    );
    const grant = await authorizeClientWithPkce(
      page,
      before.url,
      client.clientId,
      client.redirectUri,
      { consent: "required" },
    );
    const response = await exchangeCode(request, before.url, client, grant);
    expect(response.status()).toBe(200);
    tokens = (await response.json()) as TokenResponse;
    const { kid } = await verifyTokenSignature(
      request,
      before.url,
      tokens.id_token,
    );
    expect(kid).toBe(publishedJwk(previousKey).kid);
  } finally {
    await stopGammaInstance(before);
  }

  const nextKeyPath = testInfo.outputPath("signing-key.pem");
  await writeFile(nextKeyPath, nextKey.privateKeyPem);
  const after = await startMockGamma(env, {
    env: {
      OAUTH2_SIGNING_KEY: "/tmp/oauth2/signing-key.pem",
      OAUTH2_VERIFICATION_KEYS: previousKey.publicKeyPem,
    },
    filesToCopy: [
      { source: nextKeyPath, target: "/tmp/oauth2/signing-key.pem" },
    ],
    waitForAdminCredentials: false,
    waitForBootstrapApiKeys: false,
  });
  try {
    expect(await publishedKeys(request, after.url)).toEqual([
      publishedJwk(nextKey),
      publishedJwk(previousKey),
    ]);

    const { kid: previousKid } = await verifyTokenSignature(
      request,
      after.url,
      tokens.id_token,
    );
    expect(previousKid).toBe(publishedJwk(previousKey).kid);
    const infoResponse = await request.get(`${after.url}/oauth2/userinfo`, {
      headers: { Authorization: `Bearer ${tokens.access_token}` },
      maxRedirects: 0,
    });
    expect(infoResponse.status()).toBe(200);

    const context = await browser.newContext({ ignoreHTTPSErrors: true });
    try {
      const newPage = await context.newPage();
      await login(newPage, after.url, "mscott", "password1337", "Boss");
      const newClient = await createUserClientViaUi(
        newPage,
        after.url,
        uniqueLabel("Rotation"),
      );
      const grant = await authorizeClientWithPkce(
        newPage,
        after.url,
        newClient.clientId,
        newClient.redirectUri,
        { consent: "required" },
      );
      const response = await exchangeCode(request, after.url, newClient, grant);
      expect(response.status()).toBe(200);
      const newTokens = (await response.json()) as TokenResponse;
      const { kid: nextKid } = await verifyTokenSignature(
        request,
        after.url,
        newTokens.id_token,
      );
      expect(nextKid).toBe(publishedJwk(nextKey).kid);
    } finally {
      await context.close();
    }
  } finally {
    await stopGammaInstance(after);
  }
});
