import { expect, test as base } from "@playwright/test";
import { startDefaultGamma, startMockGamma } from "./gamma";
import {
  createIsolatedEnvironment,
  removeIsolatedEnvironment,
  startDependencies,
  stopDependencies,
  stopGammaInstance,
  type GammaEnvironment,
  type GammaInstance,
} from "../gamma-setup";

type EnvironmentFixture = {
  env: GammaEnvironment;
};

type GammaFixture = {
  gamma: GammaInstance;
};

type WorkerFixture = {
  sharedEnv: GammaEnvironment;
};

async function withIsolatedEnvironment(
  sharedEnv: GammaEnvironment,
  use: (env: GammaEnvironment) => Promise<void>,
): Promise<void> {
  const env = await createIsolatedEnvironment(sharedEnv);
  try {
    await use(env);
  } finally {
    await removeIsolatedEnvironment(env);
  }
}

const testWithEnvironment = base.extend<EnvironmentFixture, WorkerFixture>({
  sharedEnv: [
    async ({ browserName }, use) => {
      void browserName;
      const env = await startDependencies();
      try {
        await use(env);
      } finally {
        await stopDependencies(env);
      }
    },
    { scope: "worker", timeout: 600_000 },
  ],
  env: async ({ sharedEnv }, use) => {
    await withIsolatedEnvironment(sharedEnv, use);
  },
});

export const testWithDefaultGamma = testWithEnvironment.extend<GammaFixture>({
  gamma: async ({ env }, use) => {
    const gamma = await startDefaultGamma(env);
    try {
      await use(gamma);
    } finally {
      await stopGammaInstance(gamma);
    }
  },
});

export const testWithMockGamma = testWithEnvironment.extend<GammaFixture>({
  gamma: async ({ env }, use) => {
    const gamma = await startMockGamma(env);
    try {
      await use(gamma);
    } finally {
      await stopGammaInstance(gamma);
    }
  },
});

export { expect };
