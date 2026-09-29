import { defineConfig } from "@playwright/test";
import config from "./playwright.config";

export default defineConfig({
  ...config,
  testDir: "./helpers",
  testMatch: "startup.probe.ts",
  fullyParallel: false,
  workers: 1,
  retries: 0,
  repeatEach: 20,
  projects: [{ name: "http2" }, { name: "http1" }],
});
