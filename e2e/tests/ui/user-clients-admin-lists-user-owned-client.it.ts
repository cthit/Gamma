import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { createUserClientViaUi } from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";

const jhalpertUserId = "bc605869-9a4d-46ec-8a29-d00819d4c195";
// '#', '/' and '=' are valid in an email local part but must be
// percent-encoded in a mailto: href (RFC 6068), otherwise the
// recipient is cut short or misread.
const ownerEmail = "jhalpert#e2e/x=y@example.org";
const ownerMailto = "mailto:jhalpert%23e2e%2Fx%3Dy@example.org";

test("given a user-owned client when admin opens user clients page then the client is listed", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  const prettyName = uniqueLabel("E2E User Client");
  const { clientId, clientUid } = await createUserClientViaUi(
    page,
    gamma.url,
    prettyName,
  );
  expect(clientId).not.toBe("");

  await logout(page);

  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  await page.goto(`${gamma.url}/users/${jhalpertUserId}`, { timeout: 30000 });
  await page.getByRole("button", { name: "Edit user" }).click();
  await page.fill('input[name="email"]', ownerEmail);
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes(`/users/${jhalpertUserId}`) &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.getByRole("button", { name: "Save" }).click(),
  ]);
  await expect(page.getByText("User updated")).toBeVisible({ timeout: 10000 });

  await page.goto(`${gamma.url}/user-clients`, { timeout: 30000 });

  const row = page.locator("tr", { hasText: prettyName }).first();
  await expect(row).toBeVisible({ timeout: 10000 });
  await expect(row.getByRole("link", { name: /Jim .*Halpert/ })).toBeVisible({
    timeout: 10000,
  });
  await expect(row.locator("code")).toHaveText(clientId);

  const rowEmailLink = row.getByRole("link", { name: ownerEmail });
  await expect(rowEmailLink).toBeVisible();
  await expect(rowEmailLink).toHaveAttribute("href", ownerMailto);

  await Promise.all([
    page.waitForURL(`**/clients/${clientUid}`, { timeout: 15000 }),
    row.getByRole("link", { name: "Details" }).click(),
  ]);

  const ownedBy = page.locator("ul.details li", { hasText: "Owned by:" });
  await expect(ownedBy).toBeVisible({ timeout: 10000 });
  await expect(ownedBy.getByRole("link", { name: "Big Tuna" })).toBeVisible();
  const ownerEmailLink = ownedBy.getByRole("link", { name: ownerEmail });
  await expect(ownerEmailLink).toBeVisible();
  await expect(ownerEmailLink).toHaveAttribute("href", ownerMailto);
});
