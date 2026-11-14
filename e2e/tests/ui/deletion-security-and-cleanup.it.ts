import { execFile } from "node:child_process";
import { promisify } from "node:util";
import { mkdtemp, rm } from "node:fs/promises";
import { tmpdir } from "node:os";
import path from "node:path";
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { csrfTokenOf } from "../../helpers/a11y";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
  exchangeCode,
} from "../../helpers/oauth";
import type { GammaEnvironment } from "../../gamma-setup";

const userId = "bc605869-9a4d-46ec-8a29-d00819d4c195";

async function sql(env: GammaEnvironment, query: string) {
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
}

async function csrf(page: import("@playwright/test").Page) {
  return csrfTokenOf(page.getByRole("button", { name: "Logout", exact: true }));
}

test("withdrawal revokes issued tokens and pending codes, including after reapproval", async ({
  page,
  request,
  gamma,
  env,
}) => {
  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!);
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    "Revocation client",
    { official: true },
  );
  await logout(page);
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const issued = await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "required" },
  );
  const exchanged = await exchangeCode(request, gamma.url, client, issued);
  expect(
    exchanged.status(),
    exchanged.ok() ? "success" : await exchanged.text(),
  ).toBe(200);
  const tokens = (await exchanged.json()) as { access_token: string };
  const pending = await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "none" },
  );
  expect(
    (
      await request.get(`${gamma.url}/oauth2/userinfo`, {
        headers: { Authorization: `Bearer ${tokens.access_token}` },
      })
    ).status(),
  ).toBe(200);
  await page.goto(`${gamma.url}/me/accepted-clients`);
  const token = await csrf(page);
  await Promise.all(
    [1, 2].map(() =>
      page.request.post(
        `${gamma.url}/me/accepted-clients/${client.clientUid}`,
        { form: { _csrf: token, _method: "delete" } },
      ),
    ),
  );
  expect(await sql(env, "SELECT count(*) FROM g_data_deletion_request")).toBe(
    "1",
  );
  expect(
    (
      await request.get(`${gamma.url}/oauth2/userinfo`, {
        headers: { Authorization: `Bearer ${tokens.access_token}` },
      })
    ).status(),
  ).toBe(401);
  await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "required" },
  );
  expect(
    (
      await request.get(`${gamma.url}/oauth2/userinfo`, {
        headers: { Authorization: `Bearer ${tokens.access_token}` },
      })
    ).status(),
  ).toBe(401);
  const staleCode = await exchangeCode(request, gamma.url, client, pending);
  expect(staleCode.status()).toBe(400);
  expect(((await staleCode.json()) as { error: string }).error).toBe(
    "invalid_grant",
  );
  const oldKey = await env.redis.exec([
    "redis-cli",
    "EXISTS",
    `rawToken:access_token:${tokens.access_token}`,
  ]);
  expect(oldKey.output.trim()).toBe("0");

  // Compete at the shared user lock. Either exchange may win, but no token
  // from it may remain usable after the withdrawal request completes.
  for (let attempt = 0; attempt < 3; attempt++) {
    const racingGrant = await authorizeClientWithPkce(
      page,
      gamma.url,
      client.clientId,
      client.redirectUri,
      { consent: "none" },
    );
    await page.goto(`${gamma.url}/me/accepted-clients`);
    const withdrawalCsrf = await csrf(page);
    const [exchange, withdrawal] = await Promise.all([
      exchangeCode(request, gamma.url, client, racingGrant),
      page.request.post(
        `${gamma.url}/me/accepted-clients/${client.clientUid}`,
        { form: { _csrf: withdrawalCsrf, _method: "delete" } },
      ),
    ]);
    expect(withdrawal.status()).toBe(200);
    expect([200, 400]).toContain(exchange.status());
    if (exchange.ok()) {
      const racedToken = (await exchange.json()) as { access_token: string };
      expect(
        (
          await request.get(`${gamma.url}/oauth2/userinfo`, {
            headers: { Authorization: `Bearer ${racedToken.access_token}` },
          })
        ).status(),
      ).toBe(401);
    } else {
      expect(((await exchange.json()) as { error: string }).error).toBe(
        "invalid_grant",
      );
    }
    await authorizeClientWithPkce(
      page,
      gamma.url,
      client.clientId,
      client.redirectUri,
      { consent: "required" },
    );
    expect(
      (await exchangeCode(request, gamma.url, client, racingGrant)).status(),
    ).toBe(400);
  }
});

test("deleting a client owner requires acknowledgement on the server and cascades client data", async ({
  page,
  browser,
  request,
  gamma,
  env,
}) => {
  await login(page, gamma.url, "mscott", "password1337", "Boss");
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    "Owner deletion client",
  );
  const actualOwnerId = await sql(
    env,
    "SELECT user_id FROM g_user WHERE cid = 'mscott'",
  );
  const other = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const otherPage = await other.newPage();
    await login(otherPage, gamma.url, "jhalpert", "password1337", "Big Tuna");
    const grant = await authorizeClientWithPkce(
      otherPage,
      gamma.url,
      client.clientId,
      client.redirectUri,
      { consent: "required" },
    );
    const exchanged = await exchangeCode(request, gamma.url, client, grant);
    expect(
      exchanged.status(),
      exchanged.ok() ? "success" : await exchanged.text(),
    ).toBe(200);
    const tokens = (await exchanged.json()) as { access_token: string };
    await page.goto(`${gamma.url}/delete-your-account`);
    await expect(
      page.getByRole("heading", { name: "Clients you own" }),
    ).toBeVisible();
    await expect(
      page.getByRole("listitem").filter({ hasText: "Owner deletion client" }),
    ).toBeVisible();
    const rejected = await page.request.post(
      `${gamma.url}/delete-your-account`,
      {
        form: {
          _method: "delete",
          _csrf: await csrf(page),
          password: "password1337",
        },
        maxRedirects: 0,
      },
    );
    expect(await rejected.text()).toContain(
      "Confirm that you have deleted the personal data",
    );
    expect(
      await sql(
        env,
        `SELECT count(*) FROM g_user WHERE user_id = '${actualOwnerId}'`,
      ),
    ).toBe("1");
    await page
      .getByLabel(
        "I confirm that I have deleted all personal information about users collected through my clients.",
      )
      .check();
    await page
      .getByLabel("Confirm password", { exact: true })
      .fill("password1337");
    await Promise.all([
      page.waitForURL("**/login?deleted"),
      page
        .getByRole("button", { name: "Delete the account", exact: true })
        .click(),
    ]);
    expect(
      await sql(
        env,
        `SELECT count(*) FROM g_user WHERE user_id = '${actualOwnerId}'`,
      ),
    ).toBe("0");
    for (const table of [
      "g_client",
      "g_client_scope",
      "g_user_approval",
      "g_client_api_key",
    ]) {
      expect(
        await sql(
          env,
          `SELECT count(*) FROM ${table} WHERE client_uid = '${client.clientUid}'`,
        ),
      ).toBe("0");
    }
    expect(
      (
        await request.get(`${gamma.url}/oauth2/userinfo`, {
          headers: { Authorization: `Bearer ${tokens.access_token}` },
        })
      ).status(),
    ).toBe(401);
    expect(
      (
        await env.redis.exec(["redis-cli", "SCARD", "gamma-authorization"])
      ).output.trim(),
    ).toBe("0");
  } finally {
    await other.close();
  }
});

test("withdrawn unofficial-client contacts survive reload, can be dismissed and are removed on reapproval", async ({
  page,
  gamma,
  env,
}) => {
  await login(page, gamma.url, "mscott", "password1337", "Boss");
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    "Contact reminder client",
  );
  await logout(page);
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "required" },
  );
  await page.goto(`${gamma.url}/me/accepted-clients`);
  await page
    .getByRole("row")
    .filter({ hasText: "Contact reminder client" })
    .getByRole("button", { name: "Retract approval" })
    .click();
  await expect(
    page.getByRole("row").filter({ hasText: "Contact reminder client" }),
  ).toHaveCount(0);
  await page.reload();
  await expect(
    page.getByRole("link", { name: "Email mscott@example.org" }),
  ).toBeVisible();
  expect(await sql(env, "SELECT count(*) FROM g_data_deletion_request")).toBe(
    "0",
  );
  await page.goto(`${gamma.url}/delete-your-account`);
  await expect(
    page.getByRole("link", { name: "Email mscott@example.org" }),
  ).toBeVisible();
  await page.goto(`${gamma.url}/me/accepted-clients`);
  await page
    .getByRole("button", {
      name: "Dismiss contact reminder for Contact reminder client",
    })
    .click();
  await expect(
    page.getByRole("link", { name: "Email mscott@example.org" }),
  ).toHaveCount(0);
  expect(
    await sql(env, "SELECT count(*) FROM g_withdrawn_client_contact"),
  ).toBe("0");
  await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "required" },
  );
  await page.goto(`${gamma.url}/me/accepted-clients`);
  await page
    .getByRole("row")
    .filter({ hasText: "Contact reminder client" })
    .getByRole("button", { name: "Retract approval" })
    .click();
  await expect(
    page.getByRole("button", {
      name: "Dismiss contact reminder for Contact reminder client",
    }),
  ).toBeVisible();
  await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "required" },
  );
  expect(
    await sql(env, "SELECT count(*) FROM g_withdrawn_client_contact"),
  ).toBe("0");
});

test("deleted accounts lose stored sessions and avatar files; failed file removal retries", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  const buildDir = await mkdtemp(path.join(tmpdir(), "gamma-upload-probe-"));
  const idle = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    await promisify(execFile)("javac", [
      "--release",
      "17",
      "-d",
      buildDir,
      path.resolve(__dirname, "../../fixtures/java/UploadFileProbe.java"),
    ]);
    await gamma.container.copyFilesToContainer([
      {
        source: path.join(buildDir, "UploadFileProbe.class"),
        target: "/tmp/UploadFileProbe.class",
      },
    ]);
    const probe = async (action: string, uri: string) => {
      const result = await gamma.container.exec([
        "/layers/paketo-buildpacks_bellsoft-liberica/jre/bin/java",
        "-cp",
        "/tmp",
        "UploadFileProbe",
        action,
        uri,
      ]);
      expect(result.exitCode).toBe(0);
      return result.output.trim();
    };
    await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
    const idlePage = await idle.newPage();
    await login(idlePage, gamma.url, "jhalpert", "password1337", "Big Tuna");
    const sessionsBefore = await env.redis.exec([
      "redis-cli",
      "SCARD",
      `spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:${userId}`,
    ]);
    expect(Number(sessionsBefore.output.trim())).toBe(2);
    const sessionIds = (
      await env.redis.exec([
        "redis-cli",
        "SMEMBERS",
        `spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:${userId}`,
      ])
    ).output
      .trim()
      .split(/\s+/);
    for (const id of sessionIds) {
      const stored = await env.redis.exec([
        "redis-cli",
        "--raw",
        "HGETALL",
        `spring:session:sessions:${id}`,
      ]);
      expect(stored.output).not.toContain("jhalpert@example.org");
      expect(stored.output).not.toContain("UserAuthentication");
    }
    const upload = async () => {
      await page.goto(gamma.url);
      await page
        .getByLabel("Choose a new avatar", { exact: true })
        .setInputFiles({
          name: "avatar.png",
          mimeType: "image/png",
          buffer: Buffer.from(
            "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+a9l8AAAAASUVORK5CYII=",
            "base64",
          ),
        });
      await page
        .getByRole("button", { name: "Upload avatar", exact: true })
        .click();
      await expect(
        page.getByText("You have uploaded a new avatar", { exact: true }),
      ).toBeVisible();
      return sql(
        env,
        `SELECT avatar_uri FROM g_user_avatar_uri WHERE user_id = '${userId}'`,
      );
    };
    const first = await upload();
    expect(await probe("exists", first)).toBe("true");
    await probe("block", first);
    const second = await upload();
    expect(second).not.toBe(first);
    await expect
      .poll(
        () =>
          sql(
            env,
            `SELECT count(*) FROM g_image_removal WHERE image_uri = '${first}' AND retry_after > CURRENT_TIMESTAMP`,
          ),
        { timeout: 20000 },
      )
      .toBe("1");
    await probe("unblock", first);
    await sql(
      env,
      `UPDATE g_image_removal SET retry_after = CURRENT_TIMESTAMP WHERE image_uri = '${first}'`,
    );
    await expect
      .poll(() => sql(env, "SELECT count(*) FROM g_image_removal"), {
        timeout: 20000,
      })
      .toBe("0");
    await page.goto(`${gamma.url}/delete-your-account`);
    await page
      .getByLabel("Confirm password", { exact: true })
      .fill("password1337");
    await Promise.all([
      page.waitForURL("**/login?deleted"),
      page
        .getByRole("button", { name: "Delete the account", exact: true })
        .click(),
    ]);
    await expect
      .poll(() => probe("exists", second), { timeout: 20000 })
      .toBe("false");
    expect(await sql(env, "SELECT count(*) FROM g_user_avatar_uri")).toBe("0");
    expect(
      (
        await env.redis.exec([
          "redis-cli",
          "SCARD",
          `spring:session:index:org.springframework.session.FindByIndexNameSessionRepository.PRINCIPAL_NAME_INDEX_NAME:${userId}`,
        ])
      ).output.trim(),
    ).toBe("0");
    for (const id of sessionIds) {
      const expired = await env.redis.exec([
        "redis-cli",
        "--raw",
        "HGETALL",
        `spring:session:sessions:${id}`,
      ]);
      expect(expired.output).not.toContain(userId);
      expect(expired.output).not.toContain("SPRING_SECURITY_CONTEXT");
    }
    await idlePage.goto(gamma.url);
    await expect(
      idlePage.getByLabel("Cid or email", { exact: true }),
    ).toBeVisible();
  } finally {
    await idle.close();
    await rm(buildDir, { recursive: true, force: true });
  }
});
