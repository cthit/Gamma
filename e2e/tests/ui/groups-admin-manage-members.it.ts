import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { articleByHeading } from "../../helpers/a11y";

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

  await expect(
    page.getByRole("heading", { level: 2, name: "Group details", exact: true }),
  ).toBeVisible({
    timeout: 10000,
  });

  await page.getByRole("button", { name: "Edit" }).click();

  await expect(
    page.getByRole("heading", {
      level: 2,
      name: "Edit group details",
      exact: true,
    }),
  ).toBeVisible({
    timeout: 10000,
  });

  const editArticle = articleByHeading(page, "Edit group details");
  // Every member row has a button named "Delete <member>".
  const memberRows = editArticle.getByRole("button", { name: /^Delete / });
  while ((await memberRows.count()) > 0) {
    await memberRows.first().click();
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

  await editArticle
    .getByRole("combobox", { name: "User", exact: true })
    .selectOption({ label: "Boss" });
  await editArticle
    .getByLabel("Unofficial post name", { exact: true })
    .fill("e2e-role");

  await Promise.all([
    page.waitForURL("**/groups/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Save" }).click(),
  ]);

  await expect(
    page.getByRole("heading", { level: 2, name: "Group details", exact: true }),
  ).toBeVisible({
    timeout: 10000,
  });

  const membersList = articleByHeading(page, "Group details").getByRole("list");
  await expect(membersList.getByRole("listitem")).toHaveCount(1);
  await expect(membersList).toContainText("Boss");
  await expect(membersList).toContainText("e2e-role");
});
