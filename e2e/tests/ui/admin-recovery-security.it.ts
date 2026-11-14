import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import {
  issueAdminRecoveryLink,
  startAdminRecovery,
} from "../../helpers/admin-recovery";
import { articleByHeading, csrfTokenOf, rowWithText } from "../../helpers/a11y";
import {
  addPasskey,
  signInWithPasskey,
  useAnotherVirtualAuthenticator,
} from "../../helpers/passkeys";
import { saveRecoveryKey } from "../../helpers/recovery-key";
import { login } from "../../helpers/auth";
import { waitForMailLink } from "../../helpers/mail";
import type { GammaEnvironment } from "../../gamma-setup";

async function sql(env: GammaEnvironment, statement: string): Promise<string> {
  const result = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-v",
    "ON_ERROR_STOP=1",
    "-At",
    "-c",
    statement,
  ]);
  expect(result.exitCode, result.stderr).toBe(0);
  return result.stdout.trim();
}

test("admin recovery links require CSRF and can only be consumed once, including concurrently", async ({
  page,
  browser,
  gamma,
}) => {
  const link = await issueAdminRecoveryLink(page, gamma, "jhalpert");
  const token = new URL(link).searchParams.get("token")!;
  const contexts = await Promise.all([
    browser.newContext({ ignoreHTTPSErrors: true }),
    browser.newContext({ ignoreHTTPSErrors: true }),
  ]);
  try {
    const pages = await Promise.all(contexts.map((c) => c.newPage()));
    for (const p of pages) await p.goto(link);
    const noCsrf = await pages[0]!.request.post(`${gamma.url}/admin-recovery`, {
      form: { token },
      maxRedirects: 0,
    });
    expect(noCsrf.status()).toBe(403);
    const csrf = await Promise.all(
      pages.map((p) =>
        csrfTokenOf(
          p.getByRole("button", { name: "Set up new sign-in credentials" }),
        ),
      ),
    );
    const responses = await Promise.all(
      pages.map((p, i) =>
        p.request.post(`${gamma.url}/admin-recovery`, {
          form: { token, _csrf: csrf[i]! },
          maxRedirects: 0,
        }),
      ),
    );
    expect(
      responses.filter(
        (r) =>
          r.status() === 302 &&
          r.headers().location?.endsWith("/sign-in-setup"),
      ),
    ).toHaveLength(1);
    expect(responses.filter((r) => r.status() === 200)).toHaveLength(1);
    const winner = responses.findIndex((r) => r.status() === 302);
    await pages[winner]!.goto(`${gamma.url}/me/security`);
    await expect(
      pages[winner]!.getByRole("heading", {
        name: "Sign in and security",
        exact: true,
      }),
    ).toHaveCount(0);
    await pages[1 - winner]!.goto(link);
    await expect(
      pages[1 - winner]!.getByRole("button", {
        name: "Set up new sign-in credentials",
      }),
    ).toHaveCount(0);
  } finally {
    await Promise.all(contexts.map((c) => c.close()));
  }
});

test("admin and emailed recovery tokens cannot be substituted for each other", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  const adminLink = await issueAdminRecoveryLink(page, gamma, "jhalpert");
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await context.newPage();
    const adminToken = new URL(adminLink).searchParams.get("token")!;
    await user.goto(
      `${gamma.url}/forgot-password/finalize?token=${adminToken}`,
    );
    await expect(
      user.getByRole("button", { name: "Reset password", exact: true }),
    ).toHaveCount(0);
    await user.goto(`${gamma.url}/forgot-password`);
    await user.getByLabel("Cid or Email", { exact: true }).fill("dschrute");
    await user.getByRole("button", { name: /reset|send/i }).click();
    const emailed = await waitForMailLink(
      env.gotify,
      gamma.url,
      "dschrute@example.org",
      "/forgot-password/finalize",
    );
    await user.goto(
      `${gamma.url}/admin-recovery?token=${new URL(emailed).searchParams.get("token")!}`,
    );
    await expect(
      user.getByRole("button", { name: "Set up new sign-in credentials" }),
    ).toHaveCount(0);
    await startAdminRecovery(user, adminLink);
  } finally {
    await context.close();
  }
});

test("a replacement admin recovery link invalidates the old link and links expire after 15 minutes", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  const oldLink = await issueAdminRecoveryLink(page, gamma, "jhalpert");
  const article = articleByHeading(page, "How the user signs in");
  page.once("dialog", (d) => d.accept());
  await article
    .getByRole("button", { name: "Reset sign-in and generate recovery link" })
    .click();
  await expect(article.getByRole("code")).not.toHaveText(oldLink);
  const newLink = (await article.getByRole("code").innerText()).trim();
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await context.newPage();
    await user.goto(oldLink);
    await expect(
      user.getByRole("button", { name: "Set up new sign-in credentials" }),
    ).toHaveCount(0);
    await user.goto(newLink);
    await expect(
      user.getByRole("button", { name: "Set up new sign-in credentials" }),
    ).toBeVisible();
    await sql(
      env,
      "UPDATE g_password_reset SET created_at=NOW() - INTERVAL '16 minutes'",
    );
    await user.reload();
    await expect(
      user.getByRole("button", { name: "Set up new sign-in credentials" }),
    ).toHaveCount(0);
  } finally {
    await context.close();
  }
});

test("recovery links and setup sessions reject locked accounts and newly promoted admins", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  const link = await issueAdminRecoveryLink(page, gamma, "jhalpert");
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await context.newPage();
    await user.goto(link);
    const csrf = await csrfTokenOf(
      user.getByRole("button", { name: "Set up new sign-in credentials" }),
    );
    const token = new URL(link).searchParams.get("token")!;
    await sql(env, "UPDATE g_user SET locked=TRUE WHERE cid='jhalpert'");
    const locked = await user.request.post(`${gamma.url}/admin-recovery`, {
      form: { token, _csrf: csrf },
      maxRedirects: 0,
    });
    expect(locked.status()).toBe(200);
    expect(await locked.text()).not.toContain("Choose a new way to sign in");
    await sql(
      env,
      "UPDATE g_user SET locked=FALSE WHERE cid='jhalpert'; INSERT INTO g_admin_user(user_id,created_at) SELECT user_id,NOW() FROM g_user WHERE cid='jhalpert'",
    );
    const promoted = await user.request.post(`${gamma.url}/admin-recovery`, {
      form: { token, _csrf: csrf },
      maxRedirects: 0,
    });
    expect(promoted.status()).toBe(200);
    expect(
      await sql(
        env,
        "SELECT COUNT(*) FROM g_password_reset WHERE admin_recovery",
      ),
    ).toBe("1");
    await sql(
      env,
      "DELETE FROM g_admin_user WHERE user_id=(SELECT user_id FROM g_user WHERE cid='jhalpert')",
    );
    await startAdminRecovery(user, link);
    await sql(
      env,
      "INSERT INTO g_admin_user(user_id,created_at) SELECT user_id,NOW() FROM g_user WHERE cid='jhalpert'",
    );
    await user.reload();
    await expect(
      user.getByRole("button", { name: "Use a password" }),
    ).toHaveCount(0);
  } finally {
    await context.close();
  }
});

test("admin recovery revokes existing sessions, pending email changes, and old password reset links", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await context.newPage();
    await user.goto(`${gamma.url}/forgot-password`);
    await user.getByLabel("Cid or Email", { exact: true }).fill("jhalpert");
    await user.getByRole("button", { name: /reset|send/i }).click();
    const oldResetLink = await waitForMailLink(
      env.gotify,
      gamma.url,
      "jhalpert@example.org",
      "/forgot-password/finalize",
    );
    await login(user, gamma.url, "jhalpert", "password1337", "Big Tuna");
    await user.getByRole("button", { name: "Change email" }).click();
    await user
      .getByLabel("New email", { exact: true })
      .fill("pending.current@example.org");
    await user
      .getByLabel("Current password", { exact: true })
      .fill("password1337");
    await user.getByRole("button", { name: "Send confirmation link" }).click();
    await expect(user.getByText("We sent a confirmation link")).toBeVisible();
    expect(await sql(env, "SELECT COUNT(*) FROM g_email_change")).toBe("1");
    await issueAdminRecoveryLink(page, gamma, "jhalpert");
    expect(await sql(env, "SELECT COUNT(*) FROM g_email_change")).toBe("0");
    await user.goto(oldResetLink);
    await expect(
      user.getByRole("button", { name: "Reset password", exact: true }),
    ).toHaveCount(0);
    await user.goto(gamma.url);
    await expect(user).toHaveURL(/\/login/);
    await user.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
    await user.getByLabel("Password", { exact: true }).fill("password1337");
    await user.getByRole("button", { name: "Login", exact: true }).click();
    await expect(user).toHaveURL(/\/login/);
    await expect(user.getByText("Hey, Big Tuna!")).toHaveCount(0);
  } finally {
    await context.close();
  }
});

test("another admin can be demoted, recovered without email, and restored after enrolling a new passkey", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await context.newPage();
    await login(user, gamma.url, "jhalpert", "password1337", "Big Tuna");
    await user.goto(`${gamma.url}/me/security`);
    user.once("dialog", (d) => d.accept());
    await addPasskey(user, "Lost admin device");
    await login(
      page,
      gamma.url,
      gamma.adminCid!,
      gamma.adminPassword!,
      "admin",
    );
    const setAdmin = async (enabled: boolean) => {
      await page.goto(`${gamma.url}/admins`);
      await rowWithText(page, "Big Tuna")
        .getByRole("checkbox")
        .setChecked(enabled);
      await Promise.all([
        page.waitForResponse(
          (r) =>
            r.request().method() === "POST" &&
            new URL(r.url()).pathname === "/admins",
        ),
        page.getByRole("button", { name: "Save", exact: true }).click(),
      ]);
      await expect(
        rowWithText(page, "Big Tuna").getByRole("checkbox"),
      ).toBeChecked({ checked: enabled });
    };
    await setAdmin(true);
    const targetId = await sql(
      env,
      "SELECT user_id FROM g_user WHERE cid='jhalpert'",
    );
    await page.goto(`${gamma.url}/users/${targetId}`);
    const article = articleByHeading(page, "How the user signs in");
    page.once("dialog", (d) => d.accept());
    await article
      .getByRole("button", { name: "Reset sign-in and generate recovery link" })
      .click();
    await expect(
      article.getByText("Remove this user's admin rights first", {
        exact: false,
      }),
    ).toBeVisible();
    await expect(article.getByRole("code")).toHaveCount(0);
    await setAdmin(false);
    await page.goto(`${gamma.url}/users/${targetId}`);
    page.once("dialog", (d) => d.accept());
    await article
      .getByRole("button", { name: "Reset sign-in and generate recovery link" })
      .click();
    await expect(article.getByRole("code")).toBeVisible();
    const link = (await article.getByRole("code").innerText()).trim();
    // The old user session was revoked by recovery.
    await user.goto(gamma.url);
    await expect(user).toHaveURL(/\/login/);
    await startAdminRecovery(user, link);
    await useAnotherVirtualAuthenticator(user);
    await addPasskey(user, "Recovered admin device");
    await expect(
      user.getByRole("heading", { name: "Check your email address" }),
    ).toBeVisible();
    await user.getByRole("button", { name: "Create a recovery key" }).click();
    await saveRecoveryKey(user);
    await user.getByRole("button", { name: "Continue to signing in" }).click();
    await expect(user).toHaveURL(/\/login\?sign-in-ready/);
    await setAdmin(true);
    await signInWithPasskey(user);
    await expect(user.getByText("Hey, Big Tuna!")).toBeVisible();
    await user.goto(`${gamma.url}/admins`);
    await expect(
      user.getByRole("button", { name: "Save", exact: true }),
    ).toBeVisible();
  } finally {
    await context.close();
  }
});
