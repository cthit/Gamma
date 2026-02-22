import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";

test("given an admin user when viewing navigation then admin links are visible", async ({
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

  await expect(page.getByRole("link", { name: "Api keys" })).toBeVisible({
    timeout: 10000,
  });
  await expect(page.getByRole("link", { name: "Admins" })).toBeVisible({
    timeout: 10000,
  });
  await expect(page.getByRole("link", { name: "Allow lists" })).toBeVisible({
    timeout: 10000,
  });
});
