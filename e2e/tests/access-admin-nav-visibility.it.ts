import { expect, test } from "@playwright/test";
import { login } from "../helpers/auth";
import { startDefaultGamma } from "../helpers/gamma";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../gamma-setup";

test("given an admin user when viewing navigation then admin links are visible", async ({
  page,
}) => {
  const env = await startDependencies();
  const gamma = await startDefaultGamma(env);

  try {
    await login(
      page,
      gamma.url,
      gamma.adminCid ?? "",
      gamma.adminPassword ?? "",
      "admin",
    );

    await expect(page.getByRole("link", { name: "Api keys" })).toBeVisible({
      timeout: 10000,
    });
    await expect(page.getByRole("link", { name: "Admins" })).toBeVisible({
      timeout: 10000,
    });
    await expect(page.getByRole("link", { name: "Allow lists" })).toBeVisible({
      timeout: 10000,
    });
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
