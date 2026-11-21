import type { Page } from "@playwright/test";
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { issueAdminRecoveryLink } from "../../helpers/admin-recovery";
import {
  articleByHeading,
  formValuesOf,
  rowWithText,
} from "../../helpers/a11y";
import { addPasskey, signInWithPasskey } from "../../helpers/passkeys";
import type { GammaEnvironment } from "../../gamma-setup";

const refused =
  "No admin would be able to sign in after this change, so it was not saved.";

async function sql(env: GammaEnvironment, query: string): Promise<string> {
  const result = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-At",
    "-c",
    query,
  ]);
  expect(result.exitCode).toBe(0);
  return result.stdout.trim();
}

const userId = (env: GammaEnvironment, cid: string) =>
  sql(env, `SELECT user_id FROM g_user WHERE cid='${cid}'`);

const isAdmin = async (env: GammaEnvironment, cid: string) =>
  (await sql(
    env,
    `SELECT count(*) FROM g_admin_user a JOIN g_user u USING (user_id) WHERE u.cid='${cid}'`,
  )) === "1";

const usableAdmins = async (env: GammaEnvironment) =>
  Number(
    await sql(
      env,
      `SELECT count(*) FROM g_admin_user a JOIN g_user u USING (user_id)
       WHERE u.locked IS NOT TRUE AND EXISTS (
         SELECT 1 FROM user_entities e JOIN user_credentials c ON c.user_entity_user_id = e.id
         WHERE e.name = a.user_id::text)`,
    ),
  );

async function saveAdmins(
  page: Page,
  baseUrl: string,
  adminIds: string[],
): Promise<void> {
  await page.goto(`${baseUrl}/admins`);
  const boxes = page.getByRole("checkbox");
  const n = await boxes.count();
  for (let i = 0; i < n; i++) {
    const box = boxes.nth(i);
    await box.setChecked(adminIds.includes((await box.getAttribute("name"))!));
  }
  await Promise.all([
    page.waitForResponse(
      (r) =>
        r.request().method() !== "GET" &&
        new URL(r.url()).pathname === "/admins",
    ),
    page.getByRole("button", { name: "Save" }).click(),
  ]);
  await page.waitForLoadState("networkidle");
}

test("an admin can't hand over to an admin who has no way to sign in", async ({
  page,
  gamma,
  env,
}) => {
  // Revoking dwight's sign-in methods leaves him with only the handover link.
  const link = await issueAdminRecoveryLink(page, gamma, "dschrute");
  const dwight = await userId(env, "dschrute");
  const me = await userId(env, gamma.adminCid!);

  await saveAdmins(page, gamma.url, [dwight]);
  await expect(page.getByText(refused)).toBeVisible();

  expect(await isAdmin(env, gamma.adminCid!)).toBe(true);
  expect(await isAdmin(env, "dschrute")).toBe(false);

  // Promoting him while staying an admin is still fine.
  await saveAdmins(page, gamma.url, [me, dwight]);
  await expect(page).toHaveURL(/\/admins$/);
  await expect(page.getByText(refused)).toHaveCount(0);
  expect(await isAdmin(env, "dschrute")).toBe(true);
  expect(link).toContain("/admin-recovery?token=");
});

test("the last admin who can sign in can't delete their own account", async ({
  page,
  gamma,
  env,
}) => {
  await issueAdminRecoveryLink(page, gamma, "dschrute");
  await saveAdmins(page, gamma.url, [
    await userId(env, gamma.adminCid!),
    await userId(env, "dschrute"),
  ]);
  await expect(page).toHaveURL(/\/admins$/);
  expect(await isAdmin(env, "dschrute")).toBe(true);

  // Adding a passkey removed the admin's password, so no password is asked for.
  await page.goto(`${gamma.url}/delete-your-account`);
  await page.getByRole("button", { name: "Delete the account" }).click();
  await expect(page.getByText(refused)).toBeVisible();
  expect(await userId(env, gamma.adminCid!)).not.toBe("");

  await page.goto(`${gamma.url}/users`);
  await rowWithText(page, gamma.adminCid!)
    .getByRole("link", { name: "Details" })
    .click();
  page.once("dialog", (dialog) => dialog.accept());
  await articleByHeading(page, "User details")
    .getByRole("button", { name: "Delete user", exact: true })
    .click();
  await expect(page.getByText(refused)).toBeVisible();
  expect(await isAdmin(env, gamma.adminCid!)).toBe(true);
  expect(await usableAdmins(env)).toBe(1);

  // Nothing was revoked by the refused deletions.
  await page.goto(`${gamma.url}/users`);
  await expect(page.getByText("Hey, admin!")).toBeVisible();
});

test("an admin locking the other admin while being demoted by them leaves one admin who can sign in", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  const other = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const jim = await other.newPage();
    await login(jim, gamma.url, "jhalpert", "password1337", "Big Tuna");
    await jim.goto(`${gamma.url}/me/security`);
    await addPasskey(jim, "Jim");
    await expect(jim.getByText("The passkey was added.")).toBeVisible();

    await login(
      page,
      gamma.url,
      gamma.adminCid!,
      gamma.adminPassword!,
      "admin",
    );
    const me = await userId(env, gamma.adminCid!);
    const jimId = await userId(env, "jhalpert");
    await saveAdmins(page, gamma.url, [me, jimId]);
    await expect(page).toHaveURL(/\/admins$/);
    expect(await usableAdmins(env)).toBe(2);

    await jim.goto(gamma.url);
    await logout(jim);
    await signInWithPasskey(jim);
    await jim.goto(`${gamma.url}/admins`);
    const jimSave = jim.getByRole("button", { name: "Save" });
    const demoteAdmin = { ...(await formValuesOf(jimSave)), [jimId]: "on" };
    delete demoteAdmin[me];

    await page.goto(`${gamma.url}/users/${jimId}`);
    const lockJim = await formValuesOf(
      articleByHeading(page, "User details").getByRole("button", {
        name: "Lock user",
        exact: true,
      }),
    );

    const [locked, demoted] = await Promise.all([
      page.request.post(`${gamma.url}/users/${jimId}/lock`, {
        form: lockJim,
        headers: { "HX-Request": "true" },
        maxRedirects: 0,
      }),
      jim.request.post(`${gamma.url}/admins`, {
        form: demoteAdmin,
        maxRedirects: 0,
      }),
    ]);
    expect(locked.status()).toBeLessThan(500);
    expect(demoted.status()).toBeLessThan(500);

    expect(await usableAdmins(env)).toBe(1);
  } finally {
    await other.close();
  }
});
