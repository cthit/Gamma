import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import {
  type GammaBootstrapApiKeyType,
  type GammaInstance,
} from "../../gamma-setup";

test("given api keys when calling routes outside their api scope then access is forbidden", async ({
  request,
  gamma,
}) => {
  const infoKeyAuthHeader = makeAuthHeader(getApiKeyCredentials(gamma, "INFO"));
  const accountScaffoldAuthHeader = makeAuthHeader(
    getApiKeyCredentials(gamma, "ACCOUNT_SCAFFOLD"),
  );

  const infoAgainstAccountScaffold = await request.get(
    `${gamma.url}/api/account-scaffold/v1/users`,
    {
      headers: {
        Accept: "application/json",
        Authorization: infoKeyAuthHeader,
      },
    },
  );

  expect(infoAgainstAccountScaffold.status()).toBe(403);
  expect(infoAgainstAccountScaffold.headers()["content-type"]).toContain(
    "application/json",
  );
  expect(await infoAgainstAccountScaffold.json()).toEqual({
    error: "Forbidden",
    message: "FORBIDDEN",
    status: 403,
  });

  const accountScaffoldAgainstClient = await request.get(
    `${gamma.url}/api/client/v1/groups`,
    {
      headers: {
        Accept: "application/json",
        Authorization: accountScaffoldAuthHeader,
      },
    },
  );

  expect(accountScaffoldAgainstClient.status()).toBe(403);
  expect(accountScaffoldAgainstClient.headers()["content-type"]).toContain(
    "application/json",
  );
  expect(await accountScaffoldAgainstClient.json()).toEqual({
    error: "Forbidden",
    message: "FORBIDDEN",
    status: 403,
  });
});

function getApiKeyCredentials(
  gamma: GammaInstance,
  type: GammaBootstrapApiKeyType,
): { id: string; token: string } {
  const credentials = gamma.apiKeys?.[type];
  if (!credentials) {
    throw new Error(`Bootstrap ${type} api key credentials were not captured`);
  }

  return credentials;
}

function makeAuthHeader(credentials: { id: string; token: string }): string {
  return `pre-shared ${credentials.id}:${credentials.token}`;
}
