import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueLabel } from "../../helpers/strings";

test("given a personal client when deleting it then it is removed from my clients", async ({ page, gamma }) => {

  await login(page, gamma.url, "mscott", "password1337", "Boss");

  const prettyName = uniqueLabel("E2E My Client Del");

  await page.goto(`${gamma.url}/my-clients/create`, { timeout: 30000 });
  await page.fill('input[name="prettyName"]', prettyName);
  await page.fill('input[name="svDescription"]', "E2E svensk beskrivning");
  await page.fill('input[name="enDescription"]', "E2E english description");
  await page.fill(
    'input[name="redirectUrl"]',
    "https://example.org/callback",
  );

  await Promise.all([
    page.waitForURL("**/clients/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/my-clients", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete" }).click(),
  ]);

  await expect(page.getByText(prettyName)).toHaveCount(0);
});
