import { expect, type Page } from "@playwright/test";
import type { GammaInstance } from "../gamma-setup";
import { login } from "./auth";
import { articleByHeading, rowWithText } from "./a11y";
import { saveRecoveryKey } from "./recovery-key";

export async function issueAdminRecoveryLink(
  page: Page,
  gamma: GammaInstance,
  cid: string,
): Promise<string> {
  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!, "admin");
  await page.goto(`${gamma.url}/users`);
  await rowWithText(page, cid).getByRole("link", { name: "Details" }).click();
  const article = articleByHeading(page, "How the user signs in");
  page.once("dialog", (dialog) => dialog.accept());
  await article
    .getByRole("button", { name: "Reset sign-in and generate recovery link" })
    .click();
  await expect(article.getByRole("code")).toBeVisible();
  const link = new URL((await article.getByRole("code").innerText()).trim());
  return `${gamma.url}${link.pathname}${link.search}`;
}

export async function startAdminRecovery(
  page: Page,
  link: string,
): Promise<void> {
  await page.goto(link);
  await page
    .getByRole("button", { name: "Set up new sign-in credentials" })
    .click();
  await expect(page).toHaveURL(/\/sign-in-setup$/);
}

export async function saveSetupRecoveryKey(page: Page): Promise<string> {
  await expect(
    page.getByRole("heading", { name: "First, save a new recovery key" }),
  ).toBeVisible();
  await page.getByRole("button", { name: "Create a recovery key" }).click();
  const key = await saveRecoveryKey(page);
  await expect(page).toHaveURL(/\/sign-in-setup$/);
  await expect(
    page.getByRole("button", { name: "Use a password" }),
  ).toBeVisible();
  return key;
}
