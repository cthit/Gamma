import { expect, test } from "@playwright/test";
import { createApiKeyViaUi, readApiKeyCredentials } from "../../helpers/api-keys";
import { login } from "../../helpers/auth";
import { startDefaultGamma } from "../../helpers/gamma";
import { uniqueLabel } from "../../helpers/strings";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../../gamma-setup";

test("given an api key when resetting its token then old token is rejected and new token is accepted", async ({
  page,
  request,
}) => {
  const env = await startDependencies();
  const gamma = await startDefaultGamma(env);

  try {
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

    const resetCredentials = await readApiKeyCredentials(page);

    expect(resetCredentials.apiKeyToken).not.toEqual(
      originalCredentials.apiKeyToken,
    );

    const oldTokenResponse = await request.get(
      `${gamma.url}/api/info/v1/blob`,
      {
        headers: {
          Authorization: `pre-shared ${originalCredentials.apiKeyId}:${originalCredentials.apiKeyToken}`,
        },
      },
    );
    expect(oldTokenResponse.status()).toBe(401);

    const newTokenResponse = await request.get(
      `${gamma.url}/api/info/v1/blob`,
      {
        headers: {
          Authorization: `pre-shared ${resetCredentials.apiKeyId}:${resetCredentials.apiKeyToken}`,
        },
      },
    );
    expect(newTokenResponse.ok()).toBe(true);
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
