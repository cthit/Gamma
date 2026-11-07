import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import type { Page } from "@playwright/test";
import { articleByHeading } from "../../helpers/a11y";

async function submitAuthority(page: Page): Promise<void> {
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/authority") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    articleByHeading(page, "Create client authority")
      .getByRole("button", { name: "Save", exact: true })
      .click(),
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
  await page.getByLabel("Pretty name", { exact: true }).fill(prettyName);
  await page
    .getByLabel("Swedish description", { exact: true })
    .fill("E2E svensk beskrivning");
  await page
    .getByLabel("English description", { exact: true })
    .fill("E2E english description");
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

  const authorityInput = page.getByLabel("Authority name", { exact: true });

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

  const authorityHeaders = page.getByRole("heading", {
    level: 2,
    name: authorityName,
    exact: true,
  });

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
