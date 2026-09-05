import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import {
  createApiKeyViaUi,
  readApiKeyCredentials,
} from "../../helpers/api-keys";
import { login } from "../../helpers/auth";
import { uniqueLabel } from "../../helpers/strings";
import { getGammaE2ERuntime } from "../../gamma-setup";

test("an admin can create configure rotate and delete an api key", async ({
  page,
  request,
  gamma,
}) => {
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const prettyName = uniqueLabel("E2E Reset Key");
  const originalCredentials = await createApiKeyViaUi(page, gamma.url, {
    prettyName,
    svDescription: "E2E svensk beskrivning",
    enDescription: "E2E english description",
    keyType: "INFO",
  });

  const initialResponse = await request.get(`${gamma.url}/api/info/v1/blob`, {
    headers: {
      Authorization: `pre-shared ${originalCredentials.apiKeyId}:${originalCredentials.apiKeyToken}`,
    },
  });
  expect(initialResponse.ok()).toBe(true);

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response
          .url()
          .includes(`/api-keys/${originalCredentials.apiKeyId}/reset`) &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.getByRole("button", { name: "Reset token" }).click(),
  ]);

  await expect(
    page.locator('article:has-text("Credentials") code').first(),
  ).not.toHaveText(originalCredentials.apiKeyToken, { timeout: 10000 });
  const resetCredentials = await readApiKeyCredentials(page);

  expect(resetCredentials.apiKeyToken).not.toEqual(
    originalCredentials.apiKeyToken,
  );

  const oldTokenResponse = await request.get(`${gamma.url}/api/info/v1/blob`, {
    headers: {
      Authorization: `pre-shared ${originalCredentials.apiKeyId}:${originalCredentials.apiKeyToken}`,
    },
  });
  expect(oldTokenResponse.status()).toBe(401);

  const newTokenResponse = await request.get(`${gamma.url}/api/info/v1/blob`, {
    headers: {
      Authorization: `pre-shared ${resetCredentials.apiKeyId}:${resetCredentials.apiKeyToken}`,
    },
  });
  expect(newTokenResponse.ok()).toBe(true);

  if (getGammaE2ERuntime() === "kotlin") {
    await page.goto(`${gamma.url}/api-keys/${resetCredentials.apiKeyId}`);
    const settingsForm = page.locator(
      `form[action="/api-keys/${resetCredentials.apiKeyId}/info-settings"]`,
    );
    await settingsForm.getByRole("button", { name: "Add type" }).click();
    await settingsForm.getByRole("button", { name: "Add type" }).click();
    const typeSelectors = settingsForm.locator("select.type");
    await expect(typeSelectors).toHaveCount(2);
    await typeSelectors.nth(0).selectOption("committee");
    await typeSelectors.nth(1).selectOption("society");
    expect(await settingsEntries(settingsForm)).toEqual([
      ["superGroupTypes[0].type", "committee"],
      ["superGroupTypes[1].type", "society"],
    ]);
    await Promise.all([
      page.waitForResponse(
        (response) =>
          response.request().method() === "POST" &&
          response
            .url()
            .endsWith(`/api-keys/${resetCredentials.apiKeyId}/info-settings`) &&
          response.status() === 200,
      ),
      settingsForm.getByRole("button", { name: "Save" }).click(),
    ]);
    await expect(
      settingsForm.locator(".super-group-type select").nth(0),
    ).toHaveValue("committee");
    await expect(
      settingsForm.locator(".super-group-type select").nth(1),
    ).toHaveValue("society");

    await expect(settingsForm).toHaveCount(2);
    const retainedSettingsForm = settingsForm.last();
    // Released Gamma rendered retained selections as disabled options, so an untouched
    // form omitted them. Its default inner swap also leaves both form roots in the DOM.
    // Keep both behaviors visible until a compatibility change is explicitly approved.
    expect(await settingsEntries(retainedSettingsForm)).toEqual([]);
  }

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/api-keys", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete" }).click(),
  ]);
  await expect(page.getByText(prettyName)).toHaveCount(0);
});

test("account scaffold form keeps managed flags aligned with added types", async ({
  page,
  gamma,
}) => {
  if (getGammaE2ERuntime() !== "kotlin") return;

  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );
  const credentials = await createApiKeyViaUi(page, gamma.url, {
    prettyName: uniqueLabel("E2E Scaffold Key"),
    svDescription: "",
    enDescription: "",
    keyType: "ACCOUNT_SCAFFOLD",
  });
  await page.goto(`${gamma.url}/api-keys/${credentials.apiKeyId}`);
  const settingsForm = page.locator(
    `form[action="/api-keys/${credentials.apiKeyId}/account-scaffold-settings"]`,
  );
  await settingsForm.getByRole("button", { name: "Add type" }).click();
  await settingsForm.getByRole("button", { name: "Add type" }).click();
  const types = settingsForm.locator("select.type");
  const managed = settingsForm.locator("input.requiresManaged");
  await types.nth(0).selectOption("committee");
  await types.nth(1).selectOption("society");
  await managed.nth(0).check();
  expect(await settingsEntries(settingsForm)).toEqual([
    ["superGroupTypes[0].type", "committee"],
    ["superGroupTypes[0].requiresManaged", "on"],
    ["superGroupTypes[1].type", "society"],
  ]);

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response
          .url()
          .endsWith(
            `/api-keys/${credentials.apiKeyId}/account-scaffold-settings`,
          ) &&
        response.status() === 200,
    ),
    settingsForm.getByRole("button", { name: "Save" }).click(),
  ]);
  await expect(settingsForm).toHaveCount(2);
  await expect(
    settingsForm.last().locator(".super-group-type select"),
  ).toHaveCount(2);
  expect(await settingsEntries(settingsForm.last())).toEqual([
    ["superGroupTypes[0].requiresManaged", "on"],
  ]);

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/api-keys", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete" }).click(),
  ]);
});

async function settingsEntries(form: import("@playwright/test").Locator) {
  return form.evaluate((element) => {
    const BrowserFormData = FormData as unknown as new (
      form: unknown,
    ) => FormData;
    return Array.from(new BrowserFormData(element).entries())
      .filter(([name]) => name.startsWith("superGroupTypes"))
      .map(([name, value]) => {
        if (typeof value !== "string") {
          throw new Error("Settings forms must not submit file values");
        }
        return [name, value];
      });
  });
}
