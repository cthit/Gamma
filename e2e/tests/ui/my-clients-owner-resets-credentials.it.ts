import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import type { APIRequestContext } from "@playwright/test";
import { readApiKeyCredentials } from "../../helpers/api-keys";
import { login } from "../../helpers/auth";
import { createUserClientWithApiKeyViaUi } from "../../helpers/client-api";
import { authorizeClientWithPkce, exchangeCode } from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";
import { articleByHeading, csrfTokenOf } from "../../helpers/a11y";

// jhalpert and pbeesly are non-admin users in the mock data.
const OWNER = { cid: "jhalpert", nick: "Big Tuna" };
const OTHER = { cid: "pbeesly", nick: "Pam-Pam" };

async function clientApiStatus(
  request: APIRequestContext,
  baseUrl: string,
  apiKeyId: string,
  apiKeyToken: string,
): Promise<number> {
  const response = await request.get(`${baseUrl}/api/client/v1/users`, {
    headers: { Authorization: `pre-shared ${apiKeyId}:${apiKeyToken}` },
  });
  return response.status();
}

test("a non-admin owner can reset the secret of a client that has an api key", async ({
  page,
  request,
  gamma,
}) => {
  await login(page, gamma.url, OWNER.cid, "password1337", OWNER.nick);
  const client = await createUserClientWithApiKeyViaUi(
    page,
    gamma.url,
    uniqueLabel("Owner reset secret"),
  );

  await page.goto(`${gamma.url}/clients/${client.clientUid}`);
  page.once("dialog", (dialog) => dialog.accept());
  const [resetResponse] = await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().endsWith(`/clients/${client.clientUid}/reset`),
    ),
    page.getByRole("button", { name: "Reset client secret" }).click(),
  ]);
  expect(resetResponse.status()).toBe(200);

  const secret = articleByHeading(page, "Credentials")
    .getByRole("code")
    .first();
  await expect(secret).toBeVisible();
  const newSecret = (await secret.innerText()).trim();
  expect(newSecret.length).toBeGreaterThan(0);
  expect(newSecret).not.toBe(client.clientSecret);

  const oldGrant = await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "required" },
  );
  const oldResponse = await exchangeCode(request, gamma.url, client, oldGrant);
  expect(oldResponse.status()).toBe(401);

  const newGrant = await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "none" },
  );
  const newResponse = await exchangeCode(
    request,
    gamma.url,
    { ...client, clientSecret: newSecret },
    newGrant,
  );
  expect(newResponse.status()).toBe(200);

  // Resetting the client secret must leave the client's api key untouched.
  expect(
    await clientApiStatus(
      request,
      gamma.url,
      client.apiKeyId,
      client.apiKeyToken,
    ),
  ).toBe(200);
});

test("a non-admin owner can open and reset the api key of their client", async ({
  page,
  request,
  gamma,
}) => {
  await login(page, gamma.url, OWNER.cid, "password1337", OWNER.nick);
  const client = await createUserClientWithApiKeyViaUi(
    page,
    gamma.url,
    uniqueLabel("Owner reset api key"),
  );
  expect(
    await clientApiStatus(
      request,
      gamma.url,
      client.apiKeyId,
      client.apiKeyToken,
    ),
  ).toBe(200);

  await page.goto(`${gamma.url}/clients/${client.clientUid}`);
  await Promise.all([
    page.waitForURL(`**/api-keys/${client.apiKeyId}`),
    page.getByRole("link", { name: "More details" }).click(),
  ]);
  await expect(page.getByText("Api key details")).toBeVisible();
  await expect(page.getByText("403 - Unauthorized")).toHaveCount(0);
  // Deleting api keys stays admin only, so the owner is not offered it.
  await expect(page.getByRole("button", { name: "Delete" })).toHaveCount(0);

  page.once("dialog", (dialog) => dialog.accept());
  const [resetResponse] = await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().endsWith(`/api-keys/${client.apiKeyId}/reset`),
    ),
    page.getByRole("button", { name: "Reset token" }).click(),
  ]);
  expect(resetResponse.status()).toBe(200);
  await expect(
    articleByHeading(page, "Credentials").getByRole("code").first(),
  ).toBeVisible();

  const reset = await readApiKeyCredentials(page);
  expect(reset.apiKeyId).toBe(client.apiKeyId);
  expect(reset.apiKeyToken).not.toBe(client.apiKeyToken);

  expect(
    await clientApiStatus(
      request,
      gamma.url,
      client.apiKeyId,
      client.apiKeyToken,
    ),
  ).toBe(401);
  expect(
    await clientApiStatus(
      request,
      gamma.url,
      reset.apiKeyId,
      reset.apiKeyToken,
    ),
  ).toBe(200);
});

test("another non-admin user cannot open or reset the api key of someone else's client", async ({
  page,
  browser,
  request,
  gamma,
}) => {
  await login(page, gamma.url, OWNER.cid, "password1337", OWNER.nick);
  const client = await createUserClientWithApiKeyViaUi(
    page,
    gamma.url,
    uniqueLabel("Private api key"),
  );

  const otherContext = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const other = await otherContext.newPage();
    await login(other, gamma.url, OTHER.cid, "password1337", OTHER.nick);

    await other.goto(`${gamma.url}/api-keys/${client.apiKeyId}`);
    await expect(other.getByText("403 - Unauthorized")).toBeVisible();
    await expect(other.getByText("Api key details")).toHaveCount(0);

    await other.goto(`${gamma.url}/my-clients/create`);
    const csrf = await csrfTokenOf(
      other.getByLabel("Pretty name", { exact: true }),
    );
    // Use the other user's valid CSRF token so denial must come from authorization.
    const response = await other.request.post(
      `${gamma.url}/api-keys/${client.apiKeyId}/reset`,
      {
        headers: { "HX-Request": "true" },
        form: { _csrf: csrf },
        maxRedirects: 0,
      },
    );
    // Gamma currently renders its access-denied page with HTTP 200.
    expect(response.status()).toBe(200);
    const body = await response.text();
    expect(body).toContain("403 - Unauthorized");
    expect(body).not.toContain("Reset token");
  } finally {
    await otherContext.close();
  }

  // The denied reset must not have rotated the owner's token.
  expect(
    await clientApiStatus(
      request,
      gamma.url,
      client.apiKeyId,
      client.apiKeyToken,
    ),
  ).toBe(200);
});
