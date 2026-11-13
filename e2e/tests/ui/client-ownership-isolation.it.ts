import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
  exchangeCode,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";
import { articleByHeading, csrfTokenOf } from "../../helpers/a11y";

const PBEESLY_ID = "ec8987d7-4087-461d-bed5-9365086b6e3b";

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
  await page.getByLabel("Authority name", { exact: true }).fill(authorityName);
  await articleByHeading(page, "Create client authority")
    .getByRole("button", { name: "Save", exact: true })
    .click();
  await expect(
    page.getByRole("heading", { level: 2, name: authorityName, exact: true }),
  ).toBeVisible();

  const otherContext = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const other = await otherContext.newPage();
    await login(other, gamma.url, "pbeesly", "password1337", "Pam-Pam");
    await expect(
      other.getByRole("link", { name: "Admins", exact: true }),
    ).toHaveCount(0);
    await other.goto(`${gamma.url}/my-clients/create`);
    const csrf = await csrfTokenOf(
      other.getByLabel("Pretty name", { exact: true }),
    );
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
        path: `/clients/${client.clientUid}/authority/${authorityName}/edit`,
        method: "GET",
        form: {},
      },
      {
        path: `/clients/${client.clientUid}/authority/${authorityName}`,
        method: "POST",
        form: { _method: "put", "users[0]": PBEESLY_ID },
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
    page.getByRole("heading", { level: 2, name: authorityName, exact: true }),
  ).toBeVisible();
  await expect(
    page.getByRole("heading", { level: 2, name: "intruder", exact: true }),
  ).toHaveCount(0);
  await expect(
    articleByHeading(page, authorityName).getByText("No users", {
      exact: true,
    }),
  ).toBeVisible();
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
