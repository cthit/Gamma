import type { Page } from "@playwright/test";
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { waitForMailLink } from "../../helpers/mail";

async function requestPasswordReset(page: Page, baseUrl: string) {
  await page.goto(`${baseUrl}/forgot-password`);
  await page.getByLabel("Cid or Email", { exact: true }).fill("jhalpert");
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        new URL(response.url()).pathname === "/forgot-password",
      { timeout: 15000 },
    ),
    page.getByRole("button", { name: /reset|send/i }).click(),
  ]);
}

test("a throttled password reset request leaves the last mailed link working", async ({
  page,
  env,
  gamma,
}) => {
  const latestLink = () =>
    waitForMailLink(
      env.gotify,
      gamma.url,
      "jhalpert@example.org",
      "/forgot-password/finalize",
    );
  let previous: string | undefined;
  for (let i = 0; i < 3; i++) {
    await requestPasswordReset(page, gamma.url);
    await expect.poll(latestLink, { timeout: 15000 }).not.toBe(previous);
    previous = await latestLink();
  }

  await requestPasswordReset(page, gamma.url);
  expect(await latestLink()).toBe(previous);

  await page.goto(previous!);
  await expect(page.getByLabel("Password", { exact: true })).toBeVisible();
});
