import { createHash, randomBytes } from "node:crypto";
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
  exchangeCode,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";

test("given a client whose owner is locked when a new user tries to approve it then it is refused, while users that already approved it can still sign in", async ({
  page,
  gamma,
  env,
  request,
}) => {
  const clientName = uniqueLabel("Scott app");
  await login(page, gamma.url, "mscott", "password1337", "Boss");
  const client = await createUserClientViaUi(page, gamma.url, clientName);
  await logout(page);

  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "required" },
  );
  await page.goto(gamma.url, { timeout: 30000 });
  await logout(page);

  await login(page, gamma.url, "pbeesly", "password1337", "Pam-Pam");
  const authorizeUrl = new URL("/oauth2/authorize", gamma.url);
  authorizeUrl.search = new URLSearchParams({
    response_type: "code",
    client_id: client.clientId,
    redirect_uri: client.redirectUri,
    scope: "openid profile email",
    state: randomBytes(16).toString("hex"),
    code_challenge: createHash("sha256")
      .update(randomBytes(32).toString("base64url"))
      .digest("base64url"),
    code_challenge_method: "S256",
  }).toString();
  await page.goto(authorizeUrl.toString(), { timeout: 30000 });
  const authorizeButton = page.getByRole("button", { name: "Authorize" });
  await expect(authorizeButton).toBeVisible({ timeout: 10000 });

  const lock = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-c",
    "UPDATE g_user SET locked = true WHERE cid = 'mscott'",
  ]);
  expect(lock.exitCode).toBe(0);

  const [consentResponse] = await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        new URL(response.url()).pathname === "/oauth2/authorize",
    ),
    authorizeButton.click(),
  ]);
  expect(consentResponse.status()).toBe(400);

  await page.goto(`${gamma.url}/me/accepted-clients`, { timeout: 30000 });
  await expect(
    page.getByRole("row").filter({ hasText: clientName }),
  ).toHaveCount(0);

  await page.goto(authorizeUrl.toString(), { timeout: 30000 });
  await expect(
    page.getByRole("heading", { name: "Client can't be approved" }),
  ).toBeVisible({ timeout: 10000 });
  await expect(authorizeButton).toHaveCount(0);
  await page.goto(gamma.url, { timeout: 30000 });
  await logout(page);

  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const grant = await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "none" },
  );
  expect((await exchangeCode(request, gamma.url, client, grant)).status()).toBe(
    200,
  );
});
