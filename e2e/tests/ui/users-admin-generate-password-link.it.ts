import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { issueAdminRecoveryLink } from "../../helpers/admin-recovery";

test("an admin issues a recovery link while revoking old credentials and leaving email recovery off", async ({
  page,
  gamma,
  env,
}) => {
  const link = await issueAdminRecoveryLink(page, gamma, "jhalpert");
  expect(link).toContain("/admin-recovery?token=");
  const result = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-At",
    "-c",
    "SELECT (password IS NULL), email_recovery, email_recovery_requires_verification, (sessions_revoked_at IS NOT NULL) FROM g_user WHERE cid='jhalpert'",
  ]);
  expect(result.exitCode).toBe(0);
  expect(result.stdout.trim()).toBe("t|f|t|t");
  const token = new URL(link).searchParams.get("token")!;
  const stored = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-At",
    "-c",
    "SELECT token, admin_recovery FROM g_password_reset JOIN g_user USING (user_id) WHERE cid='jhalpert'",
  ]);
  expect(stored.exitCode).toBe(0);
  expect(stored.stdout.trim()).toMatch(/\|t$/);
  // Inspecting the stored hash also proves the bearer link is not persisted in plaintext.
  expect(stored.stdout).not.toContain(token);
});
