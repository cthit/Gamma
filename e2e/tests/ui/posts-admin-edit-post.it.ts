import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";

test("an admin can create edit reorder and delete a post", async ({
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

  const svName = uniqueLabel("E2E Sv Edit Post");
  const enName = uniqueLabel("E2E En Edit Post");
  const emailPrefix = uniqueCid("ep");

  await page.goto(`${gamma.url}/posts/create`, { timeout: 30000 });
  await page.fill('input[name="svName"]', svName);
  await page.fill('input[name="enName"]', enName);
  await page.fill('input[name="emailPrefix"]', emailPrefix);

  await Promise.all([
    page.waitForURL("**/posts/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  const updatedEnName = uniqueLabel("E2E En Updated Post");

  await page.getByRole("button", { name: "Edit post" }).click();
  await page.fill('input[name="enName"]', updatedEnName);

  await Promise.all([
    page.waitForURL("**/posts/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Save" }).click(),
  ]);

  await expect(page.getByText(updatedEnName)).toBeVisible({ timeout: 10000 });

  await page.goto(`${gamma.url}/posts`, { timeout: 30000 });
  const inputs = page.locator('input[name="list"]');
  const original = await Promise.all(
    (await inputs.all()).map((input) => input.inputValue()),
  );
  expect(original.length).toBeGreaterThan(1);
  const lastRow = page.locator(".post-order tr").last();
  await lastRow.getByRole("button", { name: "Move up" }).click();
  const expected = original.slice(0, -2).concat(original.slice(-2).reverse());
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().endsWith("/posts/order") &&
        response.status() === 302,
    ),
    page.getByRole("button", { name: "Save order" }).click(),
  ]);
  await page.goto(`${gamma.url}/posts`);
  await page.reload();
  await expect(inputs).toHaveCount(expected.length);
  expect(
    await Promise.all((await inputs.all()).map((input) => input.inputValue())),
  ).toEqual(expected);

  await page
    .locator("tr", { hasText: updatedEnName })
    .getByRole("link", { name: "Details" })
    .click();
  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/posts", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete" }).click(),
  ]);
  await expect(page.getByText(updatedEnName)).toHaveCount(0);
});
