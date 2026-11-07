import type { Page } from "@playwright/test";
import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";
import type { GammaInstance } from "../../gamma-setup";

const digitGroupId = "047ac437-a789-4cc5-bb6e-ba50efd7c509";
const tooLargeMessage = "The file is too large. It must be at most 2MB.";

function pngOfSize(bytes: number) {
  return {
    name: "banner.png",
    mimeType: "image/png",
    buffer: Buffer.alloc(bytes),
  };
}

async function openDigitGroupAsAdmin(page: Page, gamma: GammaInstance) {
  await login(
    page,
    gamma.url,
    gamma.adminCid ?? "",
    gamma.adminPassword ?? "",
    "admin",
  );
  await page.goto(`${gamma.url}/groups/${digitGroupId}`, { timeout: 30000 });
  await expect(page.getByText("Group banner")).toBeVisible({ timeout: 10000 });
}

async function expectGroupPageIntact(page: Page) {
  await expect(page.getByText("Group details")).toBeVisible();
  await expect(page.getByText("Group avatar")).toBeVisible();
  await expect(page.getByText("Group banner")).toBeVisible();
  await expect(page.getByText("Your information")).toHaveCount(0);
  await expect(page.getByText(/413/)).toHaveCount(0);
}

test("given a banner larger than the limit when selecting it then an error is shown and nothing is uploaded", async ({
  page,
  gamma,
}) => {
  await openDigitGroupAsAdmin(page, gamma);

  const uploadRequests: string[] = [];
  page.on("request", (request) => {
    if (request.url().includes("/groups/banner/")) {
      uploadRequests.push(request.url());
    }
  });

  await page
    .getByLabel("Choose a new banner", { exact: true })
    .setInputFiles(pngOfSize(3 * 1024 * 1024));

  await expect(page.getByRole("alert")).toHaveText(tooLargeMessage);
  await expect(
    page.getByRole("button", { name: "Upload banner" }),
  ).toBeDisabled();
  expect(uploadRequests).toHaveLength(0);
  await expectGroupPageIntact(page);
});

for (const [description, bytes] of [
  ["slightly larger than the limit", 2 * 1024 * 1024 + 100 * 1024],
  ["far larger than the limit", 20 * 1024 * 1024],
] as const) {
  test(`given a banner ${description} when the browser check is bypassed then the server responds with an error alert instead of a broken page`, async ({
    page,
    gamma,
  }) => {
    await openDigitGroupAsAdmin(page, gamma);

    const fileInput = page.getByLabel("Choose a new banner", { exact: true });
    // Simulate a client that does not check the size before uploading.
    await fileInput.evaluate((input: unknown) =>
      (input as { removeAttribute(name: string): void }).removeAttribute(
        "data-max-size",
      ),
    );
    await fileInput.setInputFiles(pngOfSize(bytes));

    const uploadResponse = page.waitForResponse((response) =>
      response.url().includes(`/groups/banner/${digitGroupId}`),
    );
    await page.getByRole("button", { name: "Upload banner" }).click();
    expect((await uploadResponse).status()).toBe(413);

    await expect(page.getByRole("alert")).toHaveText(tooLargeMessage);
    await expectGroupPageIntact(page);
  });
}
