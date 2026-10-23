import { defineConfig, devices } from "@playwright/test";

export default defineConfig({
  testDir: "./tests",
  testMatch: "**/*.it.ts",
  timeout: 120000,
  fullyParallel: true,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 2 : 0,
  // Each test starts its own Docker network and containers on random host
  // ports, so workers never share state. Two workers keeps the CI suite well
  // inside its step timeout; the runner has 4 vCPUs and 16 GB of memory.
  workers: process.env.CI ? 2 : 4,
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
