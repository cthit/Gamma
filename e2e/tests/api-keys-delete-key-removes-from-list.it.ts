import { expect, test } from "@playwright/test";
import { createApiKeyViaUi } from "../helpers/api-keys";
import { login } from "../helpers/auth";
import { startDefaultGamma } from "../helpers/gamma";
import { uniqueLabel } from "../helpers/strings";
import {
  startDependencies,
  stopDependencies,
  stopGammaInstance,
} from "../gamma-setup";

test("given an existing api key when deleting it then it is removed from the api keys list", async ({
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

    const prettyName = uniqueLabel("E2E Delete Key");
    await createApiKeyViaUi(page, gamma.url, {
      prettyName,
      svDescription: "E2E svensk beskrivning",
      enDescription: "E2E english description",
      keyType: "INFO",
    });

    page.once("dialog", async (dialog) => dialog.accept());

    await Promise.all([
      page.waitForURL("**/api-keys", { timeout: 15000 }),
      page.getByRole("button", { name: "Delete" }).click(),
    ]);

    await expect(page.getByText(prettyName)).toHaveCount(0);
  } finally {
    await stopGammaInstance(gamma);
    await stopDependencies(env);
  }
});
