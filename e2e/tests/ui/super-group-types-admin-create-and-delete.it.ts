import { expect, testWithDefaultGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid } from "../../helpers/strings";

test("given an admin user when creating and deleting a super group type then the type is removed", async ({ page, gamma }) => {

  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const typeName = uniqueCid("type");

  await page.goto(`${gamma.url}/types`, { timeout: 30000 });
  await expect(page.getByText("Create new super group type")).toBeVisible({
    timeout: 10000,
  });

  await page.fill('input[name="type"]', typeName);
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/types") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.getByRole("button", { name: "Save" }).click(),
  ]);

  const detailsLink = page
    .locator("tr", { hasText: typeName })
    .getByRole("link", {
      name: "Details",
    });
  await expect(detailsLink).toBeVisible({ timeout: 10000 });

  await Promise.all([
    page.waitForURL("**/types/*", { timeout: 15000 }),
    detailsLink.click(),
  ]);

  await expect(page.getByRole("button", { name: "Delete" })).toBeVisible({
    timeout: 10000,
  });

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/types", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete" }).click(),
  ]);

  await expect(page.getByText(typeName)).toHaveCount(0);
});
