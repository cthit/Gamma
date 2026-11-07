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
  await page.getByLabel("First name", { exact: true }).fill("Delete");
  await page.getByLabel("Last name", { exact: true }).fill("Me");
  await page.getByLabel("Nick", { exact: true }).fill("DeleteMe");
  await page.getByLabel("Cid", { exact: true }).fill(cid);
  await page.getByLabel("Email", { exact: true }).fill(email);
  await page.getByLabel("Password", { exact: true }).fill("password1337");
  await page
    .getByRole("combobox", {
      name: "What language does the user prefer?",
      exact: true,
    })
    .selectOption("EN");

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
