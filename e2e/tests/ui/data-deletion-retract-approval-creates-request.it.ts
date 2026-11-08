import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";
import { rowWithText } from "../../helpers/a11y";

test("given an approved official client when retracting and approving it again then the data deletion request is marked as approved again", async ({
  page,
  gamma,
}) => {
  const officialName = uniqueLabel("Official");
  const unofficialName = uniqueLabel("Own app");

  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!);
  const official = await createUserClientViaUi(page, gamma.url, officialName, {
    official: true,
  });
  await logout(page);

  await login(page, gamma.url, "pbeesly", "password1337", "Pam-Pam");
  const unofficial = await createUserClientViaUi(
    page,
    gamma.url,
    unofficialName,
  );
  for (const client of [official, unofficial]) {
    await authorizeClientWithPkce(
      page,
      gamma.url,
      client.clientId,
      client.redirectUri,
      { consent: "required" },
    );
  }

  await page.goto(`${gamma.url}/me/accepted-clients`, { timeout: 30000 });
  for (const name of [officialName, unofficialName]) {
    const row = rowWithText(page, name);
    await row.getByRole("button", { name: "Retract approval" }).click();
    await expect(rowWithText(page, name)).toHaveCount(0, { timeout: 10000 });
  }

  await page.goto(`${gamma.url}/delete-your-account`, { timeout: 30000 });
  await expect(
    page.getByRole("heading", { name: "Official clients you have approved" }),
  ).toBeVisible({ timeout: 10000 });
  await expect(
    page.getByRole("listitem").filter({ hasText: officialName }),
  ).toBeVisible();
  await logout(page);

  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!);
  await page.goto(`${gamma.url}/data-deletion`, { timeout: 30000 });
  const openRequests = page.getByRole("table", {
    name: "Data deletion requests",
  });
  const request = rowWithText(openRequests, officialName);
  await expect(request).toBeVisible({ timeout: 10000 });
  await expect(request).toContainText("pbeesly");
  await expect(request).toContainText("Approval retracted");
  await expect(request).not.toContainText("Approved the client again");
  await expect(rowWithText(openRequests, unofficialName)).toHaveCount(0);
  await logout(page);

  await login(page, gamma.url, "pbeesly", "password1337", "Pam-Pam");
  await authorizeClientWithPkce(
    page,
    gamma.url,
    official.clientId,
    official.redirectUri,
    { consent: "required" },
  );
  await page.goto(gamma.url, { timeout: 30000 });
  await logout(page);

  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!);
  await page.goto(`${gamma.url}/data-deletion`, { timeout: 30000 });
  await expect(rowWithText(openRequests, officialName)).toContainText(
    "Approved the client again",
    { timeout: 10000 },
  );
});
