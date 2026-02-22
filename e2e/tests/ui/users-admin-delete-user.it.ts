import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueEmail } from "../../helpers/strings";

test("given a created user when admin deletes the user then it is removed from users list", async ({
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

  const cid = uniqueCid("del");
  const email = uniqueEmail("del");

  await page.goto(`${gamma.url}/users/create`, { timeout: 30000 });
  await page.fill('input[name="firstName"]', "Delete");
  await page.fill('input[name="lastName"]', "Me");
  await page.fill('input[name="nick"]', "DeleteMe");
  await page.fill('input[name="cid"]', cid);
  await page.fill('input[name="email"]', email);
  await page.fill('input[name="password"]', "password1337");
  await page.selectOption('select[name="language"]', "EN");

  await Promise.all([
    page.waitForURL("**/users/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create user" }).click(),
  ]);

  page.once("dialog", async (dialog) => dialog.accept());

  await Promise.all([
    page.waitForURL("**/users", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete user" }).click(),
  ]);

  await expect(page.getByText(cid)).toHaveCount(0);
});
