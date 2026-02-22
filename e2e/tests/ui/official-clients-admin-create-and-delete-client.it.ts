import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueLabel } from "../../helpers/strings";

test("given an admin user when creating and deleting an official client then it is removed", async ({
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

  await page.goto(`${gamma.url}/clients/create`, { timeout: 30000 });
  await expect(page.locator("article > header")).toHaveText("Create client", {
    timeout: 10000,
  });

  await page.fill('input[name="prettyName"]', prettyName);
  await page.fill('input[name="svDescription"]', "E2E svensk beskrivning");
  await page.fill('input[name="enDescription"]', "E2E english description");
  await page.fill('input[name="redirectUrl"]', "https://example.org/callback");

  await Promise.all([
    page.waitForURL("**/clients/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  await expect(page.getByText("Client details")).toBeVisible({
    timeout: 10000,
  });
  await expect(page.getByText(prettyName)).toBeVisible({ timeout: 10000 });

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/clients", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete" }).click(),
  ]);

  await expect(page.getByText(prettyName)).toHaveCount(0);
});
