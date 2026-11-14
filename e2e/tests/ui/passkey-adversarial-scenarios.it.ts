/// <reference lib="dom" />
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import { addPasskey, useVirtualAuthenticator } from "../../helpers/passkeys";
import { csrfTokenOf } from "../../helpers/a11y";
import type { Page } from "@playwright/test";
import {
  createHash,
  createPrivateKey,
  createPublicKey,
  sign,
  verify,
} from "node:crypto";

async function createAssertion(page: Page, withoutVerification = false) {
  return page
    .getByRole("button", { name: "Sign in with a passkey" })
    .evaluate(async (button, withoutVerification) => {
      const headers: Record<string, string> = {
        "Content-Type": "application/json",
        [button.dataset.csrfHeader!]: button.dataset.csrfToken!,
      };
      const decode = (value: string) =>
        Uint8Array.from(
          atob(value.replace(/-/g, "+").replace(/_/g, "/")),
          (c) => c.charCodeAt(0),
        );
      const encode = (value: ArrayBuffer) =>
        btoa(String.fromCharCode(...new Uint8Array(value)))
          .replace(/=/g, "")
          .replace(/\+/g, "-")
          .replace(/\//g, "_");
      const raw = (await (
        await fetch("/webauthn/authenticate/options", {
          method: "POST",
          headers,
        })
      ).json()) as Omit<
        PublicKeyCredentialRequestOptions,
        "challenge" | "allowCredentials"
      > & {
        challenge: string;
        allowCredentials?: { id: string; type: PublicKeyCredentialType }[];
      };
      const credential = (await navigator.credentials.get({
        publicKey: {
          ...raw,
          challenge: decode(raw.challenge),
          allowCredentials: (raw.allowCredentials ?? []).map((allowed) => ({
            ...allowed,
            id: decode(allowed.id),
          })),
          userVerification: withoutVerification ? "discouraged" : "required",
        },
      })) as PublicKeyCredential;
      const response = credential.response as AuthenticatorAssertionResponse;
      return {
        headers,
        flags: new Uint8Array(response.authenticatorData)[32]!,
        body: {
          id: credential.id,
          rawId: encode(credential.rawId),
          response: {
            authenticatorData: encode(response.authenticatorData),
            clientDataJSON: encode(response.clientDataJSON),
            signature: encode(response.signature),
            userHandle: response.userHandle
              ? encode(response.userHandle)
              : undefined,
          },
          credType: credential.type,
          clientExtensionResults: credential.getClientExtensionResults(),
        },
      };
    }, withoutVerification);
}

const invalidAssertions = [
  "wrong origin",
  "wrong challenge",
  "wrong ceremony type",
  "wrong user handle",
  "missing user handle",
  "empty user handle",
  "invalid signature",
  "wrong relying party hash",
  "without user verification",
  "without CSRF",
] as const;

for (const scenario of invalidAssertions) {
  test(`passkey sign-in rejects an assertion ${scenario}`, async ({
    page,
    gamma,
  }) => {
    await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
    await page.goto(`${gamma.url}/me/security`);
    page.once("dialog", (d) => d.accept());
    await addPasskey(page, "Adversarial device");
    await expect(
      page.getByRole("row", { name: /Adversarial device/ }),
    ).toBeVisible();
    await logout(page);

    const withoutVerification = scenario === "without user verification";
    if (withoutVerification) {
      const authenticatorId = await useVirtualAuthenticator(page);
      const cdp = await page.context().newCDPSession(page);
      await cdp.send("WebAuthn.setUserVerified", {
        authenticatorId,
        isUserVerified: false,
      });
    }
    const assertion = await createAssertion(page, withoutVerification);
    if (withoutVerification) expect(assertion.flags & 4).toBe(0);

    const response = assertion.body.response;
    if (
      scenario === "wrong origin" ||
      scenario === "wrong challenge" ||
      scenario === "wrong ceremony type"
    ) {
      const data = JSON.parse(
        Buffer.from(response.clientDataJSON, "base64url").toString(),
      ) as { origin: string; challenge: string; type: string };
      if (scenario === "wrong origin") data.origin = "https://evil.example";
      if (scenario === "wrong challenge") data.challenge = "wrong-challenge";
      if (scenario === "wrong ceremony type") data.type = "webauthn.create";
      response.clientDataJSON = Buffer.from(JSON.stringify(data)).toString(
        "base64url",
      );
    }
    if (scenario === "wrong user handle") {
      response.userHandle = Buffer.alloc(32, 42).toString("base64url");
    }
    if (scenario === "missing user handle") {
      response.userHandle = undefined;
    }
    if (scenario === "empty user handle") {
      response.userHandle = "";
    }
    if (scenario === "invalid signature") {
      const signature = Buffer.from(response.signature, "base64url");
      signature[signature.length - 1]! ^= 1;
      response.signature = signature.toString("base64url");
    }
    if (scenario === "wrong relying party hash") {
      const data = Buffer.from(response.authenticatorData, "base64url");
      data[0]! ^= 1;
      response.authenticatorData = data.toString("base64url");
    }
    if (
      scenario === "wrong origin" ||
      scenario === "wrong challenge" ||
      scenario === "wrong ceremony type" ||
      scenario === "wrong relying party hash"
    ) {
      // Re-sign the altered data with the virtual authenticator's actual key.
      // Otherwise a broken signature could mask missing protocol validation.
      const authenticatorId = await useVirtualAuthenticator(page);
      const cdp = await page.context().newCDPSession(page);
      const { credentials } = await cdp.send("WebAuthn.getCredentials", {
        authenticatorId,
      });
      const credential = credentials.find((candidate) =>
        Buffer.from(candidate.credentialId, "base64").equals(
          Buffer.from(assertion.body.rawId, "base64url"),
        ),
      )!;
      const privateKey = createPrivateKey({
        key: Buffer.from(credential.privateKey, "base64"),
        format: "der",
        type: "pkcs8",
      });
      const signedData = Buffer.concat([
        Buffer.from(response.authenticatorData, "base64url"),
        createHash("sha256")
          .update(Buffer.from(response.clientDataJSON, "base64url"))
          .digest(),
      ]);
      const algorithm =
        privateKey.asymmetricKeyType === "ed25519" ||
        privateKey.asymmetricKeyType === "ed448"
          ? null
          : "sha256";
      const signature = sign(algorithm, signedData, privateKey);
      expect(
        verify(algorithm, signedData, createPublicKey(privateKey), signature),
      ).toBe(true);
      response.signature = signature.toString("base64url");
    }
    const result = await page.request.post(`${gamma.url}/login/webauthn`, {
      headers:
        scenario === "without CSRF"
          ? { "Content-Type": "application/json" }
          : assertion.headers,
      data: assertion.body,
      maxRedirects: 0,
    });
    if (scenario === "without CSRF") expect(result.status()).toBe(403);
    expect(result.status()).toBeLessThan(500);
    expect(await result.text()).not.toContain('"authenticated":true');
    await page.goto(gamma.url);
    await expect(page).toHaveURL(/\/login/);
    await expect(page.getByText("Hey, Big Tuna!")).toHaveCount(0);
  });
}

test("a passkey assertion is bound to its browser session and cannot be replayed after successful sign-in", async ({
  page,
  browser,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  await page.goto(`${gamma.url}/me/security`);
  page.once("dialog", (d) => d.accept());
  await addPasskey(page, "Replay device");
  await expect(page.getByRole("row", { name: /Replay device/ })).toBeVisible();
  await logout(page);
  const assertion = await createAssertion(page);
  const context = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const other = await context.newPage();
    await other.goto(`${gamma.url}/login`);
    const headers = {
      "Content-Type": "application/json",
      "X-CSRF-TOKEN": await csrfTokenOf(
        other.getByRole("button", { name: "Login", exact: true }),
      ),
    };
    const crossSession = await other.request.post(
      `${gamma.url}/login/webauthn`,
      { headers, data: assertion.body, maxRedirects: 0 },
    );
    expect(crossSession.status()).toBeLessThan(500);
    expect(await crossSession.text()).not.toContain('"authenticated":true');
    await other.goto(gamma.url);
    await expect(other).toHaveURL(/\/login/);

    const accepted = await page.request.post(`${gamma.url}/login/webauthn`, {
      headers: assertion.headers,
      data: assertion.body,
      maxRedirects: 0,
    });
    expect(accepted.status()).toBe(200);
    expect((await accepted.json()) as { authenticated: boolean }).toMatchObject(
      {
        authenticated: true,
      },
    );
    await page.goto(gamma.url);
    await expect(page.getByText("Hey, Big Tuna!")).toBeVisible();
    const replay = await page.request.post(`${gamma.url}/login/webauthn`, {
      headers: {
        "Content-Type": "application/json",
        "X-CSRF-TOKEN": await csrfTokenOf(
          page.getByRole("button", { name: "Logout", exact: true }),
        ),
      },
      data: assertion.body,
      maxRedirects: 0,
    });
    expect(replay.status()).toBeLessThan(500);
    expect(await replay.text()).not.toContain('"authenticated":true');
  } finally {
    await context.close();
  }
});
