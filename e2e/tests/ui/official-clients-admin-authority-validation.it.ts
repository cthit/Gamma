import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import type { Page } from "@playwright/test";

async function submitAuthority(page: Page): Promise<void> {
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/authority") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.locator('button[form="create-client-authority"]').click(),
  ]);
}

test("given an official client when an admin submits invalid or duplicate authority names then validation errors are shown", async ({
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
  const authorityName = uniqueCid("auth");

  await page.goto(`${gamma.url}/clients/create`, { timeout: 30000 });
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

  const authorityInput = page.locator('input[name="authority"]');

  await authorityInput.fill("A");
  await submitAuthority(page);

  await expect(page.getByText(/between 2 and 30/)).toBeVisible({
    timeout: 10000,
  });
  await expect(authorityInput).toHaveValue("A");
  await expect(authorityInput).toHaveAttribute("aria-invalid", "true");
  await expect(page.getByText("Client details")).toBeVisible();
  await expect(page.getByText("500 - Internal Server Error")).toHaveCount(0);

  await authorityInput.fill("");
  await submitAuthority(page);

  await expect(page.getByText("Cannot be empty")).toBeVisible({
    timeout: 10000,
  });
  await expect(page.getByText("500 - Internal Server Error")).toHaveCount(0);

  await authorityInput.fill(authorityName);
  await submitAuthority(page);

  const authorityHeaders = page
    .locator("main > article > header")
    .filter({ hasText: authorityName });

  await expect(authorityHeaders).toHaveCount(1, { timeout: 10000 });
  await expect(authorityInput).toHaveValue("");

  await authorityInput.fill(authorityName);
  await submitAuthority(page);

  await expect(page.getByText(/already exists/)).toBeVisible({
    timeout: 10000,
  });
  await expect(authorityInput).toHaveValue(authorityName);
  await expect(authorityHeaders).toHaveCount(1);
  await expect(page.getByText("500 - Internal Server Error")).toHaveCount(0);
});
