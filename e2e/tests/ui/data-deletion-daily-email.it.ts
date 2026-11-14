import { expect, testWithMockGammaEnv } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";
import { waitForMail } from "../../helpers/mail";

const test = testWithMockGammaEnv({
  DATA_DELETION_DIGEST_CRON: "*/5 * * * * *",
});

test("given an open data deletion request when the daily email is sent then the recipients get the deadline and a link, but no personal data", async ({
  page,
  gamma,
  env,
}) => {
  const officialName = uniqueLabel("Official");
  const recipient = "dpo@example.org";

  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!);
  const official = await createUserClientViaUi(page, gamma.url, officialName, {
    official: true,
  });
  await page.goto(`${gamma.url}/data-deletion`, { timeout: 30000 });
  await page.getByLabel("Email", { exact: true }).fill(recipient);
  await page.getByRole("button", { name: "Add" }).click();
  await expect(
    page.getByRole("button", { name: `Remove ${recipient}` }),
  ).toBeVisible({ timeout: 10000 });
  await logout(page);

  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await authorizeClientWithPkce(
    page,
    gamma.url,
    official.clientId,
    official.redirectUri,
    { consent: "required" },
  );
  await page.goto(`${gamma.url}/delete-your-account`, { timeout: 30000 });
  await page
    .getByLabel("Confirm password", { exact: true })
    .fill("password1337");
  await Promise.all([
    page.waitForURL("**/login?deleted", { timeout: 15000 }),
    page.getByRole("button", { name: "Delete the account" }).click(),
  ]);

  const body = await waitForMail(
    env.gotify,
    recipient,
    /Action required: 1 data deletion request/,
    30000,
  );
  expect(body).toContain("within one month");
  expect(body).toMatch(/- \d{4}-\d{2}-\d{2}: 1 request, \d+ days? left/);
  expect(body).toMatch(/https?:\/\/\S+\/data-deletion/);
  expect(body).not.toContain("jhalpert");
  expect(body).not.toContain("bc605869-9a4d-46ec-8a29-d00819d4c195");
  expect(body).not.toContain(officialName);
});
