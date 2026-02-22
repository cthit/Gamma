import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";

test("given an admin user when editing a created post then updated values are shown", async ({
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

  const svName = uniqueLabel("E2E Sv Edit Post");
  const enName = uniqueLabel("E2E En Edit Post");
  const emailPrefix = uniqueCid("ep");

  await page.goto(`${gamma.url}/posts/create`, { timeout: 30000 });
  await page.fill('input[name="svName"]', svName);
  await page.fill('input[name="enName"]', enName);
  await page.fill('input[name="emailPrefix"]', emailPrefix);

  await Promise.all([
    page.waitForURL("**/posts/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  const updatedEnName = uniqueLabel("E2E En Updated Post");

  await page.getByRole("button", { name: "Edit post" }).click();
  await page.fill('input[name="enName"]', updatedEnName);

  await Promise.all([
    page.waitForURL("**/posts/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Save" }).click(),
  ]);

  await expect(page.getByText(updatedEnName)).toBeVisible({ timeout: 10000 });
});
