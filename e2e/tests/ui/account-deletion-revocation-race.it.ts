import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { addPasskey } from "../../helpers/passkeys";
import { csrfTokenOf } from "../../helpers/a11y";

for (const { method, clearPassword, scenario } of [
  { method: "password", clearPassword: true, scenario: "password credentials" },
  { method: "passkey", clearPassword: true, scenario: "passkey credentials" },
  {
    method: "password",
    clearPassword: false,
    scenario: "a revoked password session",
  },
] as const) {
  test(`account deletion rechecks ${scenario} after waiting for a concurrent recovery`, async ({
    page,
    gamma,
    env,
  }) => {
    const sql = async (statement: string) => {
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
    };
    await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
    if (method === "passkey") {
      await page.goto(`${gamma.url}/me/security`);
      page.once("dialog", (dialog) => dialog.accept());
      await addPasskey(page, "Deletion race device");
      await expect(
        page.getByRole("row", { name: /Deletion race device/ }),
      ).toBeVisible();
    }
    await page.goto(`${gamma.url}/delete-your-account`);
    const csrf = await csrfTokenOf(
      page.getByRole("button", { name: "Delete the account", exact: true }),
    );
    // Keep recovery's credential/session changes uncommitted while deletion
    // passes request authorization. It must check again after obtaining the lock.
    const recovery = sql(`
      SET application_name = 'gamma-recovery-race';
      BEGIN;
      UPDATE g_user SET ${clearPassword ? "password=NULL," : ""} sessions_revoked_at=clock_timestamp() WHERE cid='jhalpert';
      SELECT pg_sleep(6);
      COMMIT;
    `);
    await expect
      .poll(() =>
        sql(
          "SELECT count(*) FROM pg_stat_activity WHERE application_name='gamma-recovery-race' AND wait_event='PgSleep'",
        ),
      )
      .toBe("1");
    const deletion = page.request.post(`${gamma.url}/delete-your-account`, {
      form: {
        _csrf: csrf,
        _method: "delete",
        password: method === "password" ? "password1337" : "",
        clientDataDeleted: "true",
      },
      maxRedirects: 0,
    });
    await expect
      .poll(() =>
        sql(
          "SELECT count(*) FROM pg_stat_activity WHERE wait_event_type='Lock' AND query ILIKE '%g_user%' AND application_name<>'gamma-recovery-race'",
        ),
      )
      .toBe("1");
    await recovery;
    const response = await deletion;
    expect(response.status()).toBeLessThan(500);
    expect(response.headers().location ?? "").not.toContain("deleted");
    expect(await sql("SELECT count(*) FROM g_user WHERE cid='jhalpert'")).toBe(
      "1",
    );
  });
}
