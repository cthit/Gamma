import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid } from "../../helpers/strings";

test("an admin can select and save a new member beyond the first directory page", async ({
  page,
  gamma,
  env,
}) => {
  const seeded = await env.postgres.exec([
    "psql",
    "--username",
    "postgres",
    "--dbname",
    env.databaseName,
    "--command",
    `INSERT INTO g_user (user_id, cid, nick, first_name, last_name, email, acceptance_year, version, created_at, updated_at)
     SELECT gen_random_uuid(), 'candidate' || chr(97 + n / 26) || chr(97 + n % 26), 'Candidate ' || n,
            'Candidate', 'Member', 'candidate' || n || '@example.org', 2020, 0, NOW(), NOW()
     FROM generate_series(1, 205) AS n;
     UPDATE g_user SET cid = 'zzcandidate', nick = 'Last directory candidate' WHERE cid = 'jhalpert';`,
  ]);
  expect(seeded.exitCode).toBe(0);
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );
  await page.goto(`${gamma.url}/groups/create`);
  await page.fill('input[name="name"]', uniqueCid("group"));
  await page.fill('input[name="prettyName"]', "Later directory member");
  await page.getByRole("button", { name: "Create", exact: true }).click();
  await page.getByRole("button", { name: "Edit", exact: true }).click();
  await page.getByRole("button", { name: "Add member" }).click();
  const members = page.locator('select[name="userId"]');
  await expect
    .poll(() => members.locator("option").count())
    .toBeGreaterThan(200);
  await members.selectOption({ label: "Last directory candidate" });
  await page.getByRole("button", { name: "Save", exact: true }).click();
  await page.reload();
  await expect(
    page
      .getByRole("listitem")
      .filter({ hasText: "Last directory candidate -" }),
  ).toBeVisible();
  await page.getByRole("button", { name: "Edit", exact: true }).click();
  await expect(page.locator('select[name="userId"] option:checked')).toHaveText(
    "Last directory candidate",
  );
});
