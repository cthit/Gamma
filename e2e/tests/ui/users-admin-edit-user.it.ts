import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { uniqueCid } from "../../helpers/strings";
import { articleByHeading, rowWithText } from "../../helpers/a11y";

test("given an admin user when editing another user then updated details are shown", async ({
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

  const updatedNick = uniqueCid("nick");
  const updatedFirstName = uniqueCid("first");

  await page.goto(`${gamma.url}/users`, { timeout: 30000 });
  await rowWithText(page, "jhalpert")
    .getByRole("link", { name: "Details" })
    .click();

  await expect(page.getByText("User details")).toBeVisible({
    timeout: 10000,
  });

  await page.getByRole("button", { name: "Edit user" }).click();
  await page.getByLabel("Nick", { exact: true }).fill(updatedNick);
  await page.getByLabel("First name", { exact: true }).fill(updatedFirstName);

  await Promise.all([
    page.waitForResponse(
      (response) =>
        response.request().method() === "POST" &&
        response.url().includes("/users/") &&
        response.status() >= 200 &&
        response.status() < 400,
    ),
    page.getByRole("button", { name: "Save" }).click(),
  ]);

  await expect(page.getByText("User updated")).toBeVisible({
    timeout: 10000,
  });
  await expect(articleByHeading(page, "User details")).toContainText(
    updatedFirstName,
    {
      timeout: 10000,
    },
  );
});
