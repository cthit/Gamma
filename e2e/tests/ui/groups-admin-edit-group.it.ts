import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import { getGammaE2ERuntime } from "../../gamma-setup";
import { Buffer } from "node:buffer";

test("an admin can create edit and delete a group", async ({ page, gamma }) => {
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const initialName = uniqueCid("grp");
  const initialPrettyName = uniqueLabel("E2E Group");
  const updatedName = uniqueCid("grp");
  const updatedPrettyName = uniqueLabel("E2E Group Updated");

  await page.goto(`${gamma.url}/groups/create`, { timeout: 30000 });
  await page.fill('input[name="name"]', initialName);
  await page.fill('input[name="prettyName"]', initialPrettyName);

  await Promise.all([
    page.waitForURL(/\/groups\/[0-9a-f-]{36}$/, { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);
  const detailsUrl = page.url();
  const groupId = new URL(detailsUrl).pathname.split("/").at(-1) ?? "";

  await expect(page.locator("main > article").first()).toContainText(
    "Group details",
    {
      timeout: 10000,
    },
  );
  if (getGammaE2ERuntime() === "kotlin") {
    await expect(page.locator("article .tuple")).toContainText(groupId);

    await expect(page.locator('img[alt="Group avatar"]')).toHaveAttribute(
      "src",
      /\?v=0$/,
    );
    await page
      .locator('form#edit-group-avatar input[name="file"]')
      .setInputFiles({
        name: "avatar.png",
        mimeType: "image/png",
        buffer: Buffer.from(
          "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=",
          "base64",
        ),
      });
    const avatarResponse = page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().endsWith(`/groups/avatar/${groupId}`),
    );
    await page.getByRole("button", { name: "Upload avatar" }).click();
    expect((await avatarResponse).status()).toBe(200);
    await expect(page.locator('img[alt="Group avatar"]')).toHaveAttribute(
      "src",
      /\?v=1$/,
    );

    await page
      .locator('form#edit-group-banner input[name="file"]')
      .setInputFiles({
        name: "too-large.png",
        mimeType: "image/png",
        // Gamma 2.5.1 accepted images below 3 MiB because it compared whole MiB.
        // Exercise the first byte outside that compatibility boundary.
        buffer: Buffer.alloc(3 * 1024 * 1024),
      });
    const oversizedResponse = page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().endsWith(`/groups/banner/${groupId}`),
    );
    await page.getByRole("button", { name: "Upload banner" }).click();
    const response = await oversizedResponse;
    expect(response.status()).toBe(413);
    expect(await response.text()).toContain("413 - Upload too large");
    await expect(page.getByText("413 - Upload too large")).toBeVisible();
    await page.goto(detailsUrl, { timeout: 30000 });
  }

  await page.getByRole("button", { name: "Edit" }).click();
  await expect(page.locator("main > article").first()).toContainText(
    "Edit group details",
    {
      timeout: 10000,
    },
  );

  await page.fill('input[name="name"]', updatedName);
  await page.fill('input[name="prettyName"]', updatedPrettyName);

  await Promise.all([
    page.waitForURL("**/groups/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Save" }).click(),
  ]);

  await expect(page.locator("article .tuple")).toContainText(updatedName, {
    timeout: 10000,
  });
  await expect(page.locator("article .tuple")).toContainText(
    updatedPrettyName,
    {
      timeout: 10000,
    },
  );

  await page.goto(`${gamma.url}/groups`, { timeout: 30000 });
  await expect(page.getByText(updatedPrettyName)).toBeVisible({
    timeout: 10000,
  });

  await page
    .locator("tr", { hasText: updatedPrettyName })
    .getByRole("link", { name: "Details" })
    .click();
  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForURL("**/groups", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete" }).click(),
  ]);

  await expect(page.getByText(updatedPrettyName)).toHaveCount(0);
});
