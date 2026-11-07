import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { createUserClientViaUi } from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";
import { rowWithText } from "../../helpers/a11y";

test("given a user-owned client when the owner opens my clients page then the client id is listed and copyable", async ({
  page,
  context,
  gamma,
}) => {
  await context.grantPermissions(["clipboard-read", "clipboard-write"], {
    origin: gamma.url,
  });

  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  const prettyName = uniqueLabel("E2E My Client");
  const { clientId } = await createUserClientViaUi(page, gamma.url, prettyName);
  expect(clientId).not.toBe("");

  await page.goto(`${gamma.url}/my-clients`, { timeout: 30000 });

  const row = rowWithText(page, prettyName).first();
  await expect(row).toBeVisible({ timeout: 10000 });
  await expect(row.getByRole("code")).toHaveText(clientId);

  const copyButton = row.getByRole("button", { name: "Copy" });
  await expect(copyButton).toBeVisible();
  await copyButton.click();

  // The button keeps its accessible name; its visible label (announced
  // through a live region) briefly changes to "Copied".
  await expect(copyButton).toHaveText("Copied", {
    timeout: 5000,
  });
  expect(await page.evaluate("navigator.clipboard.readText()")).toBe(clientId);
  await expect(copyButton).toHaveText("Copy", {
    timeout: 5000,
  });
});
