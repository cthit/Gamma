import {
  startGammaInstance,
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
  return startGammaInstance(env, {
    ...(image === undefined ? {} : { image }),
    ...(logLabel === undefined ? {} : { logLabel }),
    publicProtocol,
    ...(publicHostname === undefined ? {} : { publicHostname }),
    env: {
      IS_MOCKING: "true",
      MOCK_DATA_RESOURCE: "classpath:/mock/mock.json",
      ...environmentOverrides,
    },
    waitForBootstrapApiKeys: true,
  });
}
