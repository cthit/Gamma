import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";

test("given a non admin user when viewing navigation then admin links are hidden", async ({ page, gamma }) => {

  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  await expect(page.getByRole("link", { name: "Users" })).toBeVisible({
    timeout: 10000,
  });
  await expect(page.getByRole("link", { name: "Api keys" })).toHaveCount(0);
  await expect(page.getByRole("link", { name: "Admins" })).toHaveCount(0);
  await expect(page.getByRole("link", { name: "Allow lists" })).toHaveCount(
    0,
  );
});
