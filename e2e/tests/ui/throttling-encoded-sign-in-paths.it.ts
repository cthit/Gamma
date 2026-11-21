import { expect, testWithMockGamma as test } from "../../helpers/test-fixtures";
import { csrfTokenOf } from "../../helpers/a11y";
import type { GammaEnvironment } from "../../gamma-setup";

const MAX_SIGN_IN_ATTEMPTS = 50;

async function signInAttempts(env: GammaEnvironment) {
  const keys = await env.redis.exec([
    "redis-cli",
    "--raw",
    "KEYS",
    "throttle:login:*",
  ]);
  const key = keys.output.trim();
  if (key === "") return 0;
  expect(key.split("\n")).toHaveLength(1);
  const value = await env.redis.exec(["redis-cli", "--raw", "GET", key]);
  return Number(value.output.trim());
}

test("percent-encoded sign-in paths are counted and throttled like the plain ones", async ({
  page,
  gamma,
  env,
}) => {
  await page.goto(`${gamma.url}/login`);
  const csrf = await csrfTokenOf(
    page.getByLabel("Cid or email", { exact: true }),
  );
  const post = (path: string, form: Record<string, string> = {}) =>
    page.request.post(`${gamma.url}${path}`, {
      form: { _csrf: csrf, ...form },
      maxRedirects: 0,
    });

  for (const path of [
    "/log%69n",
    "/login/tot%70",
    "/login/recover%79",
    "/login/webaut%68n",
  ]) {
    const before = await signInAttempts(env);
    await post(path, { username: "jhalpert", password: "wrong" });
    expect(await signInAttempts(env), path).toBe(before + 1);
  }

  while ((await signInAttempts(env)) < MAX_SIGN_IN_ATTEMPTS) {
    await post("/log%69n", { username: "jhalpert", password: "wrong" });
  }

  const throttled = await post("/log%69n", {
    username: "jhalpert",
    password: "password1337",
  });
  expect(throttled.status()).toBe(302);
  expect(throttled.headers()["location"]).toMatch(/\/login\?throttle=true$/);
  const recovery = await post("/login/recover%79", {
    username: "jhalpert",
    recoveryKey: "0000 0000 0000 0000",
  });
  expect(recovery.headers()["location"]).toMatch(
    /\/login\/recovery\?throttle$/,
  );
});
