import {
  expect,
  testWithEnvironment as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { csrfTokenOf } from "../../helpers/a11y";
import { createUserClientViaUi } from "../../helpers/oauth";
import { startMockGamma } from "../../helpers/gamma";
import { stopGammaInstance } from "../../gamma-setup";

test("V12 preserves populated V11 records and allows admins to delete client owners", async ({
  page,
  env,
}) => {
  const sql = async (query: string) => {
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
    return result.output.trim();
  };
  // Only bootstrap/use V11 tables before upgrading. The new cleanup worker can log
  // missing-outbox errors in this deliberately old schema; it has no work to erase.
  let gamma = await startMockGamma(env, {
    env: { SPRING_FLYWAY_TARGET: "11", SPRING_JPA_HIBERNATE_DDL_AUTO: "none" },
  });
  let stopped = false;
  try {
    const adminCid = gamma.adminCid!;
    const adminPassword = gamma.adminPassword!;
    await login(page, gamma.url, "mscott", "password1337", "Boss");
    const client = await createUserClientViaUi(
      page,
      gamma.url,
      "Migrated owner client",
    );
    const ownerId = await sql(
      "SELECT user_id FROM g_user WHERE cid = 'mscott'",
    );
    const users = await sql("SELECT count(*) FROM g_user");
    const scopes = await sql(
      `SELECT count(*) FROM g_client_scope WHERE client_uid = '${client.clientUid}'`,
    );
    expect(Number(scopes)).toBeGreaterThan(0);
    await sql(
      `INSERT INTO g_data_deletion_request (data_deletion_request_id, user_id, client_uid, cid, client_name, reason, created_at) VALUES ('90b16ea5-b177-426c-9cee-17237dd3eceb', '${ownerId}', '${client.clientUid}', 'mscott', 'Migrated owner client', 'ACCOUNT_DELETED', CURRENT_TIMESTAMP)`,
    );
    await stopGammaInstance(gamma);
    stopped = true;
    gamma = await startMockGamma(env, {
      waitForAdminCredentials: false,
      waitForBootstrapApiKeys: false,
    });
    stopped = false;
    expect(await sql("SELECT count(*) FROM g_user")).toBe(users);
    expect(
      await sql(
        "SELECT max(version::int) FROM flyway_schema_history WHERE version IS NOT NULL",
      ),
    ).toBe("12");
    expect(
      await sql(
        `SELECT count(*) FROM g_client_scope WHERE client_uid = '${client.clientUid}'`,
      ),
    ).toBe(scopes);
    expect(
      await sql(
        "SELECT count(*) FROM g_data_deletion_request WHERE cid = 'mscott'",
      ),
    ).toBe("1");
    await page.context().clearCookies();
    const privacy = await page.request.get(`${gamma.url}/privacy`, {
      maxRedirects: 0,
    });
    expect(privacy.status()).toBe(200);
    expect(await privacy.text()).toContain("365 days");
    await login(page, gamma.url, adminCid, adminPassword);
    await page.goto(`${gamma.url}/users/${ownerId}`);
    const token = await csrfTokenOf(
      page.getByRole("button", { name: "Delete user", exact: true }),
    );
    const removed = await page.request.post(`${gamma.url}/users/${ownerId}`, {
      headers: { "HX-Request": "true" },
      form: { _csrf: token, _method: "delete" },
      maxRedirects: 0,
    });
    expect(removed.status()).toBe(302);
    expect(
      await sql(`SELECT count(*) FROM g_user WHERE user_id = '${ownerId}'`),
    ).toBe("0");
    expect(
      await sql(
        `SELECT count(*) FROM g_client_scope WHERE client_uid = '${client.clientUid}'`,
      ),
    ).toBe("0");
    // The follow-up request deliberately survives both user and client deletion.
    expect(
      await sql(
        "SELECT count(*) FROM g_data_deletion_request WHERE cid = 'mscott'",
      ),
    ).toBe("1");
  } finally {
    if (!stopped) await stopGammaInstance(gamma);
  }
});
