import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";

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
  await page.fill('input[name="name"]', name);
  await page.fill('input[name="prettyName"]', uniqueLabel("E2E Group"));

  await Promise.all([
    page.waitForURL("**/groups/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);
  await expect(page.locator("main > article").first()).toContainText(
    "Group details",
    { timeout: 10000 },
  );

  const groupUrl = page.url();
  const groupId = new URL(groupUrl).pathname.split("/").pop() ?? "";
  expect(groupId).toMatch(/^[0-9a-f-]{36}$/);

  await page.getByRole("button", { name: "Edit" }).click();
  await expect(page.locator("main > article").first()).toContainText(
    "Edit group details",
    { timeout: 10000 },
  );
  await page.fill('input[name="prettyName"]', stalePrettyName);

  const otherTab = await page.context().newPage();
  await otherTab.goto(groupUrl, { timeout: 30000 });
  await otherTab.getByRole("button", { name: "Edit" }).click();
  await expect(otherTab.locator("main > article").first()).toContainText(
    "Edit group details",
    { timeout: 10000 },
  );
  await otherTab.fill('input[name="prettyName"]', otherTabPrettyName);
  await otherTab.getByRole("button", { name: "Save" }).click();
  await expect(otherTab.locator("article .tuple")).toContainText(
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
  await expect(page.locator('#alerts [role="alert"]')).toContainText(
    "modified by someone else",
    { timeout: 10000 },
  );
  expect(page.url()).toBe(groupUrl);
  await expect(page.getByText("Internal Server Error")).toHaveCount(0);

  const form = page.locator("form#edit-group");
  const formValue = async (field: string) =>
    (await form.locator(`[name="${field}"]`).first().inputValue()).trim();
  const staleFormResponse = await page.request.fetch(groupUrl, {
    method: "POST",
    maxRedirects: 0,
    headers: { Accept: "text/html" },
    form: {
      _method: "put",
      _csrf: await formValue("_csrf"),
      version: await formValue("version"),
      name: await formValue("name"),
      prettyName: stalePrettyName,
      superGroupId: await formValue("superGroupId"),
    },
  });
  expect(staleFormResponse.status()).toBe(409);
  const staleFormBody = await staleFormResponse.text();
  expect(staleFormBody).toContain("409 - Conflict");
  expect(staleFormBody).toContain("modified by someone else");

  await page.reload();
  await expect(page.locator("article .tuple")).toContainText(
    otherTabPrettyName,
    { timeout: 10000 },
  );
  await expect(page.locator("article .tuple")).not.toContainText(
    stalePrettyName,
  );
});
