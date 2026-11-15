import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import {
  addPasskey,
  signInWithPasskey,
  useVirtualAuthenticator,
} from "../../helpers/passkeys";
import type { GammaEnvironment } from "../../gamma-setup";

async function sql(env: GammaEnvironment, statement: string): Promise<string> {
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
    statement,
  ]);
  expect(result.exitCode, result.stderr).toBe(0);
  return result.stdout.trim();
}

test("a cloned passkey whose signature counter went back can't sign in", async ({
  page,
  browser,
  env,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(`${gamma.url}/me/security`);
  page.once("dialog", (d) => d.accept());
  await addPasskey(page, "Original device");
  await expect(
    page.getByRole("row", { name: /Original device/ }),
  ).toBeVisible();

  const authenticatorId = await useVirtualAuthenticator(page);
  const cdp = await page.context().newCDPSession(page);
  await cdp.send("WebAuthn.enable");
  const snapshot = (
    await cdp.send("WebAuthn.getCredentials", { authenticatorId })
  ).credentials;
  expect(snapshot).toHaveLength(1);

  for (let i = 0; i < 3; i++) {
    await page.context().clearCookies();
    await page.goto(`${gamma.url}/login`);
    await signInWithPasskey(page);
    await expect(page.getByText("Hey, Big Tuna!")).toBeVisible();
  }
  const stored = await sql(env, "SELECT signature_count FROM user_credentials");
  expect(Number(stored)).toBeGreaterThan(snapshot[0]!.signCount);

  const ctx = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const clone = await ctx.newPage();
    const cloneCdp = await ctx.newCDPSession(clone);
    await cloneCdp.send("WebAuthn.enable");
    const { authenticatorId: cloneId } = await cloneCdp.send(
      "WebAuthn.addVirtualAuthenticator",
      {
        options: {
          protocol: "ctap2",
          transport: "internal",
          hasResidentKey: true,
          hasUserVerification: true,
          isUserVerified: true,
          automaticPresenceSimulation: true,
        },
      },
    );
    await cloneCdp.send("WebAuthn.addCredential", {
      authenticatorId: cloneId,
      credential: snapshot[0]!,
    });
    await clone.goto(`${gamma.url}/login`);
    await clone.getByRole("button", { name: "Sign in with a passkey" }).click();
    await expect(
      clone.getByText("That didn't work. Try again.", { exact: true }),
    ).toBeVisible();
    await clone.goto(gamma.url);
    await expect(clone).toHaveURL(/\/login/);
    expect(await sql(env, "SELECT signature_count FROM user_credentials")).toBe(
      stored,
    );
  } finally {
    await ctx.close();
  }

  await page.context().clearCookies();
  await page.goto(`${gamma.url}/login`);
  await signInWithPasskey(page);
  await expect(page.getByText("Hey, Big Tuna!")).toBeVisible();
});

test("the server rejects a passkey label that is missing, blank or too long", async ({
  page,
  env,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(`${gamma.url}/me/security`);
  await useVirtualAuthenticator(page);

  const labels: (string | null)[] = [
    null,
    "   ",
    "x".repeat(61),
    "x".repeat(60),
  ];
  const statuses = await page
    .getByRole("button", { name: "Add a passkey", exact: true })
    .evaluate(async (button, labels) => {
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
      const statuses: number[] = [];
      for (const label of labels) {
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
        };
        const credential = (await navigator.credentials.create({
          publicKey: {
            ...raw,
            challenge: decode(raw.challenge),
            user: { ...raw.user, id: decode(raw.user.id) },
            excludeCredentials: [],
          },
        })) as PublicKeyCredential;
        const r = credential.response as AuthenticatorAttestationResponse;
        const publicKey: Record<string, unknown> = {
          credential: {
            id: credential.id,
            rawId: encode(credential.rawId),
            response: {
              attestationObject: encode(r.attestationObject),
              clientDataJSON: encode(r.clientDataJSON),
              transports: r.getTransports(),
            },
            type: credential.type,
            clientExtensionResults: credential.getClientExtensionResults(),
          },
        };
        if (label !== null) publicKey.label = label;
        const response = await fetch("/webauthn/register", {
          method: "POST",
          headers,
          body: JSON.stringify({ publicKey }),
        });
        statuses.push(response.status);
      }
      return statuses;
    }, labels);

  expect(statuses).toEqual([400, 400, 400, 200]);
  expect(await sql(env, "SELECT label FROM user_credentials")).toBe(
    "x".repeat(60),
  );
});
