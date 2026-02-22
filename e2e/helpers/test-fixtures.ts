import { expect, test as base } from "@playwright/test";
import { startDefaultGamma, startMockGamma } from "./gamma";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
  type GammaEnvironment,
  type GammaInstance,
} from "../gamma-setup";

type GammaFixture = {
  env: GammaEnvironment;
  gamma: GammaInstance;
};

async function withEnvironment(
  use: (env: GammaEnvironment) => Promise<void>,
): Promise<void> {
  const env = await startDependencies();
  try {
    await use(env);
  } finally {
    await stopDependencies(env);
  }
}

export const testWithDefaultGamma = base.extend<GammaFixture>({
  env: async ({ browserName }, use) => {
    void browserName;
    await withEnvironment(use);
  },
  gamma: async ({ env }, use) => {
    const gamma = await startDefaultGamma(env);
    try {
      await use(gamma);
    } finally {
      await stopGammaInstance(gamma);
    }
  },
});

export const testWithMockGamma = base.extend<GammaFixture>({
  env: async ({ browserName }, use) => {
    void browserName;
    await withEnvironment(use);
  },
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
