import { readFile, readdir } from "node:fs/promises";
import path from "node:path";
import {
  expect,
  testWithEnvironment as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { csrfTokenOf } from "../../helpers/a11y";
import { startGammaInstance, stopGammaInstance } from "../../gamma-setup";

test("a populated V11 database upgrades through deletion and authentication migrations without losing records", async ({
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
      "-v",
      "ON_ERROR_STOP=1",
      "-At",
      "-c",
      query,
    ]);
    expect(result.exitCode, result.stderr).toBe(0);
    return result.stdout.trim();
  };
  // Build the released schema directly: current application code needs the new
  // authentication tables and cannot be used to run a deliberately old schema.
  const directory = path.resolve(
    __dirname,
    "../../../app/src/main/resources/db/migration",
  );
  const migrations = (await readdir(directory))
    .map((file) => ({ file, version: Number(/^V(\d+)__/.exec(file)?.[1]) }))
    .filter(({ version }) => version <= 11)
    .sort((a, b) => a.version - b.version);
  expect(migrations).toHaveLength(11);
  for (const { file } of migrations) {
    await sql(await readFile(path.join(directory, file), "utf8"));
  }
  const ownerId = "bc605869-9a4d-46ec-8a29-d00819d4c195";
  const clientId = "63b53871-e6fb-436b-86b8-7aab03d9b356";
  await sql(`
    INSERT INTO g_user (user_id, cid, password, nick, first_name, last_name, email, language, acceptance_year, version, created_at, updated_at)
    VALUES ('${ownerId}', 'legacyowner', '{bcrypt}$legacy', 'Legacy owner', 'Legacy', 'Owner', 'legacy@example.org', 'EN', 2020, 0, now(), now());
    INSERT INTO g_text (text_id, sv, en) VALUES ('${clientId}', 'Legacy description', 'Legacy description');
    INSERT INTO g_client (client_uid, client_id, client_secret, redirect_uri, pretty_name, created_at, description, official, created_by)
    VALUES ('${clientId}', 'legacy-client', '{bcrypt}$legacy', 'https://example.org/callback', 'Migrated owner client', now(), '${clientId}', false, '${ownerId}');
    INSERT INTO g_client_scope (client_uid, scope, created_at) VALUES ('${clientId}', 'openid', now());
    INSERT INTO g_data_deletion_request (data_deletion_request_id, user_id, client_uid, cid, client_name, reason, created_at)
    VALUES ('90b16ea5-b177-426c-9cee-17237dd3eceb', '${ownerId}', '${clientId}', 'legacyowner', 'Migrated owner client', 'ACCOUNT_DELETED', now());
  `);
  const gamma = await startGammaInstance(env, {
    env: {
      SPRING_FLYWAY_BASELINE_ON_MIGRATE: "true",
      SPRING_FLYWAY_BASELINE_VERSION: "11",
    },
  });
  try {
    expect(
      await sql(
        "SELECT max(version::int) FROM flyway_schema_history WHERE version IS NOT NULL",
      ),
    ).toBe("15");
    expect(
      await sql(
        `SELECT cid, email, email_recovery, email_recovery_requires_verification FROM g_user WHERE user_id='${ownerId}'`,
      ),
    ).toBe("legacyowner|legacy@example.org|t|f");
    expect(
      await sql(
        `SELECT count(*) FROM g_client_scope WHERE client_uid='${clientId}'`,
      ),
    ).toBe("1");
    expect(
      await sql(
        "SELECT count(*) FROM g_data_deletion_request WHERE cid='legacyowner'",
      ),
    ).toBe("1");
    const privacy = await page.request.get(`${gamma.url}/privacy`, {
      maxRedirects: 0,
    });
    expect(privacy.status()).toBe(200);
    expect(await privacy.text()).toContain("365 days");
    await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!);
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
      await sql(`SELECT count(*) FROM g_user WHERE user_id='${ownerId}'`),
    ).toBe("0");
    expect(
      await sql(
        `SELECT count(*) FROM g_client_scope WHERE client_uid='${clientId}'`,
      ),
    ).toBe("0");
    // Follow-up deliberately survives deletion of both the owner and their client.
    expect(
      await sql(
        "SELECT count(*) FROM g_data_deletion_request WHERE cid='legacyowner'",
      ),
    ).toBe("1");
  } finally {
    await stopGammaInstance(gamma);
  }
});
