/// <reference lib="dom" />
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import {
  addPasskey,
  signInWithPasskey,
  useVirtualAuthenticator,
  useAnotherVirtualAuthenticator,
} from "../../helpers/passkeys";
import { currentTimeStep, totpCode } from "../../helpers/totp";
import { saveRecoveryKey } from "../../helpers/recovery-key";
import { waitForMailLink, waitForMail } from "../../helpers/mail";
import {
  createUserClientViaUi,
  authorizeClientWithPkce,
  exchangeCode,
} from "../../helpers/oauth";
import { readFile } from "node:fs/promises";
import path from "node:path";
import type { Page } from "@playwright/test";
import { csrfTokenOf } from "../../helpers/a11y";
import { startMockGamma } from "../../helpers/gamma";
import { stopGammaInstance } from "../../gamma-setup";

test("OAuth token revocation accepts an untyped token and removes UserInfo access", async ({
  page,
  request,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    "Token revocation",
  );
  const grant = await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
  );
  const issued = await exchangeCode(request, gamma.url, client, grant);
  expect(issued.status()).toBe(200);
  const { access_token: token } = (await issued.json()) as {
    access_token: string;
  };
  const headers = {
    Authorization: `Basic ${Buffer.from(`${client.clientId}:${client.clientSecret}`).toString("base64")}`,
  };
  const unknown = await request.post(`${gamma.url}/oauth2/revoke`, {
    headers,
    form: { token: "unknown-token" },
  });
  expect(unknown.status()).toBe(200);
  const revoked = await request.post(`${gamma.url}/oauth2/revoke`, {
    headers,
    form: { token },
  });
  expect(revoked.status()).toBe(200);
  const userinfo = await request.get(`${gamma.url}/oauth2/userinfo`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  expect(userinfo.status()).toBe(401);
});

test("email passkey recovery revokes old credentials before registering a replacement", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(`${gamma.url}/me/security`);
  page.once("dialog", (d) => d.accept());
  await addPasskey(page, "Old device");
  await expect(page.getByRole("row", { name: /Old device/ })).toBeVisible();
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const recovery = await context.newPage();
    await recovery.goto(`${gamma.url}/forgot-password`);
    await recovery.getByLabel("Cid or Email", { exact: true }).fill("jhalpert");
    await recovery.getByRole("button", { name: /reset|send/i }).click();
    await recovery.goto(
      await waitForMailLink(
        env.gotify,
        gamma.url,
        "jhalpert@example.org",
        "/forgot-password/finalize",
      ),
    );
    await recovery
      .getByRole("button", { name: "Set up a passkey", exact: true })
      .click();
    await expect(recovery).toHaveURL(/\/sign-in-setup/);
    await page.goto(`${gamma.url}/me/security`);
    await expect(page).toHaveURL(/\/login/);
    await page.getByRole("button", { name: "Sign in with a passkey" }).click();
    await expect(
      page.getByText("That didn't work. Try again.", { exact: true }),
    ).toBeVisible();
    await addPasskey(recovery, "Replacement device");
    await expect(
      recovery.getByRole("heading", { name: "You're all set" }),
    ).toBeVisible();
    await recovery
      .getByRole("button", { name: "Continue to signing in" })
      .click();
    await signInWithPasskey(recovery);
    await expect(recovery.getByText("Hey, Big Tuna!")).toBeVisible();
    await recovery.goto(`${gamma.url}/me/security`);
    await expect(recovery.getByRole("row", { name: /Old device/ })).toHaveCount(
      0,
    );
    await expect(
      recovery.getByRole("row", { name: /Replacement device/ }),
    ).toBeVisible();
  } finally {
    await context.close();
  }
});

test("replacing an authenticator app revokes old completed MFA sessions", async ({
  page,
  browser,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const { secret, step } = await enableTotp(page, gamma.url);
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const other = await context.newPage();
    await rawPassword(other, gamma.url);
    await other
      .getByLabel("Six digit code", { exact: true })
      .fill(totpCode(secret, Math.max(step, currentTimeStep()) + 1));
    await other.getByRole("button", { name: "Continue", exact: true }).click();
    await expect(other.getByText("Hey, Big Tuna!")).toBeVisible();
    const csrf = await csrfTokenOf(
      page.getByRole("button", { name: "Remove authenticator app" }),
    );
    const start = await page.request.post(
      `${gamma.url}/me/security/totp/start`,
      { form: { _csrf: csrf }, maxRedirects: 0 },
    );
    expect(start.status()).toBe(302);
    await page.goto(`${gamma.url}/me/security/totp`);
    const replacement = (
      await page
        .getByLabel("Key to enter in the app", { exact: true })
        .innerText()
    ).replace(/\s/g, "");
    await page
      .getByLabel("Code from the app", { exact: true })
      .fill(totpCode(replacement, currentTimeStep()));
    await page.getByRole("button", { name: "Turn on", exact: true }).click();
    await expect(page).toHaveURL(/\/login\?security-changed/);
    await other.goto(`${gamma.url}/me/security`);
    await expect(other).toHaveURL(/\/login/);
  } finally {
    await context.close();
  }
});

test("startup encrypts legacy TOTP secrets and existing codes still work", async ({
  page,
  gamma,
  env,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const { secret, step } = await enableTotp(page, gamma.url);
  const legacy = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-v",
    "ON_ERROR_STOP=1",
    "-c",
    `UPDATE g_user_totp SET secret='${secret}' WHERE user_id=(SELECT user_id FROM g_user WHERE cid='jhalpert')`,
  ]);
  expect(legacy.exitCode).toBe(0);
  const upgraded = await startMockGamma(env, {
    waitForAdminCredentials: false,
    waitForBootstrapApiKeys: false,
  });
  try {
    const stored = await env.postgres.exec([
      "psql",
      "-U",
      "postgres",
      "-d",
      "postgres",
      "-At",
      "-c",
      "SELECT secret FROM g_user_totp WHERE user_id=(SELECT user_id FROM g_user WHERE cid='jhalpert')",
    ]);
    expect(stored.stdout.trim()).toMatch(/^v1:/);
    expect(stored.stdout).not.toContain(secret);
    await page.context().clearCookies();
    await rawPassword(page, upgraded.url);
    await page
      .getByLabel("Six digit code", { exact: true })
      .fill(totpCode(secret, Math.max(step, currentTimeStep()) + 1));
    await page.getByRole("button", { name: "Continue", exact: true }).click();
    await expect(page.getByText("Hey, Big Tuna!")).toBeVisible();
  } finally {
    await stopGammaInstance(upgraded);
  }
});

async function rawPassword(page: Page, baseUrl: string) {
  await page.goto(`${baseUrl}/login`);
  await page.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
  await page.getByLabel("Password", { exact: true }).fill("password1337");
  await page.getByRole("button", { name: "Login", exact: true }).click();
}

async function enableTotp(page: Page, baseUrl: string) {
  await page.goto(`${baseUrl}/me/security`);
  await page
    .getByRole("button", { name: "Set up an authenticator app" })
    .click();
  const secret = (
    await page
      .getByLabel("Key to enter in the app", { exact: true })
      .innerText()
  ).replace(/\s/g, "");
  const step = currentTimeStep();
  await page
    .getByLabel("Code from the app", { exact: true })
    .fill(totpCode(secret, step));
  await page.getByRole("button", { name: "Turn on", exact: true }).click();
  const key = await saveRecoveryKey(page);
  await expect(
    page.getByText("Your authenticator app is set up."),
  ).toBeVisible();
  return { secret, step, key };
}

test("removing a passkey revokes sessions and prevents the removed device enrolling a replacement", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(`${gamma.url}/me/security`);
  page.once("dialog", (d) => d.accept());
  await addPasskey(page, "Lost laptop");
  await expect(
    page.getByText("The passkey was added. You sign in with passkeys only."),
  ).toBeVisible();
  const id = await useVirtualAuthenticator(page);
  const cdp = await page.context().newCDPSession(page);
  const { credentials } = await cdp.send("WebAuthn.getCredentials", {
    authenticatorId: id,
  });
  await useAnotherVirtualAuthenticator(page);
  await addPasskey(page, "Safe phone");
  await expect(page.getByRole("row", { name: /Safe phone/ })).toBeVisible();

  const ctx = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const attacker = await ctx.newPage();
    const aid = await useVirtualAuthenticator(attacker);
    const acdp = await ctx.newCDPSession(attacker);
    for (const credential of credentials)
      await acdp.send("WebAuthn.addCredential", {
        authenticatorId: aid,
        credential,
      });
    await attacker.goto(`${gamma.url}/login`);
    await signInWithPasskey(attacker);
    await expect(attacker.getByText("Hey, Big Tuna!")).toBeVisible();
    page.once("dialog", (d) => d.accept());
    await page
      .getByRole("row", { name: /Lost laptop/ })
      .getByRole("button", { name: "Remove" })
      .click();
    await expect(page).toHaveURL(/\/login\?security-changed/);
    await attacker.goto(`${gamma.url}/me/security`);
    await expect(attacker).toHaveURL(/\/login/);
    await signInWithPasskey(page);
    await page.goto(`${gamma.url}/me/security`);
    await expect(page.getByRole("row", { name: /Lost laptop/ })).toHaveCount(0);
    await expect(page.getByRole("row", { name: /Safe phone/ })).toBeVisible();
    const notice = await waitForMail(
      env.gotify,
      "jhalpert@example.org",
      /sign-in security/,
    );
    expect(notice).toContain("A passkey was removed");
  } finally {
    await ctx.close();
  }
});

test("email recovery removes old passkeys and revokes their sessions", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(`${gamma.url}/me/security`);
  page.once("dialog", (d) => d.accept());
  await addPasskey(page, "Compromised laptop");
  await expect(
    page.getByRole("row", { name: /Compromised laptop/ }),
  ).toBeVisible();
  const ctx = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const recovery = await ctx.newPage();
    await recovery.goto(`${gamma.url}/forgot-password`);
    await recovery.getByLabel("Cid or Email", { exact: true }).fill("jhalpert");
    await recovery.getByRole("button", { name: /reset|send/i }).click();
    const link = await waitForMailLink(
      env.gotify,
      gamma.url,
      "jhalpert@example.org",
      "/forgot-password/finalize",
    );
    await recovery.goto(link);
    await recovery
      .getByLabel("Password", { exact: true })
      .fill("Recovered-password-1337");
    await recovery
      .getByLabel("Confirm password", { exact: true })
      .fill("Recovered-password-1337");
    await recovery.getByRole("button", { name: /reset|save|change/i }).click();
    await expect(recovery).toHaveURL(/\/login\?password-reset/);
    await page.goto(`${gamma.url}/me/security`);
    await expect(page).toHaveURL(/\/login/);
    await page.getByRole("button", { name: "Sign in with a passkey" }).click();
    await expect(
      page.getByText("That didn't work. Try again.", { exact: true }),
    ).toBeVisible();
    await login(
      recovery,
      gamma.url,
      "jhalpert",
      "Recovered-password-1337",
      "Big Tuna",
    );
    await recovery.goto(`${gamma.url}/me/security`);
    await expect(recovery.getByText("You have no passkeys yet.")).toBeVisible();
  } finally {
    await ctx.close();
  }
});

test("a partial password sign-in cannot authorize previously approved OAuth client", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    "MFA bypass probe",
  );
  await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
  );
  await page.goto(`${gamma.url}/`);
  await enableTotp(page, gamma.url);
  await logout(page);
  await rawPassword(page, gamma.url);
  await expect(page).toHaveURL(/\/login\/totp/);
  const url = new URL("/oauth2/authorize", gamma.url);
  url.search = new URLSearchParams({
    response_type: "code",
    client_id: client.clientId,
    redirect_uri: client.redirectUri,
    scope: "openid profile email",
    state: "probe",
  }).toString();
  await page.goto(url.toString());
  await expect(page).toHaveURL(/\/login\/totp/);
});

test("recovery revokes pending OAuth codes and existing access to UserInfo", async ({
  page,
  browser,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    "Revocation probe",
  );
  const grant = await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
  );
  const issuedGrant = await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
  );
  const issued = await exchangeCode(
    page.request,
    gamma.url,
    client,
    issuedGrant,
  );
  expect(issued.status()).toBe(200);
  const tokens = (await issued.json()) as { access_token: string };
  await page.goto(`${gamma.url}/me/security`);
  await page.getByRole("button", { name: "Turn off email recovery" }).click();
  const key = await saveRecoveryKey(page);
  const ctx = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const recovery = await ctx.newPage();
    await recovery.goto(`${gamma.url}/login/recovery`);
    await recovery.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
    await recovery.getByLabel("Recovery key", { exact: true }).fill(key);
    await recovery
      .getByRole("button", { name: "Continue", exact: true })
      .click();
    await expect(recovery).toHaveURL(/\/sign-in-setup/);
    await page.goto(`${gamma.url}/`);
    await expect(page).toHaveURL(/\/login/);
    const result = await exchangeCode(ctx.request, gamma.url, client, grant);
    expect(result.status()).toBe(400);
    expect(((await result.json()) as { error: string }).error).toBe(
      "invalid_grant",
    );
    const userinfo = await ctx.request.get(`${gamma.url}/oauth2/userinfo`, {
      headers: { Authorization: `Bearer ${tokens.access_token}` },
    });
    expect(userinfo.status()).toBe(401);
  } finally {
    await ctx.close();
  }
});

test("concurrent TOTP code redemption succeeds only once with encrypted secrets", async ({
  page,
  browser,
  gamma,
  env,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const { secret, step } = await enableTotp(page, gamma.url);
  const stored = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-At",
    "-c",
    "SELECT secret FROM g_user_totp WHERE user_id=(SELECT user_id FROM g_user WHERE cid='jhalpert')",
  ]);
  expect(stored.exitCode).toBe(0);
  expect(stored.stdout.trim()).toMatch(/^v1:/);
  expect(stored.stdout).not.toContain(secret);
  const contexts = await Promise.all([
    browser.newContext({ ignoreHTTPSErrors: true }),
    browser.newContext({ ignoreHTTPSErrors: true }),
  ]);
  try {
    const pages = await Promise.all(contexts.map((c) => c.newPage()));
    await Promise.all(pages.map((p) => rawPassword(p, gamma.url)));
    for (const p of pages) await expect(p).toHaveURL(/\/login\/totp/);
    const csrf = await Promise.all(
      pages.map((p) =>
        csrfTokenOf(p.getByLabel("Six digit code", { exact: true })),
      ),
    );
    const code = totpCode(secret, Math.max(step, currentTimeStep()) + 1);
    const responses = await Promise.all(
      pages.map((p, i) =>
        p.request.post(`${gamma.url}/login/totp`, {
          form: { code, _csrf: csrf[i]! },
          maxRedirects: 0,
        }),
      ),
    );
    const redirects = responses.map(
      (r) => new URL(r.headers().location!, gamma.url),
    );
    expect(
      redirects.filter(
        (u) => u.pathname === "/login/totp" && u.searchParams.has("error"),
      ),
    ).toHaveLength(1);
    expect(redirects.filter((u) => u.pathname === "/")).toHaveLength(1);
  } finally {
    await Promise.all(contexts.map((c) => c.close()));
  }
});

test("the documented last-admin recovery invalidates old sessions even after admin rights return", async ({
  page,
  gamma,
  env,
}) => {
  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!, "admin");
  const owner = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-At",
    "-c",
    `SELECT user_id FROM g_user WHERE cid='${gamma.adminCid!}'`,
  ]);
  expect(owner.exitCode).toBe(0);
  const userId = owner.stdout.trim();
  const verification = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-c",
    `UPDATE g_user SET email_recovery=FALSE, email_recovery_requires_verification=TRUE WHERE user_id='${userId}'`,
  ]);
  expect(verification.exitCode).toBe(0);

  const readme = await readFile(
    path.resolve(__dirname, "../../../README.md"),
    "utf8",
  );
  const recoverySql = readme
    .match(/```sql\n(BEGIN;[\s\S]*?)```/)![1]!
    .replaceAll("<user id>", userId);
  const [resetSql, restoreSql] = recoverySql.split(
    "-- after they have set up a new passkey:",
  );
  const removed = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-v",
    "ON_ERROR_STOP=1",
    "-c",
    resetSql!,
  ]);
  expect(removed.exitCode).toBe(0);
  const recoveredEmail = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-At",
    "-c",
    `SELECT email_recovery, email_recovery_requires_verification FROM g_user WHERE user_id='${userId}'`,
  ]);
  expect(recoveredEmail.exitCode).toBe(0);
  expect(recoveredEmail.stdout.trim()).toBe("t|f");

  await page.goto(`${gamma.url}/`);
  await expect(page).toHaveURL(/\/login/);
  const restored = await env.postgres.exec([
    "psql",
    "-U",
    "postgres",
    "-d",
    "postgres",
    "-v",
    "ON_ERROR_STOP=1",
    "-c",
    restoreSql!,
  ]);
  expect(restored.exitCode).toBe(0);
  await page.goto(`${gamma.url}/users`);
  await expect(page).toHaveURL(/\/login/);
});

test("partial sign-in and setup sessions cannot change security or authorize clients", async ({
  page,
  browser,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    "Partial auth probe",
  );
  const { key } = await enableTotp(page, gamma.url);
  await logout(page);
  await rawPassword(page, gamma.url);
  await expect(page).toHaveURL(/\/login\/totp/);
  let csrf = await csrfTokenOf(
    page.getByLabel("Six digit code", { exact: true }),
  );
  const blocked = [
    "/me/security/totp/disable",
    "/me/security/email-recovery/enable",
    "/me/security/recovery-key",
    "/webauthn/register",
  ];
  for (const endpoint of blocked) {
    const r = await page.request.post(gamma.url + endpoint, {
      form: { _csrf: csrf },
      maxRedirects: 0,
    });
    expect(r.status()).toBe(302);
    expect(r.headers().location).toContain("/login/totp");
  }
  const noCsrf = await page.request.post(`${gamma.url}/login/totp`, {
    form: { code: "123456" },
    maxRedirects: 0,
  });
  expect(noCsrf.status()).toBe(403);
  const ctx = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const recovery = await ctx.newPage();
    await recovery.goto(`${gamma.url}/login/recovery`);
    await recovery.getByLabel("Cid or email", { exact: true }).fill("jhalpert");
    await recovery.getByLabel("Recovery key", { exact: true }).fill(key);
    await recovery
      .getByRole("button", { name: "Continue", exact: true })
      .click();
    await expect(recovery).toHaveURL(/\/sign-in-setup/);
    csrf = await csrfTokenOf(recovery.getByLabel("Password", { exact: true }));
    for (const endpoint of blocked.slice(0, 3)) {
      const r = await recovery.request.post(gamma.url + endpoint, {
        form: { _csrf: csrf },
        maxRedirects: 0,
      });
      expect(r.status()).toBe(302);
      expect(r.headers().location).toContain("/sign-in-setup");
    }
    const url = new URL("/oauth2/authorize", gamma.url);
    url.search = new URLSearchParams({
      response_type: "code",
      client_id: client.clientId,
      redirect_uri: client.redirectUri,
      scope: "openid profile email",
      state: "setup",
    }).toString();
    await recovery.goto(url.toString());
    await expect(recovery).toHaveURL(/\/sign-in-setup/);
  } finally {
    await ctx.close();
  }
});

for (const wrongOrigin of [false, true])
  test(`the server rejects WebAuthn registration ${wrongOrigin ? "with wrong origin" : "without user verification"}`, async ({
    page,
    gamma,
  }) => {
    await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
    await page.goto(`${gamma.url}/me/security`);
    const verifier = await page.context().newCDPSession(page);
    await verifier.send("WebAuthn.enable");
    await verifier.send("WebAuthn.addVirtualAuthenticator", {
      options: {
        protocol: "ctap2",
        transport: "internal",
        hasResidentKey: true,
        hasUserVerification: wrongOrigin,
        isUserVerified: wrongOrigin,
        automaticPresenceSimulation: true,
      },
    });
    const results = await page
      .getByRole("button", { name: "Add a passkey", exact: true })
      .evaluate(async (button, wrongOrigin) => {
        const headers: Record<string, string> = {
          "Content-Type": "application/json",
          [button.dataset.csrfHeader!]: button.dataset.csrfToken!,
        };
        function decode(value: string) {
          return Uint8Array.from(
            atob(value.replace(/-/g, "+").replace(/_/g, "/")),
            (c) => c.charCodeAt(0),
          );
        }
        function encode(value: ArrayBuffer) {
          return btoa(String.fromCharCode(...new Uint8Array(value)))
            .replace(/=/g, "")
            .replace(/\+/g, "-")
            .replace(/\//g, "_");
        }
        async function attempt(wrongOrigin: boolean) {
          const raw = (await (
            await fetch("/webauthn/register/options", {
              method: "POST",
              headers,
            })
          ).json()) as Omit<
            PublicKeyCredentialCreationOptions,
            "challenge" | "user"
          > & {
            challenge: string;
            user: { id: string; name: string; displayName: string };
            authenticatorSelection: AuthenticatorSelectionCriteria;
          };
          const options = {
            ...raw,
            challenge: decode(raw.challenge),
            user: { ...raw.user, id: decode(raw.user.id) },
            excludeCredentials: [],
            authenticatorSelection: {
              ...raw.authenticatorSelection,
              userVerification: wrongOrigin ? "required" : "discouraged",
            },
          } as PublicKeyCredentialCreationOptions;
          const credential = (await navigator.credentials.create({
            publicKey: options,
          })) as PublicKeyCredential;
          const r = credential.response as AuthenticatorAttestationResponse;
          let clientDataJSON = encode(r.clientDataJSON);
          if (wrongOrigin) {
            const data = JSON.parse(
              new TextDecoder().decode(r.clientDataJSON),
            ) as { origin: string };
            data.origin = "https://evil.example";
            clientDataJSON = encode(
              new TextEncoder().encode(JSON.stringify(data)).buffer,
            );
          }
          const response = await fetch("/webauthn/register", {
            method: "POST",
            headers,
            body: JSON.stringify({
              publicKey: {
                credential: {
                  id: credential.id,
                  rawId: encode(credential.rawId),
                  response: {
                    attestationObject: encode(r.attestationObject),
                    clientDataJSON,
                    transports: r.getTransports(),
                  },
                  type: credential.type,
                  clientExtensionResults:
                    credential.getClientExtensionResults(),
                },
                label: "Must be rejected",
              },
            }),
          });
          return {
            status: response.status,
            text: await response.text(),
            flags: new Uint8Array(r.getAuthenticatorData())[32],
          };
        }
        return [await attempt(wrongOrigin)];
      }, wrongOrigin);
    for (const result of results) {
      if (!wrongOrigin) expect(result.flags! & 4).toBe(0);
      expect(result.text).not.toContain('"success":true');
    }
    await page.reload();
    await expect(
      page.getByRole("row", { name: /Must be rejected/ }),
    ).toHaveCount(0);
  });
