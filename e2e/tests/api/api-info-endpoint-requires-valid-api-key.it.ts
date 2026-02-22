import {
  expect,
  testWithDefaultGamma as test,
} from "../../helpers/test-fixtures";

test("given missing or invalid api key when calling info blob then request is unauthorized", async ({
  request,
  gamma,
}) => {
  const missingAuthResponse = await request.get(
    `${gamma.url}/api/info/v1/blob`,
  );
  expect(missingAuthResponse.status()).toBe(401);

  const invalidAuthResponse = await request.get(
    `${gamma.url}/api/info/v1/blob`,
    {
      headers: {
        Authorization: "pre-shared invalid-id:invalid-token",
      },
    },
  );
  expect(invalidAuthResponse.status()).toBe(401);
});
