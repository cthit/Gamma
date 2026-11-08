import { expect, type CDPSession, type Page } from "@playwright/test";

const authenticators = new WeakMap<
  Page,
  { cdp: CDPSession; authenticatorId: string }
>();

export async function useVirtualAuthenticator(page: Page): Promise<string> {
  const existing = authenticators.get(page);
  if (existing) {
    return existing.authenticatorId;
  }

  const cdp = await page.context().newCDPSession(page);
  await cdp.send("WebAuthn.enable");
  return addAuthenticator(page, cdp);
}

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

export async function addPasskey(page: Page, name: string): Promise<void> {
  await useVirtualAuthenticator(page);
  await page
    .getByLabel(/Name of the (new )?passkey/, { exact: true })
    .fill(name);
  await page.getByRole("button", { name: "Add a passkey" }).click();
}

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
