import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
  exchangeCode,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";

test("another user cannot read, reset, delete, or change authorities on another user's client", async ({
  page,
  browser,
  request,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    uniqueLabel("Private client"),
  );
  const authorityName = "existing";
  await page.fill('input[name="authority"]', authorityName);
  await page.locator('button[form="create-client-authority"]').click();
  await expect(
    page.locator("article > header", { hasText: authorityName }),
  ).toBeVisible();

  const otherContext = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const other = await otherContext.newPage();
    await login(other, gamma.url, "pbeesly", "password1337", "Pam-Pam");
    await expect(
      other.getByRole("link", { name: "Admins", exact: true }),
    ).toHaveCount(0);
    await other.goto(`${gamma.url}/my-clients/create`);
    const csrf = await other
      .locator('#create-my-client input[name="_csrf"]')
      .inputValue();
    // Use the attacker's valid CSRF token so denial must come from authorization.
    // A forged owner header must not grant permission either.
    const headers = { "HX-Request": "true", owner: "true" };
    const operations = [
      { path: `/clients/${client.clientUid}`, method: "GET", form: {} },
      { path: `/clients/${client.clientUid}/reset`, method: "POST", form: {} },
      {
        path: `/clients/${client.clientUid}/authority`,
        method: "POST",
        form: { authority: "intruder" },
      },
      {
        path: `/clients/${client.clientUid}/authority/${authorityName}`,
        method: "POST",
        form: { _method: "delete" },
      },
      {
        path: `/clients/${client.clientUid}`,
        method: "POST",
        form: { _method: "delete" },
      },
    ];
    for (const operation of operations) {
      await test.step(`${operation.method} ${operation.path}`, async () => {
        const response = await other.request.fetch(
          `${gamma.url}${operation.path}`,
          {
            method: operation.method,
            headers,
            maxRedirects: 0,
            ...(operation.method === "POST"
              ? { form: { _csrf: csrf, ...operation.form } }
              : {}),
          },
        );
        // Gamma currently renders its access-denied page with HTTP 200.
        expect(response.status()).toBe(200);
        const body = await response.text();
        expect(body).toContain("403 - Unauthorized");
        expect(body).not.toContain(client.clientSecret);
        expect(body).not.toContain("Reset client secret");
      });
    }
  } finally {
    await otherContext.close();
  }

  await page.goto(`${gamma.url}/clients/${client.clientUid}`);
  await expect(page.getByText("Client details", { exact: true })).toBeVisible();
  await expect(
    page.locator("article > header", { hasText: authorityName }),
  ).toBeVisible();
  await expect(
    page.locator("article > header", { hasText: "intruder" }),
  ).toHaveCount(0);
  // The original secret still authenticates, proving a denied reset did not mutate it.
  const grant = await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "required" },
  );
  expect((await exchangeCode(request, gamma.url, client, grant)).status()).toBe(
    200,
  );
});
