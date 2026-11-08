import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";
import { articleByHeading, rowWithText } from "../../helpers/a11y";

test("given a user with an approved client when an admin locks the user then the session can no longer authorize the client", async ({
  page,
  browser,
  gamma,
}) => {
  const userContext = await browser.newContext({ ignoreHTTPSErrors: true });
  try {
    const user = await userContext.newPage();
    await login(user, gamma.url, "jhalpert", "password1337", "Big Tuna");

    const client = await createUserClientViaUi(
      user,
      gamma.url,
      uniqueLabel("Locked user"),
    );
    await authorizeClientWithPkce(
      user,
      gamma.url,
      client.clientId,
      client.redirectUri,
      { consent: "required" },
    );

    await login(
      page,
      gamma.url,
      gamma.adminCid ?? "",
      gamma.adminPassword ?? "",
      "admin",
    );
    await page.goto(`${gamma.url}/users`, { timeout: 30000 });
    await rowWithText(page, "jhalpert")
      .getByRole("link", { name: "Details" })
      .click();
    page.once("dialog", (dialog) => dialog.accept());
    await articleByHeading(page, "User details")
      .getByRole("button", { name: "Lock user", exact: true })
      .click();
    await expect(page.getByText("User locked")).toBeVisible({
      timeout: 10000,
    });

    // The client is already approved, so without the lock this would redirect
    // straight back to the client with a code.
    const authorizeUrl = new URL("/oauth2/authorize", gamma.url);
    authorizeUrl.search = new URLSearchParams({
      response_type: "code",
      client_id: client.clientId,
      redirect_uri: client.redirectUri,
      scope: "openid profile email",
      state: "locked",
    }).toString();
    await user.goto(authorizeUrl.toString(), { timeout: 30000 });

    await expect(user).toHaveURL(/\/login/);
    await expect(user.getByLabel("Cid or email", { exact: true })).toBeVisible({
      timeout: 10000,
    });
  } finally {
    await userContext.close();
  }
});
