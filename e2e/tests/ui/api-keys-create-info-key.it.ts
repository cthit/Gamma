import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { createApiKeyViaUi } from "../../helpers/api-keys";
import { login } from "../../helpers/auth";
import { uniqueLabel } from "../../helpers/strings";

test("given an admin user when creating an info api key then credentials are shown", async ({
  page,
  gamma,
}) => {
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const prettyName = uniqueLabel("E2E Info Key");
  const credentials = await createApiKeyViaUi(page, gamma.url, {
    prettyName,
    svDescription: "E2E svensk beskrivning",
    enDescription: "E2E english description",
    keyType: "INFO",
  });

  expect(credentials.apiKeyId).toBeTruthy();
  expect(credentials.apiKeyToken).toBeTruthy();
  await expect(page.getByText("Authorization: pre-shared")).toBeVisible({
    timeout: 10000,
  });
});
