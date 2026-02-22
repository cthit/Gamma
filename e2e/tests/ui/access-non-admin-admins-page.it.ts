import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";

test("given a non admin user when opening admins page then unauthorized is shown", async ({ page, gamma }) => {

  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  await page.goto(`${gamma.url}/admins`, { timeout: 30000 });

  await expect(page.getByText("403 - Unauthorized")).toBeVisible({
    timeout: 10000,
  });
  await expect(
    page.getByText("You are not authorized to view this page."),
  ).toBeVisible({
    timeout: 10000,
  });
});
