import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import type { GammaEnvironment } from "../../gamma-setup";

async function executeSql(env: GammaEnvironment, sql: string): Promise<void> {
  const result = await env.postgres.exec([
    "psql",
    "--username",
    "postgres",
    "--dbname",
    env.databaseName,
    "--command",
    sql,
  ]);

  if (result.exitCode !== 0) {
    throw new Error(
      `Could not update E2E access state: ${result.output.trim()}`,
    );
  }
}

test("given a signed in user when the account is locked then the next request logs them out", async ({
  page,
  gamma,
  env,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  await executeSql(
    env,
    "UPDATE g_user SET locked = TRUE WHERE cid = 'jhalpert'",
  );
  await page.goto(`${gamma.url}/me`, { timeout: 30000 });

  await expect(page.locator('input[name="username"]')).toBeVisible({
    timeout: 10000,
  });
  await expect(page).toHaveURL(/\/login/);
});

test("given a signed in admin when the user is demoted then admin pages are denied", async ({
  page,
  gamma,
  env,
}) => {
  await executeSql(
    env,
    "INSERT INTO g_admin_user (created_at, user_id) " +
      "SELECT CURRENT_TIMESTAMP, user_id FROM g_user WHERE cid = 'jhalpert'",
  );
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(`${gamma.url}/users`, { timeout: 30000 });
  await expect(page.locator('form[action="/users"]')).toBeVisible({
    timeout: 10000,
  });

  await executeSql(
    env,
    "DELETE FROM g_admin_user " +
      "WHERE user_id = (SELECT user_id FROM g_user WHERE cid = 'jhalpert')",
  );
  await page.goto(`${gamma.url}/users`, { timeout: 30000 });

  await expect(page.getByText("403 - Unauthorized")).toBeVisible({
    timeout: 10000,
  });
  await expect(
    page.getByText("You are not authorized to view this page."),
  ).toBeVisible({ timeout: 10000 });
});
