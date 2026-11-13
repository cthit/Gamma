import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import type { StartedPostgreSqlContainer } from "@testcontainers/postgresql";
import { login } from "../../helpers/auth";
import { createUserClientViaUi } from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";
import { articleByHeading } from "../../helpers/a11y";

const PBEESLY_ID = "ec8987d7-4087-461d-bed5-9365086b6e3b";

test("editing an authority as its owner keeps the locked users the owner can't see, an admin can remove them", async ({
  page,
  browser,
  env,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    uniqueLabel("Locked members"),
  );

  const createForm = articleByHeading(page, "Create client authority");
  await createForm.getByLabel("Authority name", { exact: true }).fill("kept");
  await createForm.getByRole("button", { name: "Add user authority" }).click();
  await createForm
    .getByRole("combobox", { name: "User", exact: true })
    .selectOption({ label: "Pam-Pam" });
  await createForm.getByRole("button", { name: "Save", exact: true }).click();

  const authority = articleByHeading(page, "kept");
  await expect(authority.getByRole("listitem")).toHaveText(["Pam-Pam"]);

  // There is no UI to lock a user, accounts are locked by migrations.
  await sql(
    env.postgres,
    `UPDATE g_user SET locked = true WHERE user_id = '${PBEESLY_ID}'`,
  );
  const isMember = async () =>
    (await sql(
      env.postgres,
      `SELECT count(*) FROM g_client_authority_user
       WHERE client_uid = '${client.clientUid}' AND authority_name = 'kept'
         AND user_id = '${PBEESLY_ID}'`,
    )) === "1";

  // The owner can't see the locked user, so the edit form doesn't list them.
  await page.reload();
  await expect(authority.getByText("No users", { exact: true })).toBeVisible();
  await authority.getByRole("button", { name: "Edit", exact: true }).click();
  const ownerForm = articleByHeading(page, "Edit kept");
  await ownerForm.getByRole("button", { name: "Add user authority" }).click();
  await ownerForm
    .getByRole("combobox", { name: "User", exact: true })
    .selectOption({ label: "Big Tuna" });
  await ownerForm.getByRole("button", { name: "Save", exact: true }).click();
  await expect(authority.getByRole("listitem")).toHaveText(["Big Tuna"]);

  expect(await isMember()).toBe(true);

  // An admin sees the locked user and can remove them.
  const adminContext = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const admin = await adminContext.newPage();
    await login(admin, gamma.url, gamma.adminCid!, gamma.adminPassword!);
    await admin.goto(`${gamma.url}/clients/${client.clientUid}`);

    const adminAuthority = articleByHeading(admin, "kept");
    await expect(adminAuthority.getByRole("listitem")).toHaveText([
      "Big Tuna",
      "Pam-Pam",
    ]);
    await adminAuthority
      .getByRole("button", { name: "Edit", exact: true })
      .click();
    const adminForm = articleByHeading(admin, "Edit kept");
    const users = adminForm.getByRole("combobox", {
      name: "User",
      exact: true,
    });
    await expect(users).toHaveCount(2);
    await expect(users.nth(1)).toHaveValue(PBEESLY_ID);
    await adminForm
      .getByRole("button", { name: "Delete user", exact: true })
      .nth(1)
      .click();
    await adminForm.getByRole("button", { name: "Save", exact: true }).click();
    await expect(adminAuthority.getByRole("listitem")).toHaveText(["Big Tuna"]);
  } finally {
    await adminContext.close();
  }

  expect(await isMember()).toBe(false);
});

async function sql(
  postgres: StartedPostgreSqlContainer,
  query: string,
): Promise<string> {
  const result = await postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-tAc",
    query,
  ]);
  if (result.exitCode !== 0) {
    throw new Error(`psql failed: ${result.stderr}`);
  }
  return result.stdout.trim();
}
