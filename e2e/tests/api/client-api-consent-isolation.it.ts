import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { createUserClientWithApiKeyViaUi } from "../../helpers/client-api";
import { authorizeClientWithPkce } from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";

const OWNER_ID = "bc605869-9a4d-46ec-8a29-d00819d4c195";
const OTHER_ID = "ec8987d7-4087-461d-bed5-9365086b6e3b";

test("client API exposes only approving users and withdraws access when approval is retracted", async ({
  page,
  request,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const name = uniqueLabel("Consent isolation");
  const client = await createUserClientWithApiKeyViaUi(page, gamma.url, name);
  const options = {
    headers: {
      Authorization: `pre-shared ${client.apiKeyId}:${client.apiKeyToken}`,
    },
    maxRedirects: 0,
  };
  for (const id of [OWNER_ID, OTHER_ID]) {
    const response = await request.get(
      `${gamma.url}/api/client/v1/users/${id}`,
      options,
    );
    expect(response.status()).toBe(404);
    expect(await response.json()).toEqual({
      error: "Not Found",
      message: "User Not Found Or Unauthorized",
      status: 404,
    });
  }
  const before = await request.get(`${gamma.url}/api/client/v1/users`, options);
  expect(before.status()).toBe(200);
  expect(await before.json()).toEqual([]);

  await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "required" },
  );
  const allowed = await request.get(
    `${gamma.url}/api/client/v1/users/${OWNER_ID}`,
    options,
  );
  expect(allowed.status()).toBe(200);
  expect(await allowed.json()).toEqual({
    id: OWNER_ID,
    cid: "jhalpert",
    nick: "Big Tuna",
    firstName: "Jim",
    lastName: "Halpert",
    acceptanceYear: 2002,
  });
  const list = await request.get(`${gamma.url}/api/client/v1/users`, options);
  expect(list.status()).toBe(200);
  expect(await list.json()).toEqual([await allowed.json()]);
  for (const path of [`users/${OTHER_ID}`, `groups/for/${OTHER_ID}`]) {
    const denied = await request.get(
      `${gamma.url}/api/client/v1/${path}`,
      options,
    );
    expect(denied.status()).toBe(404);
    expect(await denied.json()).toEqual({
      error: "Not Found",
      message: "User Not Found Or Unauthorized",
      status: 404,
    });
  }

  await page.goto(`${gamma.url}/me/accepted-clients`);
  await page
    .locator("tr", { hasText: name })
    .getByRole("button", { name: "Retract approval" })
    .click();
  await expect(page.locator("tr", { hasText: name })).toHaveCount(0);
  expect(
    (
      await request.get(`${gamma.url}/api/client/v1/users/${OWNER_ID}`, options)
    ).status(),
  ).toBe(404);
  expect(
    await (
      await request.get(`${gamma.url}/api/client/v1/users`, options)
    ).json(),
  ).toEqual([]);
});
