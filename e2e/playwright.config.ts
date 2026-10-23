import { defineConfig, devices } from "@playwright/test";

export default defineConfig({
  testDir: "./tests",
  testMatch: "**/*.it.ts",
  timeout: 120000,
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  // Keep a single worker on CI. Every test creates and removes its own Docker
  // network, and Chromium aborts in-flight navigations with
  // net::ERR_NETWORK_CHANGED whenever another worker adds or removes a host
  // network interface (see PR #989 for a run where 2 workers failed 8 tests).
  workers: process.env.CI ? 1 : 4,
  // The list reporter prints every result as it completes, so the CI log still
  // shows which tests ran if the step is killed before the HTML report exists.
  reporter: [["list"], ["html", { open: "never" }]],
  use: {
    ignoreHTTPSErrors: true,
    screenshot: "on",
    video: "on",
    trace: "on",
  },
  projects: [
    {
      name: "chromium",
      use: { ...devices["Desktop Chrome"] },
    },
  ],
});
