import path from "node:path";
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
): Promise<GammaInstance> {
  return startGammaInstance(env, {
    env: {
      PRODUCTION: "true",
      IS_MOCKING: "true",
      MOCK_DATA_RESOURCE: "file:/tmp/e2e-mock.json",
    },
    waitForBootstrapApiKeys: true,
    filesToCopy: [
      {
        source: path.resolve(
          __dirname,
          "..",
          "fixtures",
          "mock",
          "e2e-mock.json",
        ),
        target: "/tmp/e2e-mock.json",
      },
    ],
  });
}
