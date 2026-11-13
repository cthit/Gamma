import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import type { Locator, Page } from "@playwright/test";
import { login } from "../../helpers/auth";
import { createUserClientWithApiKeyViaUi } from "../../helpers/client-api";
import { uniqueLabel } from "../../helpers/strings";
import { articleByHeading } from "../../helpers/a11y";

const DIGIT_ID = "aed27030-ad90-4526-855c-1e909b1dcecb";
const PRIT_ID = "b3bcbbcc-0b93-4c41-a3c7-1792448c6fc1";
const CHAIRMAN_ID = "7bb1db15-730d-4864-bfc3-99abe7c0ccf8";
const JHALPERT_ID = "bc605869-9a4d-46ec-8a29-d00819d4c195";
const PBEESLY_ID = "ec8987d7-4087-461d-bed5-9365086b6e3b";

test("the owner of a client can change who has an authority without recreating it", async ({
  page,
  request,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  const client = await createUserClientWithApiKeyViaUi(
    page,
    gamma.url,
    uniqueLabel("Edit authority"),
  );

  const createForm = articleByHeading(page, "Create client authority");
  await createForm
    .getByLabel("Authority name", { exact: true })
    .fill("editable");
  await createForm
    .getByRole("button", { name: "Add super group authority" })
    .click();
  await createForm
    .getByRole("combobox", { name: "Super group", exact: true })
    .selectOption({ label: "digIT" });
  await createForm.getByRole("button", { name: "Add user authority" }).click();
  await createForm
    .getByRole("combobox", { name: "User", exact: true })
    .selectOption({ label: "Pam-Pam" });
  await saveAndWait(page, createForm, "/authority");

  const authority = articleByHeading(page, "editable");
  await expect(authority.getByRole("listitem")).toHaveText([
    "digIT",
    "Pam-Pam",
  ]);

  // Cancelling leaves the authority as it was.
  await authority.getByRole("button", { name: "Edit", exact: true }).click();
  const editForm = articleByHeading(page, "Edit editable");
  await expect(
    editForm.getByRole("combobox", { name: "Super group", exact: true }),
  ).toHaveValue(DIGIT_ID);
  await expect(
    editForm.getByRole("combobox", { name: "User", exact: true }),
  ).toHaveValue(PBEESLY_ID);
  await editForm.getByRole("button", { name: "Cancel", exact: true }).click();
  await expect(authority.getByRole("listitem")).toHaveText([
    "digIT",
    "Pam-Pam",
  ]);

  // Swap the super group for a post, and add a user.
  await authority.getByRole("button", { name: "Edit", exact: true }).click();
  await editForm
    .getByRole("button", { name: "Delete super group", exact: true })
    .click();
  await editForm.getByRole("button", { name: "Add post authority" }).click();
  await editForm
    .getByRole("combobox", { name: "Super group of post", exact: true })
    .selectOption({ label: "P.R.I.T." });
  await editForm
    .getByRole("combobox", { name: "Post", exact: true })
    .selectOption({ label: "Chairman" });
  await editForm.getByRole("button", { name: "Add user authority" }).click();
  const userSelects = editForm.getByRole("combobox", {
    name: "User",
    exact: true,
  });
  // Wait for the new row, so that the existing one is not changed instead.
  await expect(userSelects).toHaveCount(2);
  await userSelects.last().selectOption({ label: "Big Tuna" });
  await saveAndWait(page, editForm, "/authority/editable");

  await expect(authority.getByText("No super groups")).toBeVisible();
  await expect(authority.getByRole("listitem")).toHaveText([
    "Chairman in P.R.I.T.",
    "Big Tuna",
    "Pam-Pam",
  ]);

  const options = {
    headers: {
      Authorization: `pre-shared ${client.apiKeyId}:${client.apiKeyToken}`,
    },
    maxRedirects: 0,
  };
  const details = await request.get(
    `${gamma.url}/api/client/v1/authorities/editable`,
    options,
  );
  expect(details.status()).toBe(200);
  expect(await details.json()).toMatchObject({
    superGroups: [],
    superGroupPosts: [
      { superGroup: { id: PRIT_ID }, post: { id: CHAIRMAN_ID } },
    ],
  });
  // The users are not listed above since they have not approved the client.
  for (const userId of [JHALPERT_ID, PBEESLY_ID]) {
    const response = await request.get(
      `${gamma.url}/api/client/v1/authorities/for/${userId}`,
      options,
    );
    expect(await response.json()).toEqual(["editable"]);
  }

  // Removing every row leaves the authority without members.
  await page.reload();
  await authority.getByRole("button", { name: "Edit", exact: true }).click();
  await editForm
    .getByRole("button", { name: "Delete post", exact: true })
    .click();
  await editForm
    .getByRole("button", { name: "Delete user", exact: true })
    .first()
    .click();
  await editForm
    .getByRole("button", { name: "Delete user", exact: true })
    .click();
  await saveAndWait(page, editForm, "/authority/editable");

  await expect(authority.getByRole("listitem")).toHaveCount(0);
  for (const userId of [JHALPERT_ID, PBEESLY_ID]) {
    const response = await request.get(
      `${gamma.url}/api/client/v1/authorities/for/${userId}`,
      options,
    );
    expect(await response.json()).toEqual([]);
  }
});

/** Saves the form of the article and waits for the response to `path`. */
async function saveAndWait(
  page: Page,
  article: Locator,
  path: string,
): Promise<void> {
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        new URL(response.url()).pathname.endsWith(path) &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    article.getByRole("button", { name: "Save", exact: true }).click(),
  ]);
}
