import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { uniqueCid } from "../../helpers/strings";

test("given an activation code when admin deletes it then it is removed from activation codes", async ({
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

  const cid = uniqueCid("act").slice(0, 10);

  await page.goto(`${gamma.url}/allow-list`, { timeout: 30000 });
  await page.fill('input[name="cid"]', cid);

  await Promise.all([
    page.waitForURL("**/allow-list", { timeout: 15000 }),
    page.locator('button[form="allow-cid-form"]').click(),
  ]);

  await logout(page);

  await page.goto(`${gamma.url}/activate-cid`, { timeout: 30000 });
  await page.fill('input[name="cid"]', cid);

  await Promise.all([
    page.waitForURL("**/email-sent", { timeout: 15000 }),
    page.getByRole("button", { name: "Activate cid" }).click(),
  ]);

  await expect(
    page.getByText("An email should be sent to your student email"),
  ).toBeVisible({ timeout: 10000 });

  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  await page.goto(`${gamma.url}/activation-codes`, { timeout: 30000 });

  const activationRow = page.locator("tr", { hasText: cid }).first();
  await expect(activationRow).toBeVisible({ timeout: 10000 });

  await activationRow
    .getByRole("button", { name: "Delete activation" })
    .click();

  await expect(page.locator("tr", { hasText: cid })).toHaveCount(0);
});
