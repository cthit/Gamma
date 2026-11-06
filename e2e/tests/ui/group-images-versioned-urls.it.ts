import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { login } from "../../helpers/auth";

const digitGroupId = "047ac437-a789-4cc5-bb6e-ba50efd7c509";
const digitSuperGroupId = "aed27030-ad90-4526-855c-1e909b1dcecb";

// A valid 1x1 PNG.
const onePixelPng = Buffer.from(
  "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==",
  "base64",
);

test("given a group banner when a new one is uploaded then its url gets a new version and caching follows the version", async ({
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

  await page.goto(`${gamma.url}/groups/${digitGroupId}`, { timeout: 30000 });
  const banner = page.locator('img[alt="Group banner"]');
  await expect(banner).toBeVisible({ timeout: 10000 });

  const versionedUrl = new RegExp(
    `^/images/group/banner/${digitGroupId}\\?v=([0-9a-f]{16})$`,
  );
  const oldSrc = (await banner.getAttribute("src")) ?? "";
  const oldVersion = versionedUrl.exec(oldSrc)?.[1];
  expect(oldVersion, `banner src ${oldSrc}`).toBeTruthy();

  // The version is stable between page loads, so the browser can use its cache.
  await page.reload();
  await expect(banner).toHaveAttribute("src", oldSrc);

  // The current version can be cached forever.
  const versioned = await page.request.get(`${gamma.url}${oldSrc}`);
  expect(versioned.status()).toBe(200);
  expect(versioned.headers()["cache-control"]).toContain("immutable");
  expect(versioned.headers()["etag"]).toBe(`"${oldVersion}"`);

  // Without a version (e.g. a link in another system) it must be revalidated.
  const unversionedUrl = `${gamma.url}/images/group/banner/${digitGroupId}`;
  const unversioned = await page.request.get(unversionedUrl);
  expect(unversioned.status()).toBe(200);
  expect(unversioned.headers()["cache-control"]).toContain("no-cache");
  expect(unversioned.headers()["etag"]).toBe(`"${oldVersion}"`);
  const revalidated = await page.request.get(unversionedUrl, {
    headers: { "If-None-Match": `"${oldVersion}"` },
  });
  expect(revalidated.status()).toBe(304);

  await page.locator("#edit-group-banner input[type=file]").setInputFiles({
    name: "banner.png",
    mimeType: "image/png",
    buffer: onePixelPng,
  });
  await page.getByRole("button", { name: "Upload banner" }).click();

  await expect(banner).not.toHaveAttribute("src", oldSrc, { timeout: 10000 });
  const newSrc = (await banner.getAttribute("src")) ?? "";
  const newVersion = versionedUrl.exec(newSrc)?.[1];
  expect(newVersion, `banner src ${newSrc}`).toBeTruthy();
  expect(newVersion).not.toBe(oldVersion);

  const newImage = await page.request.get(`${gamma.url}${newSrc}`);
  expect(newImage.status()).toBe(200);
  expect(Buffer.from(await newImage.body())).toEqual(onePixelPng);

  // A cached copy of the old banner is no longer valid.
  const afterUpload = await page.request.get(unversionedUrl, {
    headers: { "If-None-Match": `"${oldVersion}"` },
  });
  expect(afterUpload.status()).toBe(200);
  expect(afterUpload.headers()["etag"]).toBe(`"${newVersion}"`);

  await page.reload();
  await expect(banner).toHaveAttribute("src", newSrc);

  // Super group images are picked at random among its groups, so never cached.
  const superGroupAvatar = await page.request.get(
    `${gamma.url}/images/super-group/avatar/${digitSuperGroupId}`,
  );
  expect(superGroupAvatar.headers()["cache-control"]).toBe("no-store");
});

test("given a signed in user when viewing their avatar then its url is versioned", async ({
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

  await expect(page.locator('img[alt="Me avatar"]')).toHaveAttribute(
    "src",
    /^\/images\/user\/avatar\/[0-9a-f-]{36}\?v=[0-9a-f]{16}$/,
  );
});
