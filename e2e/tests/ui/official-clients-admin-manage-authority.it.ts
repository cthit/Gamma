import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid, uniqueLabel } from "../../helpers/strings";
import { articleByHeading } from "../../helpers/a11y";

test("given an official client when an admin creates and deletes an authority then authority membership is updated", async ({
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

  const prettyName = uniqueLabel("E2E Official Client");
  const authorityName = uniqueCid("auth");

  await page.goto(`${gamma.url}/clients/create`, { timeout: 30000 });
  await page.getByLabel("Pretty name", { exact: true }).fill(prettyName);
  await page
    .getByLabel("Swedish description", { exact: true })
    .fill("E2E svensk beskrivning");
  await page
    .getByLabel("English description", { exact: true })
    .fill("E2E english description");
  await page
    .getByLabel("Redirect url", { exact: true })
    .fill("https://example.org/callback");

  await Promise.all([
    page.waitForURL("**/clients/*", { timeout: 15000 }),
    page.getByRole("button", { name: "Create" }).click(),
  ]);

  await expect(page.getByText("Client details")).toBeVisible({
    timeout: 10000,
  });

  await page.getByLabel("Authority name", { exact: true }).fill(authorityName);

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/authority") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    articleByHeading(page, "Create client authority")
      .getByRole("button", { name: "Save", exact: true })
      .click(),
  ]);

  const authorityArticle = articleByHeading(page, authorityName);

  await expect(authorityArticle).toBeVisible({
    timeout: 10000,
  });

  page.once("dialog", async (dialog) => dialog.accept());
  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes(`/authority/${authorityName}`) &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    authorityArticle.getByRole("button", { name: "Delete" }).click(),
  ]);

  await expect(articleByHeading(page, authorityName)).toHaveCount(0);
});
