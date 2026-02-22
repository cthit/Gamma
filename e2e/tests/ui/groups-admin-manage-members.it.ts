import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";

test("given an admin when editing group members then members can be removed and added", async ({
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

  await page.goto(`${gamma.url}/groups`, { timeout: 30000 });
  await page.getByRole("link", { name: "Details" }).first().click();

  await expect(page.locator("main > article").first()).toContainText(
    "Group details",
    {
      timeout: 10000,
    },
  );

  await page.getByRole("button", { name: "Edit" }).click();

  await expect(page.locator("main > article").first()).toContainText(
    "Edit group details",
    {
      timeout: 10000,
    },
  );

  const memberRows = page.locator(".member-row");
  while ((await memberRows.count()) > 0) {
    await memberRows.first().getByRole("button", { name: "Delete" }).click();
  }
  await expect(memberRows).toHaveCount(0);

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "GET" &&
        response.url().includes("/groups/new-member") &&
        response.status() === 200,
    ),
    page.getByRole("button", { name: "Add member" }).click(),
  ]);

  await expect(memberRows).toHaveCount(1);

  const addedMemberRow = memberRows.first();
  await addedMemberRow.locator("select.userId").selectOption({ label: "Boss" });
  await addedMemberRow.locator("input.unofficialPostName").fill("e2e-role");

  await Promise.all([
    page.waitForURL("**/groups/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Save" }).click(),
  ]);

  await expect(page.locator("main > article").first()).toContainText(
    "Group details",
    {
      timeout: 10000,
    },
  );

  const membersList = page
    .locator("main > article")
    .first()
    .locator("ul")
    .nth(1);
  await expect(membersList.locator("li")).toHaveCount(1);
  await expect(membersList).toContainText("Boss");
  await expect(membersList).toContainText("e2e-role");
});
