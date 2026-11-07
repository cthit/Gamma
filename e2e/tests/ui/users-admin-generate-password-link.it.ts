import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { rowWithText, articleByHeading } from "../../helpers/a11y";

test("given an admin user when generating a password reset link then a link is shown", async ({
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

  await page.goto(`${gamma.url}/users`, { timeout: 30000 });
  await rowWithText(page, "jhalpert")
    .getByRole("link", { name: "Details" })
    .click();

  const passwordLinkArticle = articleByHeading(
    page,
    "Generate reset password link",
  );

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/generate-password-link") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    passwordLinkArticle.getByRole("button", { name: "Generate" }).click(),
  ]);

  await expect(passwordLinkArticle.getByRole("code")).toBeVisible({
    timeout: 10000,
  });
  await expect(passwordLinkArticle.getByRole("code")).toContainText(
    "forgot-password",
  );
});
