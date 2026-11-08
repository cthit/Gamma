import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { waitForMailLink } from "../../helpers/mail";
import { login, logout } from "../../helpers/auth";
import { uniqueCid } from "../../helpers/strings";
import { rowWithText } from "../../helpers/a11y";
import type { Page } from "@playwright/test";

async function requestActivation(page: Page, baseUrl: string, cid: string) {
  await page.goto(`${baseUrl}/activate-cid`, { timeout: 30000 });
  await page.getByLabel("Cid", { exact: true }).fill(cid);

  await Promise.all([
    page.waitForURL("**/email-sent", { timeout: 20000 }),
    page.getByRole("button", { name: "Activate cid" }).click(),
  ]);
}

test("given a pending activation when requesting activation again then only the newest link works", async ({
  page,
  gamma,
  env,
}) => {
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  const cid = uniqueCid("resend").slice(0, 10);

  await page.goto(`${gamma.url}/allow-list`, { timeout: 30000 });
  await page.getByLabel("Cid", { exact: true }).fill(cid);

  await Promise.all([
    expect(rowWithText(page, cid)).toBeVisible({
      timeout: 15000,
    }),
    page.getByRole("button", { name: "Active", exact: true }).click(),
  ]);

  await logout(page);

  const recipient = `${cid}@chalmers.se`;

  await requestActivation(page, gamma.url, cid);
  const firstLink = await waitForMailLink(
    env.gotify,
    gamma.url,
    recipient,
    "/register",
  );

  await requestActivation(page, gamma.url, cid);
  await expect
    .poll(
      () => waitForMailLink(env.gotify, gamma.url, recipient, "/register"),
      { timeout: 15000 },
    )
    .not.toBe(firstLink);
  const secondLink = await waitForMailLink(
    env.gotify,
    gamma.url,
    recipient,
    "/register",
  );

  await page.goto(firstLink, { timeout: 30000 });
  await expect(page.getByRole("heading", { name: "Bad token" })).toBeVisible({
    timeout: 10000,
  });

  await page.goto(secondLink, { timeout: 30000 });
  await expect(page.getByText("Finish setting up your account")).toBeVisible({
    timeout: 10000,
  });
});
