import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
  exchangeCode,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";

test("given an approved client when retracting approval then it is removed from accepted clients", async ({
  page,
  gamma,
  request,
}) => {
  await login(page, gamma.url, "mscott", "password1337", "Boss");

  const prettyName = uniqueLabel("E2E Approval Client");
  const createdClient = await createUserClientViaUi(
    page,
    gamma.url,
    prettyName,
  );

  const firstGrant = await authorizeClientWithPkce(
    page,
    gamma.url,
    createdClient.clientId,
    createdClient.redirectUri,
    { consent: "required" },
  );
  expect(
    (
      await exchangeCode(request, gamma.url, createdClient, firstGrant)
    ).status(),
  ).toBe(200);
  const repeatGrant = await authorizeClientWithPkce(
    page,
    gamma.url,
    createdClient.clientId,
    createdClient.redirectUri,
    { consent: "none" },
  );
  expect(
    (
      await exchangeCode(request, gamma.url, createdClient, repeatGrant)
    ).status(),
  ).toBe(200);

  await page.goto(`${gamma.url}/me/accepted-clients`, { timeout: 30000 });

  const approvedRow = page.locator("tr", { hasText: prettyName }).first();
  await expect(approvedRow).toBeVisible({ timeout: 10000 });

  await approvedRow
    .getByRole("button", { name: "Retract approval" })
    .click({ timeout: 10000 });

  await expect(page.locator("tr", { hasText: prettyName })).toHaveCount(0);

  await page.reload({ timeout: 15000 });
  await expect(page.locator("tr", { hasText: prettyName })).toHaveCount(0);
  const renewedGrant = await authorizeClientWithPkce(
    page,
    gamma.url,
    createdClient.clientId,
    createdClient.redirectUri,
    { consent: "required" },
  );
  expect(
    (
      await exchangeCode(request, gamma.url, createdClient, renewedGrant)
    ).status(),
  ).toBe(200);
});
