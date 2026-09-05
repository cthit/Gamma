import { chromium } from "@playwright/test";
import { login } from "./auth";
import { createApiKeyViaUi } from "./api-keys";
import {
  startGammaInstance,
  stopGammaInstance,
  type GammaEnvironment,
  type GammaInstance,
} from "../gamma-setup";

export async function startDefaultGamma(
  env: GammaEnvironment,
): Promise<GammaInstance> {
  return startGammaInstance(env);
}

export async function startMockGamma(
  env: GammaEnvironment,
  environmentOverrides: Record<string, string> = {},
  publicProtocol: "http" | "https" = "https",
  publicHostname?: string,
  image?: string,
  logLabel?: string,
): Promise<GammaInstance> {
  const gamma = await startGammaInstance(env, {
    ...(image === undefined ? {} : { image }),
    ...(logLabel === undefined ? {} : { logLabel }),
    publicProtocol,
    ...(publicHostname === undefined ? {} : { publicHostname }),
    env: {
      IS_MOCKING: "true",
      MOCK_DATA_RESOURCE: "classpath:/mock/mock.json",
      ...environmentOverrides,
    },
  });
  try {
    // Fixture credentials come from the real administrative operation, never startup logs.
    const browser = await chromium.launch();
    try {
      const context = await browser.newContext({ ignoreHTTPSErrors: true });
      const page = await context.newPage();
      if (!gamma.adminCid || !gamma.adminPassword) {
        throw new Error("Gamma fixture administrator credentials are missing");
      }
      await login(
        page,
        gamma.url,
        gamma.adminCid,
        gamma.adminPassword,
        "admin",
      );
      gamma.apiKeys = {};
      for (const keyType of [
        "INFO",
        "ACCOUNT_SCAFFOLD",
        "ALLOW_LIST",
      ] as const) {
        const credentials = await createApiKeyViaUi(page, gamma.url, {
          prettyName: `E2E ${keyType}`,
          svDescription: "",
          enDescription: "",
          keyType,
        });
        gamma.apiKeys[keyType] = {
          id: credentials.apiKeyId,
          token: credentials.apiKeyToken,
        };
      }
    } finally {
      await browser.close();
    }
    return gamma;
  } catch (error) {
    await stopGammaInstance(gamma);
    throw error;
  }
}
