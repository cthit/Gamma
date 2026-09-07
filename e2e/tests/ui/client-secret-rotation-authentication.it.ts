import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import {
  createUserClientViaUi,
  authorizeClientWithPkce,
  exchangeCode,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";

for (const official of [false, true]) {
  test(`rotating a ${official ? "official client's secret as admin" : "user client's secret as owner"} invalidates the old secret`, async ({
    page,
    request,
    gamma,
  }) => {
    await login(
      page,
      gamma.url,
      official ? gamma.adminCid! : "pbeesly",
      official ? gamma.adminPassword! : "password1337",
    );
    const client = await createUserClientViaUi(
      page,
      gamma.url,
      uniqueLabel("Rotate secret"),
      { official },
    );
    const initial = await authorizeClientWithPkce(
      page,
      gamma.url,
      client.clientId,
      client.redirectUri,
      { consent: "required" },
    );
    expect(
      (await exchangeCode(request, gamma.url, client, initial)).status(),
    ).toBe(200);
    await page.goto(`${gamma.url}/clients/${client.clientUid}`);
    page.once("dialog", (dialog) => dialog.accept());
    await page.getByRole("button", { name: "Reset client secret" }).click();
    const secret = page.locator('article:has-text("Credentials") code').first();
    await expect(secret).toBeVisible();
    const newSecret = (await secret.innerText()).trim();
    expect(newSecret).not.toBe(client.clientSecret);
    const grant = await authorizeClientWithPkce(
      page,
      gamma.url,
      client.clientId,
      client.redirectUri,
      { consent: "none" },
    );
    const oldResponse = await exchangeCode(request, gamma.url, client, grant);
    expect(oldResponse.status()).toBe(401);
    expect(((await oldResponse.json()) as { error: string }).error).toBe(
      "invalid_client",
    );
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
  });
}
