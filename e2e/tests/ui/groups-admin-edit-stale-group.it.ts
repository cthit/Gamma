import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import { articleByHeading, formValuesOf } from "../../helpers/a11y";

test("given a group edited in another tab when saving a stale edit then a conflict message is shown", async ({
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

  const name = uniqueCid("grp");
  const stalePrettyName = uniqueLabel("E2E Group Stale");
  const otherTabPrettyName = uniqueLabel("E2E Group Other Tab");

  await page.goto(`${gamma.url}/groups/create`, { timeout: 30000 });
  await page.getByLabel("Name", { exact: true }).fill(name);
  await page
    .getByLabel("Pretty name", { exact: true })
    .fill(uniqueLabel("E2E Group"));

  await Promise.all([
    page.waitForURL("**/groups/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);
  await expect(
    page.getByRole("heading", { level: 2, name: "Group details", exact: true }),
  ).toBeVisible({ timeout: 10000 });

  const groupUrl = page.url();
  const groupId = new URL(groupUrl).pathname.split("/").pop() ?? "";
  expect(groupId).toMatch(/^[0-9a-f-]{36}$/);

  await page.getByRole("button", { name: "Edit" }).click();
  await expect(
    page.getByRole("heading", {
      level: 2,
      name: "Edit group details",
      exact: true,
    }),
  ).toBeVisible({ timeout: 10000 });
  await page.getByLabel("Pretty name", { exact: true }).fill(stalePrettyName);

  const otherTab = await page.context().newPage();
  await otherTab.goto(groupUrl, { timeout: 30000 });
  await otherTab.getByRole("button", { name: "Edit" }).click();
  await expect(
    otherTab.getByRole("heading", {
      level: 2,
      name: "Edit group details",
      exact: true,
    }),
  ).toBeVisible({ timeout: 10000 });
  await otherTab
    .getByLabel("Pretty name", { exact: true })
    .fill(otherTabPrettyName);
  await otherTab.getByRole("button", { name: "Save" }).click();
  await expect(articleByHeading(otherTab, "Group details")).toContainText(
    otherTabPrettyName,
    { timeout: 10000 },
  );
  await otherTab.close();

  const [staleResponse] = await Promise.all([
    page.waitForResponse(
      (response) =>
        response.url() === `${gamma.url}/groups/${groupId}` &&
        response.request().method() !== "GET",
    ),
    page.getByRole("button", { name: "Save" }).click(),
  ]);
  expect(staleResponse.status()).toBe(409);
  await expect(page.getByRole("alert")).toContainText(
    "modified by someone else",
    { timeout: 10000 },
  );
  expect(page.url()).toBe(groupUrl);
  await expect(page.getByText("Internal Server Error")).toHaveCount(0);

  const formValues = await formValuesOf(
    page.getByLabel("Name", { exact: true }),
  );
  const formValue = (field: string) => (formValues[field] ?? "").trim();
  const staleFormResponse = await page.request.fetch(groupUrl, {
    method: "POST",
    maxRedirects: 0,
    headers: { Accept: "text/html" },
    form: {
      _method: "put",
      _csrf: formValue("_csrf"),
      version: formValue("version"),
      name: formValue("name"),
      prettyName: stalePrettyName,
      superGroupId: formValue("superGroupId"),
    },
  });
  expect(staleFormResponse.status()).toBe(409);
  const staleFormBody = await staleFormResponse.text();
  expect(staleFormBody).toContain("409 - Conflict");
  expect(staleFormBody).toContain("modified by someone else");

  await page.reload();
  await expect(articleByHeading(page, "Group details")).toContainText(
    otherTabPrettyName,
    { timeout: 10000 },
  );
  await expect(articleByHeading(page, "Group details")).not.toContainText(
    stalePrettyName,
  );
});
