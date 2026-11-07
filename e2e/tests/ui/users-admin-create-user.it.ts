import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueEmail } from "../../helpers/strings";

test("given an admin user when creating a user then the new user is visible in users and details", async ({
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

  const cid = uniqueCid("usr");
  const email = uniqueEmail("usr");

  await page.goto(`${gamma.url}/users/create`, { timeout: 30000 });
  await expect(
    page.getByRole("heading", { level: 2, name: "Create user", exact: true }),
  ).toBeVisible({
    timeout: 10000,
  });

  await page.getByLabel("First name", { exact: true }).fill("E2E");
  await page.getByLabel("Last name", { exact: true }).fill("Created");
  await page.getByLabel("Nick", { exact: true }).fill("E2ENick");
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

  await expect(page.getByText(cid)).toBeVisible({ timeout: 10000 });

  await page.goto(`${gamma.url}/users`, { timeout: 30000 });
  await expect(page.getByText(cid)).toBeVisible({ timeout: 10000 });
});
