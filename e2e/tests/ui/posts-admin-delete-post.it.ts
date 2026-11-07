import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";

test("given an admin user when deleting a created post then it is removed from posts", async ({
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

  const svName = uniqueLabel("E2E Sv Delete Post");
  const enName = uniqueLabel("E2E En Delete Post");
  const emailPrefix = uniqueCid("dp");

  await page.goto(`${gamma.url}/posts/create`, { timeout: 30000 });
  await page.getByLabel("Swedish name", { exact: true }).fill(svName);
  await page.getByLabel("English name", { exact: true }).fill(enName);
  await page.getByLabel("Email prefix", { exact: true }).fill(emailPrefix);

  await Promise.all([
    page.waitForURL("**/posts/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/posts", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete" }).click(),
  ]);

  await expect(page.getByText(enName)).toHaveCount(0);
});
