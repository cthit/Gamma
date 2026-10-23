import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";

const digitGroupId = "047ac437-a789-4cc5-bb6e-ba50efd7c509";
const digitSuperGroupId = "aed27030-ad90-4526-855c-1e909b1dcecb";

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
  const groupIdRow = page.locator("article .tuple li", { hasText: "Id:" });
  await expect(groupIdRow).toBeVisible({ timeout: 10000 });
  await expect(groupIdRow.locator("code")).toHaveText(digitGroupId);

  await page.goto(`${gamma.url}/super-groups/${digitSuperGroupId}`, {
    timeout: 30000,
  });
  const superGroupIdRow = page.locator("article .tuple li", {
    hasText: "Id:",
  });
  await expect(superGroupIdRow).toBeVisible({ timeout: 10000 });
  await expect(superGroupIdRow.locator("code")).toHaveText(digitSuperGroupId);
});
