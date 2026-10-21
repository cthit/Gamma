import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login, logout } from "../../helpers/auth";
import {
  authorizeClientWithPkce,
  createUserClientViaUi,
  exchangeCode,
} from "../../helpers/oauth";
import { uniqueLabel } from "../../helpers/strings";

test("a restricted client admits a member of its super group and refuses a nonmember", async ({
  page,
  request,
  gamma,
}) => {
  await login(page, gamma.url, gamma.adminCid!, gamma.adminPassword!);
  const client = await createUserClientViaUi(
    page,
    gamma.url,
    uniqueLabel("Restricted client"),
    {
      official: true,
      superGroupId: "364a359a-f9eb-4d81-bb99-25cc5adf176d",
    },
  );
  await logout(page);
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");
  const grant = await authorizeClientWithPkce(
    page,
    gamma.url,
    client.clientId,
    client.redirectUri,
    { consent: "required" },
  );
  const accepted = await exchangeCode(request, gamma.url, client, grant);
  expect(accepted.status()).toBe(200);
  expect(
    ((await accepted.json()) as { access_token: string }).access_token,
  ).toBeTruthy();

  await page.goto(gamma.url);
  await logout(page);
  await login(page, gamma.url, "amartin", "password1337", "Pumpkin");
  const url = new URL("/oauth2/authorize", gamma.url);
  url.search = new URLSearchParams({
    response_type: "code",
    client_id: client.clientId,
    redirect_uri: client.redirectUri,
    scope: "openid profile email",
    state: "restricted-nonmember",
  }).toString();
  const denied = await page.goto(url.toString());
  // Existing Gamma surfaces the restriction exception as a server error, not an OAuth error.
  // This characterizes its current denial; it does not endorse that error response.
  expect(denied?.status()).toBe(500);
  expect(new URL(page.url()).searchParams.has("code")).toBe(false);
  await expect(
    page.getByRole("button", { name: "Authorize", exact: true }),
  ).toHaveCount(0);
  await page.goto(`${gamma.url}/me/accepted-clients`);
  await expect(
    page.locator("tr", { hasText: "Restricted client" }),
  ).toHaveCount(0);
});
