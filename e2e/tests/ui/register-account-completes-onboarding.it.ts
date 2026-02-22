import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { uniqueCid, uniqueEmail } from "../../helpers/strings";

test("given an allowed cid when completing registration then the new user can sign in", async ({
  page,
  gamma,
  env,
}) => {
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const cid = uniqueCid("reg").slice(0, 10);

  await page.goto(`${gamma.url}/allow-list`, { timeout: 30000 });
  await page.fill('input[name="cid"]', cid);

  await Promise.all([
    page.waitForURL("**/allow-list", { timeout: 15000 }),
    page.locator('button[form="allow-cid-form"]').click(),
  ]);

  await logout(page);

  await page.goto(`${gamma.url}/activate-cid`, { timeout: 30000 });
  await page.fill('input[name="cid"]', cid);

  await Promise.all([
    page.waitForURL("**/email-sent", { timeout: 20000 }),
    page.getByRole("button", { name: "Activate cid" }).click(),
  ]);

  await expect(
    page.getByText("An email should be sent to your student email"),
  ).toBeVisible({ timeout: 10000 });

  const token = await waitForActivationToken(env, cid);

  await page.goto(`${gamma.url}/register?token=${token}`, { timeout: 30000 });
  await expect(page.getByText("Finish setting up your account")).toBeVisible({
    timeout: 10000,
  });

  const password = `${uniqueCid("register")}pw`;
  const nick = uniqueCid("nick");

  await page.fill('input[name="email"]', uniqueEmail("newuser"));
  await page.fill('input[name="nick"]', nick);
  await page.fill('input[name="firstName"]', "Register");
  await page.fill('input[name="lastName"]', "Tester");
  await page.fill('input[name="password"]', password);
  await page.fill('input[name="confirmPassword"]', password);
  await page.selectOption('select[name="acceptanceYear"]', "2020");
  await page.selectOption('select[name="language"]', "EN");
  await page.check('input[name="acceptUserAgreement"]');

  await Promise.all([
    page.waitForURL("**/login?account-created", { timeout: 15000 }),
    page.getByRole("button", { name: "Create account" }).click(),
  ]);

  await expect(page.getByText("Your account has been created.")).toBeVisible({
    timeout: 10000,
  });

  await login(page, gamma.url, cid, password, nick);
});

async function waitForActivationToken(
  env: {
    postgres: {
      exec: (
        command: string | string[],
      ) => Promise<{ exitCode: number; output: string }>;
    };
  },
  cid: string,
): Promise<string> {
  for (let attempt = 0; attempt < 30; attempt++) {
    const queryResult = await env.postgres.exec([
      "psql",
      "-U",
      "postgres",
      "-d",
      "postgres",
      "-t",
      "-A",
      "-c",
      `SELECT token FROM g_user_activation WHERE cid = '${cid}' LIMIT 1;`,
    ]);

    if (queryResult.exitCode !== 0) {
      throw new Error(
        `Failed to query activation token. Exit code ${queryResult.exitCode}: ${queryResult.output}`,
      );
    }

    const token = queryResult.output.trim();
    if (token.length > 0) {
      return token;
    }

    await new Promise((resolve) => setTimeout(resolve, 250));
  }

  throw new Error(`Activation token was not created for cid ${cid}`);
}
