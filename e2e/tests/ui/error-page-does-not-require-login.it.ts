import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";

const bogusAuthorizePath =
  "/oauth2/authorize?client_id=bogus&response_type=code&redirect_uri=http%3A%2F%2Flocalhost%2Fcb&scope=openid";

test("given an anonymous user when an error occurs then the error is returned instead of a login redirect", async ({
  page,
  request,
  gamma,
}) => {
  const htmlErrorResponse = await request.get(
    `${gamma.url}${bogusAuthorizePath}`,
    { maxRedirects: 0, headers: { Accept: "text/html" } },
  );
  expect(htmlErrorResponse.status()).toBe(400);
  expect(htmlErrorResponse.headers()["location"]).toBeUndefined();
  const htmlErrorBody = await htmlErrorResponse.text();
  expect(htmlErrorBody).toContain("400 - Bad Request");
  expect(htmlErrorBody).not.toContain('name="username"');

  const jsonErrorResponse = await request.get(
    `${gamma.url}${bogusAuthorizePath}`,
    { maxRedirects: 0, headers: { Accept: "application/json" } },
  );
  expect(jsonErrorResponse.status()).toBe(400);
  expect(jsonErrorResponse.headers()["content-type"]).toContain(
    "application/json",
  );
  expect(await jsonErrorResponse.json()).toMatchObject({
    status: 400,
    error: "Bad Request",
  });

  const errorPageResponse = await request.get(`${gamma.url}/error`, {
    maxRedirects: 0,
    headers: { Accept: "text/html" },
  });
  expect(errorPageResponse.status()).toBe(500);
  const errorPageBody = await errorPageResponse.text();
  expect(errorPageBody).toContain("500 - Internal Server Error");
  expect(errorPageBody).not.toContain('name="username"');

  const protectedPageResponse = await request.get(`${gamma.url}/users`, {
    maxRedirects: 0,
    headers: { Accept: "text/html" },
  });
  expect(protectedPageResponse.status()).toBe(302);
  expect(protectedPageResponse.headers()["location"]).toContain("/login");

  const browserResponse = await page.goto(`${gamma.url}${bogusAuthorizePath}`, {
    timeout: 30000,
  });
  expect(browserResponse?.status()).toBe(400);
  expect(page.url()).not.toContain("/login");
  await expect(page.locator("main > article > header")).toHaveText(
    "400 - Bad Request",
  );
  await expect(page.locator('input[name="username"]')).toHaveCount(0);
});
