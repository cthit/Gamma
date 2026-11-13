import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";
import { rowWithText } from "../../helpers/a11y";

test("given a cleared data deletion request when visiting the delete account page then the user is told that the record is kept", async ({
  page,
  gamma,
}) => {
  const officialName = uniqueLabel("Official");

  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!);
  const official = await createUserClientViaUi(page, gamma.url, officialName, {
    official: true,
  });
  await logout(page);

  await login(page, gamma.url, "pbeesly", "password1337", "Pam-Pam");
  await authorizeClientWithPkce(
    page,
    gamma.url,
    official.clientId,
    official.redirectUri,
    { consent: "required" },
  );
  await page.goto(`${gamma.url}/me/accepted-clients`, { timeout: 30000 });
  await rowWithText(page, officialName)
    .getByRole("button", { name: "Retract approval" })
    .click();
  await expect(rowWithText(page, officialName)).toHaveCount(0, {
    timeout: 10000,
  });

  await page.goto(`${gamma.url}/delete-your-account`, { timeout: 30000 });
  await expect(
    page.getByText("You have retracted your approval of official clients"),
  ).toHaveCount(0);
  await logout(page);

  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!);
  await page.goto(`${gamma.url}/data-deletion`, { timeout: 30000 });
  const openRequests = page.getByRole("table", {
    name: "Data deletion requests",
  });
  page.once("dialog", async (dialog) => dialog.accept());
  await rowWithText(openRequests, officialName)
    .getByRole("button", {
      name: `Clear the request for pbeesly in ${officialName}`,
    })
    .click();
  await expect(rowWithText(openRequests, officialName)).toHaveCount(0, {
    timeout: 10000,
  });
  await logout(page);

  await login(page, gamma.url, "pbeesly", "password1337", "Pam-Pam");
  await page.goto(`${gamma.url}/delete-your-account`, { timeout: 30000 });
  await expect(
    page.getByRole("heading", { name: "Official clients you have approved" }),
  ).toHaveCount(0);
  await expect(
    page.getByRole("heading", {
      name: "What Gamma keeps after you delete your account",
    }),
  ).toBeVisible({ timeout: 10000 });
  await expect(
    page.getByText("You have retracted your approval of official clients"),
  ).toBeVisible();
});
