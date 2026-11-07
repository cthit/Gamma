import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import type { Page } from "@playwright/test";
import { login } from "../../helpers/auth";

const digitGroupId = "047ac437-a789-4cc5-bb6e-ba50efd7c509";
const digitSuperGroupId = "aed27030-ad90-4526-855c-1e909b1dcecb";

// The value of the "Id" entry in the details card.
function idDefinition(page: Page) {
  return page
    .getByRole("article")
    .filter({ has: page.getByRole("term").filter({ hasText: /^Id$/ }) })
    .getByRole("definition")
    .filter({ has: page.getByRole("code") });
}

test("given a group and a super group when viewing their details then their ids are shown", async ({
  page,
  gamma,
}) => {
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  await page.goto(`${gamma.url}/groups/${digitGroupId}`, { timeout: 30000 });
  const groupIdRow = idDefinition(page);
  await expect(groupIdRow).toBeVisible({ timeout: 10000 });
  await expect(groupIdRow.getByRole("code")).toHaveText(digitGroupId);

  await page.goto(`${gamma.url}/super-groups/${digitSuperGroupId}`, {
    timeout: 30000,
  });
  const superGroupIdRow = idDefinition(page);
  await expect(superGroupIdRow).toBeVisible({ timeout: 10000 });
  await expect(superGroupIdRow.getByRole("code")).toHaveText(digitSuperGroupId);
});
