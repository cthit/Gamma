import { expect, test } from "@playwright/test";
import { startDefaultGamma } from "../helpers/gamma";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../gamma-setup";

test("given missing or invalid api key when calling info blob then request is unauthorized", async ({
  request,
}) => {
  const env = await startDependencies();
  const gamma = await startDefaultGamma(env);

  try {
    const missingAuthResponse = await request.get(
      `${gamma.url}/api/info/v1/blob`,
    );
    expect(missingAuthResponse.status()).toBe(401);

    const invalidAuthResponse = await request.get(
      `${gamma.url}/api/info/v1/blob`,
      {
        headers: {
          Authorization: "pre-shared invalid-id:invalid-token",
        },
      },
    );
    expect(invalidAuthResponse.status()).toBe(401);
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
