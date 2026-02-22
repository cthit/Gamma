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
  await page.fill('input[name="svName"]', svName);
  await page.fill('input[name="enName"]', enName);
  await page.fill('input[name="emailPrefix"]', emailPrefix);

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
