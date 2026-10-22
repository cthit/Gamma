import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import type { Page } from "@playwright/test";
import { login } from "../../helpers/auth";
import { createUserClientWithApiKeyViaUi } from "../../helpers/client-api";
import { authorizeClientWithPkce } from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";

const DIGIT = {
  enDescription: "",
  id: "aed27030-ad90-4526-855c-1e909b1dcecb",
  name: "digit",
  prettyName: "digIT",
  svDescription: "",
  type: "committee",
};

const JHALPERT = {
  acceptanceYear: 2002,
  cid: "jhalpert",
  firstName: "Jim",
  id: "bc605869-9a4d-46ec-8a29-d00819d4c195",
  lastName: "Halpert",
  nick: "Big Tuna",
};

const NOT_FOUND = {
  error: "Not Found",
  message: "Authority Not Found",
  status: 404,
};

test("client API exposes the super groups and approving users of the calling client's own authorities", async ({
  page,
  request,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  const owner = await createUserClientWithApiKeyViaUi(
    page,
    gamma.url,
    uniqueLabel("Authority owner"),
  );
  await createAuthority(page, "booking", {
    superGroup: "digIT",
    user: "Big Tuna",
  });

  const other = await createUserClientWithApiKeyViaUi(
    page,
    gamma.url,
    uniqueLabel("Authority other"),
  );

  const ownerOptions = {
    headers: {
      Authorization: `pre-shared ${owner.apiKeyId}:${owner.apiKeyToken}`,
    },
    maxRedirects: 0,
  };
  const otherOptions = {
    headers: {
      Authorization: `pre-shared ${other.apiKeyId}:${other.apiKeyToken}`,
    },
    maxRedirects: 0,
  };

  const authorityUrl = `${gamma.url}/api/client/v1/authorities/booking`;

  // Like the users endpoint, only users who approved the client are visible.
  const beforeApproval = await request.get(authorityUrl, ownerOptions);
  expect(beforeApproval.status()).toBe(200);
  expect(await beforeApproval.json()).toEqual({
    name: "booking",
    superGroups: [DIGIT],
    users: [],
  });

  await authorizeClientWithPkce(
    page,
    gamma.url,
    owner.clientId,
    owner.redirectUri,
    { consent: "required" },
  );
  const afterApproval = await request.get(authorityUrl, ownerOptions);
  expect(afterApproval.status()).toBe(200);
  expect(await afterApproval.json()).toEqual({
    name: "booking",
    superGroups: [DIGIT],
    users: [JHALPERT],
  });

  // Unknown and invalid names, and another client's authority, are all not found.
  for (const [name, options] of [
    ["missing", ownerOptions],
    ["Not-Valid", ownerOptions],
    ["booking", otherOptions],
  ] as const) {
    const response = await request.get(
      `${gamma.url}/api/client/v1/authorities/${name}`,
      options,
    );
    expect(response.status()).toBe(404);
    expect(await response.json()).toEqual(NOT_FOUND);
  }

  const unauthenticated = await request.get(authorityUrl, {
    maxRedirects: 0,
  });
  expect(unauthenticated.status()).toBe(401);
});

async function createAuthority(
  page: Page,
  name: string,
  members: { superGroup: string; user: string },
): Promise<void> {
  await page.fill('input[name="authority"]', name);

  await page.getByRole("button", { name: "Add super group authority" }).click();
  await page
    .locator("select.authority-super-group-row")
    .selectOption({ label: members.superGroup });

  await page.getByRole("button", { name: "Add user authority" }).click();
  await page
    .locator("select.authority-user-row")
    .selectOption({ label: members.user });

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/authority") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.locator('button[form="create-client-authority"]').click(),
  ]);

  await expect(
    page.locator("article > header", { hasText: name }),
  ).toBeVisible();
}
