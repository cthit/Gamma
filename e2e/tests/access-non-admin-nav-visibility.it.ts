import { expect, test } from "@playwright/test";
import { login } from "../helpers/auth";
import { startMockGamma } from "../helpers/gamma";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../gamma-setup";

test("given a non admin user when viewing navigation then admin links are hidden", async ({
  page,
}) => {
  const env = await startDependencies();
  const gamma = await startMockGamma(env);

  try {
    await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

    await expect(page.getByRole("link", { name: "Users" })).toBeVisible({
      timeout: 10000,
    });
    await expect(page.getByRole("link", { name: "Api keys" })).toHaveCount(0);
    await expect(page.getByRole("link", { name: "Admins" })).toHaveCount(0);
    await expect(page.getByRole("link", { name: "Allow lists" })).toHaveCount(
      0,
    );
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
