import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueLabel } from "../../helpers/strings";

test("given an admin user when creating a post then it is visible in posts and details", async ({
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

  const svName = uniqueLabel("E2E Sv Post");
  const enName = uniqueLabel("E2E En Post");
  const emailPrefix = uniqueLabel("e2epost")
    .replace(/[^a-zA-Z0-9]/g, "")
    .toLowerCase();

  await page.goto(`${gamma.url}/posts/create`, { timeout: 30000 });
  await expect(page.getByText("Create post")).toBeVisible({ timeout: 10000 });

  await page.fill('input[name="svName"]', svName);
  await page.fill('input[name="enName"]', enName);
  await page.fill('input[name="emailPrefix"]', emailPrefix);

  await Promise.all([
    page.waitForURL("**/posts/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  await expect(page.getByText(svName)).toBeVisible({ timeout: 10000 });
  await expect(page.getByText(enName)).toBeVisible({ timeout: 10000 });

  await page.goto(`${gamma.url}/posts`, { timeout: 30000 });
  await expect(page.getByText(enName)).toBeVisible({ timeout: 10000 });
});
