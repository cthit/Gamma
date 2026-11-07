import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid } from "../../helpers/strings";
import { articleByHeading } from "../../helpers/a11y";

test("given a signed in user when editing profile info then home page shows updated values", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  const updatedNick = uniqueCid("nick");

  await page.getByRole("button", { name: "Edit" }).click();
  await expect(page.getByText("Editing your information")).toBeVisible({
    timeout: 10000,
  });

  await page.getByLabel("Nick", { exact: true }).fill(updatedNick);

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/me") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.getByRole("button", { name: "Save" }).click(),
  ]);

  await expect(
    page.getByText("You have successfully edited your information"),
  ).toBeVisible({ timeout: 10000 });

  await expect(articleByHeading(page, "Your information")).toContainText(
    updatedNick,
    { timeout: 10000 },
  );
});
