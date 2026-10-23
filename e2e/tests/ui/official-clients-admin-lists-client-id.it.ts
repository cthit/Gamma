import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { createUserClientViaUi } from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";

test("given an official client when admin opens clients page then the client id is listed", async ({
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

  const prettyName = uniqueLabel("E2E Official Client");
  const { clientId } = await createUserClientViaUi(
    page,
    gamma.url,
    prettyName,
    { official: true },
  );
  expect(clientId).not.toBe("");

  await page.goto(`${gamma.url}/clients`, { timeout: 30000 });

  const row = page.locator("tr", { hasText: prettyName }).first();
  await expect(row).toBeVisible({ timeout: 10000 });
  await expect(row.locator("code")).toHaveText(clientId);
  await expect(row.getByRole("button", { name: "Copy" })).toBeVisible();
});
