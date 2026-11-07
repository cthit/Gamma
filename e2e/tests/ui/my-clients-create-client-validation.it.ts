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
  await expect(
    page.getByRole("heading", {
      level: 2,
      name: "Create new client",
      exact: true,
    }),
  ).toBeVisible({
    timeout: 10000,
  });

  await page.getByRole("button", { name: "Create" }).click();

  await expect(
    page.getByLabel("Pretty name", { exact: true }),
  ).toHaveAccessibleDescription("Cannot be empty", { timeout: 10000 });
  await expect(
    page.getByLabel("Redirect url", { exact: true }),
  ).toHaveAccessibleDescription("Cannot be empty", { timeout: 10000 });
  await expect(page.getByLabel("Pretty name", { exact: true })).toHaveAttribute(
    "aria-invalid",
    "true",
  );
  await expect(page).toHaveURL(/\/my-clients\/create$/);
  await expect(
    page.getByRole("heading", {
      level: 2,
      name: "Create new client",
      exact: true,
    }),
  ).toBeVisible();
  await expect(page.getByText("500 - Internal Server Error")).toHaveCount(0);

  await page.getByLabel("Pretty name", { exact: true }).fill(prettyName);
  await page
    .getByLabel("Swedish description", { exact: true })
    .fill("E2E svensk beskrivning");
  await page
    .getByLabel("English description", { exact: true })
    .fill("E2E english description");
  await page.getByLabel("Redirect url", { exact: true }).fill("");

  await page.getByRole("button", { name: "Create" }).click();

  await expect(
    page.getByLabel("Pretty name", { exact: true }),
  ).toHaveAccessibleDescription("", { timeout: 10000 });
  await expect(
    page.getByLabel("Redirect url", { exact: true }),
  ).toHaveAccessibleDescription("Cannot be empty", { timeout: 10000 });
  await expect(page.getByLabel("Pretty name", { exact: true })).toHaveValue(
    prettyName,
  );
  await expect(page.getByText("500 - Internal Server Error")).toHaveCount(0);

  await page
    .getByLabel("Redirect url", { exact: true })
    .fill("https://example.org/callback");

  await Promise.all([
    page.waitForURL("**/clients/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  await expect(page.getByText("Client details")).toBeVisible({
    timeout: 10000,
  });
  await expect(page.getByText(prettyName)).toBeVisible({ timeout: 10000 });
});
