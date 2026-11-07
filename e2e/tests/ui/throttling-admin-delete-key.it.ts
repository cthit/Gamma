import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { rowWithText } from "../../helpers/a11y";

test("given a throttling key when admin deletes it then it is removed from throttling", async ({
  page,
  gamma,
}) => {
  await page.goto(`${gamma.url}/forgot-password`, { timeout: 30000 });
  await page.getByLabel("Cid or Email", { exact: true }).fill("jhalpert");

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/forgot-password") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.getByRole("button", { name: "Reset password" }).click(),
  ]);

  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  await page.goto(`${gamma.url}/throttling`, { timeout: 30000 });

  const throttledRow = page
    .getByRole("row")
    .filter({ has: page.getByRole("button", { name: "Delete" }) })
    .first();
  await expect(throttledRow).toBeVisible({ timeout: 10000 });

  const key = (await throttledRow.getByRole("cell").first().innerText()).trim();

  await throttledRow.getByRole("button", { name: "Delete" }).click();

  await expect(rowWithText(page, key)).toHaveCount(0);
});
