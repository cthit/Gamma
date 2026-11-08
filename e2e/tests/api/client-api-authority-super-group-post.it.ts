import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import type { Page } from "@playwright/test";
import { login } from "../../helpers/auth";
import { createUserClientWithApiKeyViaUi } from "../../helpers/client-api";
import { uniqueLabel } from "../../helpers/strings";
import { articleByHeading } from "../../helpers/a11y";

const PRIT_ID = "b3bcbbcc-0b93-4c41-a3c7-1792448c6fc1";
const CHAIRMAN_ID = "7bb1db15-730d-4864-bfc3-99abe7c0ccf8";
const TREASURER_ID = "844067b3-e95d-4a28-a586-7388f155b8fb";
// Chairman of P.R.I.T., treasurer of styrIT.
const JHALPERT_ID = "bc605869-9a4d-46ec-8a29-d00819d4c195";
// Treasurer of P.R.I.T.
const PBEESLY_ID = "ec8987d7-4087-461d-bed5-9365086b6e3b";

test("an authority given to a post in a super group only applies to the members holding that post there", async ({
  page,
  request,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  const client = await createUserClientWithApiKeyViaUi(
    page,
    gamma.url,
    uniqueLabel("Post authority"),
  );
  await createPostAuthority(page, "pritchair", "P.R.I.T.", "Chairman");
  await createPostAuthority(page, "prittreasurer", "P.R.I.T.", "Treasurer");

  await expect(
    articleByHeading(page, "pritchair").getByRole("listitem"),
  ).toHaveText(["Chairman in P.R.I.T."]);

  const options = {
    headers: {
      Authorization: `pre-shared ${client.apiKeyId}:${client.apiKeyToken}`,
    },
    maxRedirects: 0,
  };

  const authority = await request.get(
    `${gamma.url}/api/client/v1/authorities/prittreasurer`,
    options,
  );
  expect(authority.status()).toBe(200);
  expect(await authority.json()).toMatchObject({
    name: "prittreasurer",
    superGroups: [],
    superGroupPosts: [
      {
        superGroup: { id: PRIT_ID, prettyName: "P.R.I.T." },
        post: { id: TREASURER_ID, enName: "Treasurer" },
      },
    ],
    users: [],
  });

  const chair = await request.get(
    `${gamma.url}/api/client/v1/authorities/pritchair`,
    options,
  );
  expect(await chair.json()).toMatchObject({
    superGroupPosts: [{ post: { id: CHAIRMAN_ID } }],
  });

  for (const [userId, expected] of [
    [JHALPERT_ID, ["pritchair"]],
    [PBEESLY_ID, ["prittreasurer"]],
  ] as const) {
    const response = await request.get(
      `${gamma.url}/api/client/v1/authorities/for/${userId}`,
      options,
    );
    expect(response.status()).toBe(200);
    expect(await response.json()).toEqual(expected);
  }
});

async function createPostAuthority(
  page: Page,
  name: string,
  superGroup: string,
  post: string,
): Promise<void> {
  const authorityForm = articleByHeading(page, "Create client authority");
  await authorityForm.getByLabel("Authority name", { exact: true }).fill(name);

  await authorityForm
    .getByRole("button", { name: "Add post authority" })
    .click();
  await authorityForm
    .getByRole("combobox", { name: "Super group of post", exact: true })
    .selectOption({ label: superGroup });
  await authorityForm
    .getByRole("combobox", { name: "Post", exact: true })
    .selectOption({ label: post });

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/authority") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    authorityForm.getByRole("button", { name: "Save", exact: true }).click(),
  ]);

  await expect(
    page.getByRole("heading", { level: 2, name, exact: true }),
  ).toBeVisible();
}
