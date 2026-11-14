import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";
import { rowWithText } from "../../helpers/a11y";

const jhalpertUserId = "bc605869-9a4d-46ec-8a29-d00819d4c195";

test("given a user that approved clients when deleting the account then the admins must clear a data deletion request for the official client", async ({
  page,
  gamma,
}) => {
  const officialName = uniqueLabel("Official");
  const unofficialName = uniqueLabel("Scott app");

  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!);
  const official = await createUserClientViaUi(page, gamma.url, officialName, {
    official: true,
  });
  await logout(page);

  await login(page, gamma.url, "mscott", "password1337", "Boss");
  const unofficial = await createUserClientViaUi(
    page,
    gamma.url,
    unofficialName,
  );
  await logout(page);

  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await authorizeClientWithPkce(
    page,
    gamma.url,
    official.clientId,
    official.redirectUri,
    { consent: "required" },
  );
  await authorizeClientWithPkce(
    page,
    gamma.url,
    unofficial.clientId,
    unofficial.redirectUri,
    {
      consent: "required",
      beforeAuthorize: async (consentPage) => {
        const ownerEmail = consentPage.getByRole("link", {
          name: "mscott@example.org",
          exact: true,
        });
        await expect(ownerEmail).toBeVisible();
        await expect(ownerEmail).toHaveAttribute(
          "href",
          "mailto:mscott@example.org",
        );
      },
    },
  );

  await page.goto(`${gamma.url}/delete-your-account`, { timeout: 30000 });
  await expect(
    page.getByRole("heading", { name: "Official clients you have approved" }),
  ).toBeVisible({ timeout: 10000 });
  await expect(
    page.getByRole("listitem").filter({ hasText: officialName }),
  ).toBeVisible();

  const ownerLink = page.getByRole("link", {
    name: "Email mscott@example.org",
  });
  await expect(ownerLink).toBeVisible();
  const mailto = (await ownerLink.getAttribute("href")) ?? "";
  expect(mailto).toMatch(/^mailto:mscott@example\.org\?subject=/);
  const body = decodeURIComponent(mailto.split("&body=")[1] ?? "");
  expect(body).toContain(unofficialName);
  expect(body).toContain(jhalpertUserId);
  expect(body).toContain("jhalpert");

  await page
    .getByLabel("Confirm password", { exact: true })
    .fill("password1337");
  await Promise.all([
    page.waitForURL("**/login?deleted", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete the account" }).click(),
  ]);

  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!);

  await expect(
    page.getByRole("heading", { name: "Action required" }),
  ).toBeVisible({ timeout: 10000 });
  await expect(
    page.getByText("No one gets the daily email about data deletion requests."),
  ).toBeVisible();

  await Promise.all([
    page.waitForURL("**/data-deletion", { timeout: 15000 }),
    page.getByRole("link", { name: "Go to data deletion requests" }).click(),
  ]);

  const openRequests = page.getByRole("table", {
    name: "Data deletion requests",
  });
  const request = rowWithText(openRequests, officialName);
  await expect(request).toBeVisible({ timeout: 10000 });
  await expect(request).toContainText("jhalpert");
  await expect(request).toContainText(jhalpertUserId);
  await expect(request).toContainText("Account deleted");
  await expect(rowWithText(openRequests, unofficialName)).toHaveCount(0);

  await page
    .getByLabel("Email", { exact: true })
    .fill(`${"a".repeat(95)}@example.org`);
  await page.getByRole("button", { name: "Add" }).click();
  await expect(page.getByText("Not a valid email")).toBeVisible({
    timeout: 10000,
  });

  await page.getByLabel("Email", { exact: true }).fill("dpo@example.org");
  await page.getByRole("button", { name: "Add" }).click();
  await expect(
    page.getByRole("button", { name: "Remove dpo@example.org" }),
  ).toBeVisible({ timeout: 10000 });
  await expect(
    page.getByRole("heading", { name: "Action required" }),
  ).toHaveCount(0);

  page.once("dialog", async (dialog) => dialog.accept());
  await request
    .getByRole("button", {
      name: `Clear the request for jhalpert in ${officialName}`,
    })
    .click();

  await expect(rowWithText(openRequests, officialName)).toHaveCount(0, {
    timeout: 10000,
  });

  const cleared = rowWithText(
    page.getByRole("table", { name: "Cleared requests" }),
    jhalpertUserId,
  );
  await expect(cleared).toBeVisible();
  await expect(cleared).toContainText(official.clientUid);
  await expect(cleared).toContainText("by admin 'admin' admin");
  await expect(page.getByText("jhalpert", { exact: true })).toHaveCount(0);
  await expect(page.getByText(officialName)).toHaveCount(0);
});
