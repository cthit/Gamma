import { expect, type Page } from "@playwright/test";

/**
 * Saves the recovery key shown on the page: reads it, continues to the next
 * page and types it back there.
 */
export async function saveRecoveryKey(page: Page): Promise<string> {
  await expect(
    page.getByRole("heading", { name: "Save your recovery key" }),
  ).toBeVisible();
  const key = (
    await page.getByLabel("Your recovery key", { exact: true }).innerText()
  ).trim();
  expect(key.replace(/\s/g, "")).toHaveLength(64);

  await page.getByRole("link", { name: "I have saved it" }).click();
  await expect(
    page.getByRole("heading", { name: "Type your recovery key" }),
  ).toBeVisible();
  await page.getByLabel("Recovery key", { exact: true }).fill(key);
  await page.getByRole("button", { name: "Confirm" }).click();
  return key;
}
