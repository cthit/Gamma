import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import {
  createApiKeyViaUi,
  readApiKeyCredentials,
} from "../../helpers/api-keys";
import { login } from "../../helpers/auth";
import { uniqueLabel } from "../../helpers/strings";
import type { GammaEnvironment } from "../../gamma-setup";
import type { Page } from "@playwright/test";

test("an admin can create configure rotate and delete an api key", async ({
  page,
  request,
  gamma,
  env,
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

  await saveSettings(page, gamma.url, resetCredentials.apiKeyId, "info", {
    version: "0",
    "superGroupTypes[0].type": "committee",
    "superGroupTypes[1].type": "society",
  });
  expect(await storedTypes(env, resetCredentials.apiKeyId)).toEqual([
    "committee|f",
    "society|f",
  ]);

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/api-keys", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete" }).click(),
  ]);
  await expect(page.getByText(prettyName)).toHaveCount(0);
});

test("account scaffold settings keep managed flags aligned and clear removed flags", async ({
  page,
  gamma,
  env,
}) => {
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
  await saveSettings(
    page,
    gamma.url,
    credentials.apiKeyId,
    "account-scaffold",
    {
      version: "0",
      "superGroupTypes[0].type": "committee",
      "superGroupTypes[0].requiresManaged": "on",
      "superGroupTypes[1].type": "society",
    },
  );
  expect(await storedTypes(env, credentials.apiKeyId)).toEqual([
    "committee|t",
    "society|f",
  ]);
  await saveSettings(
    page,
    gamma.url,
    credentials.apiKeyId,
    "account-scaffold",
    {
      version: "1",
      "superGroupTypes[0].type": "society",
      "superGroupTypes[0].requiresManaged": "on",
      "superGroupTypes[1].type": "committee",
    },
  );
  expect(await storedTypes(env, credentials.apiKeyId)).toEqual([
    "committee|f",
    "society|t",
  ]);
});

// These settings are exposed through HTTP endpoints; the current details page has no settings editor.
async function saveSettings(
  page: Page,
  baseUrl: string,
  keyId: string,
  kind: string,
  fields: Record<string, string>,
) {
  await page.goto(`${baseUrl}/api-keys/${keyId}`);
  const csrf = await page.locator('input[name="_csrf"]').first().inputValue();
  const response = await page.request.post(
    `${baseUrl}/api-keys/${keyId}/${kind}-settings`,
    {
      form: { ...fields, _method: "put", _csrf: csrf },
      maxRedirects: 0,
    },
  );
  expect(response.status()).toBe(302);
}

async function storedTypes(
  env: GammaEnvironment,
  keyId: string,
): Promise<string[]> {
  if (!/^[0-9a-f-]{36}$/.test(keyId))
    throw new Error("Expected a fixture key UUID");
  const result = await env.postgres.exec([
    "psql",
    "--username",
    "postgres",
    "--dbname",
    env.databaseName,
    "-At",
    "--command",
    `SELECT types.super_group_type_name, managed.settings_id IS NOT NULL
       FROM g_api_key_settings settings
       JOIN g_api_key_to_super_group_type types USING (settings_id)
       LEFT JOIN g_api_key_account_scaffold_requires_managed managed
         ON managed.settings_id = settings.settings_id AND managed.super_group_type_name = types.super_group_type_name
       WHERE settings.api_key_id = '${keyId}' ORDER BY types.super_group_type_name`,
  ]);
  expect(result.exitCode).toBe(0);
  return result.output.trim().split("\n");
}
