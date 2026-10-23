import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueLabel } from "../../helpers/strings";

test("given a signed in user when submitting a personal client form with missing fields then field errors are shown and input is kept", async ({
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

  await page.getByRole("button", { name: "Create" }).click();

  await expect(page.locator("small#prettyName")).toHaveText("Cannot be empty", {
    timeout: 10000,
  });
  await expect(page.locator("small#redirectUrl")).toHaveText(
    "Cannot be empty",
    { timeout: 10000 },
  );
  await expect(page.locator('input[name="prettyName"]')).toHaveAttribute(
    "aria-invalid",
    "true",
  );
  await expect(page).toHaveURL(/\/my-clients\/create$/);
  await expect(page.locator("article > header")).toHaveText(
    "Create new client",
  );
  await expect(page.getByText("500 - Internal Server Error")).toHaveCount(0);

  await page.fill('input[name="prettyName"]', prettyName);
  await page.fill('input[name="svDescription"]', "E2E svensk beskrivning");
  await page.fill('input[name="enDescription"]', "E2E english description");
  await page.fill('input[name="redirectUrl"]', "");

  await page.getByRole("button", { name: "Create" }).click();

  await expect(page.locator("small#prettyName")).toHaveCount(0, {
    timeout: 10000,
  });
  await expect(page.locator("small#redirectUrl")).toHaveText(
    "Cannot be empty",
    { timeout: 10000 },
  );
  await expect(page.locator('input[name="prettyName"]')).toHaveValue(
    prettyName,
  );
  await expect(page.getByText("500 - Internal Server Error")).toHaveCount(0);

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
