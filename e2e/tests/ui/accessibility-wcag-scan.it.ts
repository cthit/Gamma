import AxeBuilder from "@axe-core/playwright";
import type { Page } from "@playwright/test";
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";

// WCAG 2.2 level A and AA, the level Gamma aims for.
const wcagTags = ["wcag2a", "wcag2aa", "wcag21a", "wcag21aa", "wcag22aa"];

const jhalpertUserId = "bc605869-9a4d-46ec-8a29-d00819d4c195";
const digitGroupId = "047ac437-a789-4cc5-bb6e-ba50efd7c509";
const missingId = "00000000-0000-0000-0000-000000000000";

const signedInPages = [
  { name: "home", path: "/" },
  { name: "users", path: "/users" },
  { name: "user details", path: `/users/${jhalpertUserId}` },
  { name: "groups", path: "/groups" },
  { name: "group details", path: `/groups/${digitGroupId}` },
  { name: "super groups", path: "/super-groups" },
  { name: "posts", path: "/posts" },
  { name: "my clients", path: "/my-clients" },
  { name: "create official client", path: "/clients/create" },
  { name: "api keys", path: "/api-keys" },
  { name: "admins", path: "/admins" },
  { name: "gdpr", path: "/gdpr" },
  { name: "allow list", path: "/allow-list" },
  { name: "data deletion", path: "/data-deletion" },
  { name: "delete your account", path: "/delete-your-account" },
  { name: "group not found", path: `/groups/${missingId}` },
];

const darkModePages = [
  "home",
  "users",
  "group details",
  "create official client",
];

async function expectNoViolations(page: Page, pageName: string) {
  const results = await new AxeBuilder({ page }).withTags(wcagTags).analyze();
  const report = results.violations
    .map((violation) =>
      [
        `${violation.id} (${violation.impact ?? "unknown impact"}): ${violation.help}`,
        `  ${violation.helpUrl}`,
        ...violation.nodes.map(
          (node) =>
            `  - ${node.target.join(" ")}\n    ${node.failureSummary?.replaceAll("\n", "\n    ") ?? ""}`,
        ),
      ].join("\n"),
    )
    .join("\n\n");
  expect
    .soft(
      results.violations.map((violation) => violation.id),
      `Accessibility violations on ${pageName} (${page.url()}):\n\n${report}`,
    )
    .toEqual([]);
}

test("given the pages of gamma when scanned with axe then they have no WCAG 2.2 AA violations", async ({
  page,
  gamma,
}) => {
  test.setTimeout(300000);

  await page.goto(`${gamma.url}/login`, { timeout: 60000 });
  await expect(page.getByRole("heading", { name: "Gamma" })).toBeVisible({
    timeout: 60000,
  });
  await expectNoViolations(page, "login");

  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  for (const { name, path } of signedInPages) {
    await test.step(name, async () => {
      await page.goto(`${gamma.url}${path}`, { timeout: 30000 });
      await expect(page.getByRole("main")).toBeVisible();
      await expectNoViolations(page, name);
    });
  }
});

test("given dark mode when the pages of gamma are scanned with axe then they have no WCAG 2.2 AA violations", async ({
  page,
  gamma,
}) => {
  test.setTimeout(180000);
  await page.emulateMedia({ colorScheme: "dark" });

  await page.goto(`${gamma.url}/login`, { timeout: 60000 });
  await expect(page.getByRole("heading", { name: "Gamma" })).toBeVisible({
    timeout: 60000,
  });
  await expectNoViolations(page, "login (dark)");

  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );

  for (const { name, path } of signedInPages.filter((signedInPage) =>
    darkModePages.includes(signedInPage.name),
  )) {
    await test.step(`${name} (dark)`, async () => {
      await page.goto(`${gamma.url}${path}`, { timeout: 30000 });
      await expect(page.getByRole("main")).toBeVisible();
      await expectNoViolations(page, `${name} (dark)`);
    });
  }
});
