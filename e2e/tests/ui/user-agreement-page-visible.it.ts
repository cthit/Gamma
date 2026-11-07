import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import { articleByHeading } from "../../helpers/a11y";

test("given a signed in user when opening user agreement then agreement content is shown", async ({
  page,
  gamma,
}) => {
  await login(page, gamma.url, "jhalpert", "password1337", "Big Tuna");

  await page.goto(`${gamma.url}/user-agreement`, { timeout: 30000 });

  await expect(
    page.getByRole("heading", {
      level: 2,
      name: "User agreement",
      exact: true,
    }),
  ).toBeVisible({
    timeout: 10000,
  });
  await expect(articleByHeading(page, "User agreement")).toContainText(
    "This agreement refers to IT:s user account system Gamma.",
    {
      timeout: 10000,
    },
  );
});
