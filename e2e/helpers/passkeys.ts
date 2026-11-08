import { expect, type CDPSession, type Page } from "@playwright/test";

const authenticators = new WeakMap<
  Page,
  { cdp: CDPSession; authenticatorId: string }
>();

/**
 * Gives the page a virtual passkey authenticator, like a phone or a security
 * key that always verifies the user. Passkeys added on the page are kept in it,
 * so the same page can sign in with them later.
 */
export async function useVirtualAuthenticator(page: Page): Promise<string> {
  const existing = authenticators.get(page);
  if (existing) {
    return existing.authenticatorId;
  }

  const cdp = await page.context().newCDPSession(page);
  await cdp.send("WebAuthn.enable");
  return addAuthenticator(page, cdp);
}

/**
 * Swaps the page's virtual authenticator for a new one, like picking up
 * another phone. An authenticator holds one passkey per user, so a second
 * passkey for the same user needs another authenticator.
 */
export async function useAnotherVirtualAuthenticator(
  page: Page,
): Promise<void> {
  const existing = authenticators.get(page);
  if (!existing) {
    await useVirtualAuthenticator(page);
    return;
  }
  await existing.cdp.send("WebAuthn.removeVirtualAuthenticator", {
    authenticatorId: existing.authenticatorId,
  });
  await addAuthenticator(page, existing.cdp);
}

async function addAuthenticator(page: Page, cdp: CDPSession): Promise<string> {
  const { authenticatorId } = await cdp.send(
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
  authenticators.set(page, { cdp, authenticatorId });
  return authenticatorId;
}

/** Signs in with a passkey of the page's virtual authenticator. */
export async function signInWithPasskey(page: Page): Promise<void> {
  await useVirtualAuthenticator(page);
  await Promise.all([
    page.waitForURL(
      (url) => url.pathname !== "/login" && url.pathname !== "/login/confirm",
      { timeout: 15000 },
    ),
    page.getByRole("button", { name: "Sign in with a passkey" }).click(),
  ]);
}

/**
 * Adds a passkey to the virtual authenticator from a page with a passkey name
 * field and an "Add a passkey" button.
 */
export async function addPasskey(page: Page, name: string): Promise<void> {
  await useVirtualAuthenticator(page);
  await page
    .getByLabel(/Name of the (new )?passkey/, { exact: true })
    .fill(name);
  await page.getByRole("button", { name: "Add a passkey" }).click();
}

/**
 * Finishes signing in an admin who has signed in with their password: adds a
 * passkey if they have none, and signs in with it.
 */
export async function finishAdminSignIn(page: Page): Promise<void> {
  await expect(page).toHaveURL(/\/login\/confirm/);
  await useVirtualAuthenticator(page);

  const addPasskeyHeading = page.getByRole("heading", {
    name: "Add a passkey to continue",
  });
  if (await addPasskeyHeading.isVisible()) {
    await page.getByLabel("Name of the passkey", { exact: true }).fill("E2E");
    await Promise.all([
      page.waitForURL((url) => url.pathname !== "/login/confirm", {
        timeout: 15000,
      }),
      page.getByRole("button", { name: "Add a passkey" }).click(),
    ]);
  } else {
    await signInWithPasskey(page);
  }
}
