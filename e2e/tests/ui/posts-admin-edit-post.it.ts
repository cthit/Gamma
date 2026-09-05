import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import { getGammaE2ERuntime } from "../../gamma-setup";

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
  if (getGammaE2ERuntime() === "kotlin") {
    const inputs = page.locator('input[name="list"]');
    const csrfToken = await page
      .locator('form[action="/posts/order"] input[name="_csrf"]')
      .first()
      .inputValue();
    const original = await Promise.all(
      (await inputs.all()).map((input) => input.inputValue()),
    );
    expect(original.length).toBeGreaterThan(1);
    const reversed = [...original].reverse();
    const reorderStatus = await page.evaluate(
      async ({ ids, csrfToken }) => {
        const body = new URLSearchParams();
        ids.forEach((id) => body.append("list", id));
        body.append("_method", "put");
        body.append("_csrf", csrfToken);
        return fetch("/posts/order", {
          method: "POST",
          headers: { "Content-Type": "application/x-www-form-urlencoded" },
          body,
        }).then((result) => result.status);
      },
      { ids: reversed, csrfToken },
    );
    expect(reorderStatus).toBe(200);

    await page.reload();
    const persisted = await Promise.all(
      (await inputs.all()).map((input) => input.inputValue()),
    );
    expect(persisted).toEqual(reversed);
  }

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
