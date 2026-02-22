import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueLabel } from "../../helpers/strings";

test("given a signed in user when creating a personal oauth client then client details are shown", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "mscott", "password1337", "Boss");

  const prettyName = uniqueLabel("E2E My Client");

  await page.goto(`${gamma.url}/my-clients/create`, { timeout: 30000 });
  await expect(page.locator("article > header")).toHaveText(
    "Create new client",
    {
      timeout: 10000,
    },
  );

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
});
