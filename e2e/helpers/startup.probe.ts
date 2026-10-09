import { execFile } from "node:child_process";
import path from "node:path";
import { promisify } from "node:util";
import { expect, test, type Request, type Response } from "@playwright/test";
import {
  startDependencies,
  startGammaInstance,
  stopDependencies,
  stopGammaInstance,
  type GammaInstance,
} from "../gamma-setup";

const execFileAsync = promisify(execFile);

test("a cold Gamma serves the login page and its assets", async ({
  page,
  request: independentRequest,
}, testInfo) => {
  const env = await startDependencies();
  let gamma: GammaInstance | undefined;
  const pending = new Set<Request>();
  const failures: string[] = [];
  const events: unknown[] = [];
  const client = await page.context().newCDPSession(page);
  await client.send("Network.enable");
  const methods = [
    "Network.requestWillBeSent",
    "Network.requestWillBeSentExtraInfo",
    "Network.responseReceived",
    "Network.loadingFinished",
    "Network.loadingFailed",
  ] as const;
  const listeners = methods.map((method) => {
    const listener = (event: unknown) => {
      events.push({ time: Date.now(), method, event });
    };
    client.on(method, listener);
    return { method, listener };
  });
  const started = (request: Request) => pending.add(request);
  const finished = (request: Request) => pending.delete(request);
  const failed = (request: Request) => {
    pending.delete(request);
    failures.push(`${request.url()}: ${request.failure()?.errorText}`);
  };
  const received = (response: Response) => {
    if (
      ["script", "stylesheet", "image"].includes(
        response.request().resourceType(),
      ) &&
      response.status() !== 200
    ) {
      failures.push(`${response.url()}: HTTP ${response.status()}`);
    }
  };
  page.on("request", started);
  page.on("requestfinished", finished);
  page.on("requestfailed", failed);
  page.on("response", received);
  try {
    gamma = await startGammaInstance(env, {
      env: {
        IS_MOCKING: "true",
        MOCK_DATA_RESOURCE: "file:/tmp/e2e-mock.json",
        SERVER_HTTP2_ENABLED: String(testInfo.project.name === "http2"),
        LOGGING_LEVEL_ORG_APACHE_COYOTE_HTTP2: "DEBUG",
      },
      waitForBootstrapApiKeys: true,
      filesToCopy: [
        {
          source: path.resolve(__dirname, "../fixtures/mock/e2e-mock.json"),
          target: "/tmp/e2e-mock.json",
        },
      ],
    });
    const response = await page.goto(gamma.url, { timeout: 60000 });
    expect(response?.status()).toBe(200);
    await expect(page.locator('input[name="username"]')).toBeVisible();
    await expect(page.locator('input[name="password"]')).toBeVisible();
    expect(pending.size).toBe(0);
    expect(failures).toEqual([]);
  } finally {
    // Capture requests and server state while the container and browser are
    // still alive; connection errors from teardown must not obscure a stall.
    try {
      await testInfo.attach("network-before-teardown", {
        body: JSON.stringify({
          pending: [...pending].map((request) => ({
            url: request.url(),
            type: request.resourceType(),
          })),
          failures,
          events,
        }),
        contentType: "application/json",
      });
      if (gamma) {
        const baseUrl = gamma.url;
        if (pending.size > 0) {
          await gamma.container.exec(["sh", "-c", "kill -QUIT 1"]);
          const checks = await Promise.all(
            ["/login", "/css/main.css"].flatMap((asset) =>
              [false, true].map(async (withCookies) => {
                try {
                  const response = await (
                    withCookies ? page.request : independentRequest
                  ).get(`${baseUrl}${asset}`, {
                    timeout: 5000,
                  });
                  return { asset, withCookies, status: response.status() };
                } catch (error) {
                  return { asset, withCookies, error: String(error) };
                }
              }),
            ),
          );
          await testInfo.attach("independent-http-checks", {
            body: JSON.stringify(checks),
            contentType: "application/json",
          });
        }
        const logs = await execFileAsync(
          "docker",
          ["logs", gamma.container.getId()],
          {
            timeout: 10000,
            maxBuffer: 16 * 1024 * 1024,
          },
        );
        await testInfo.attach("server-before-teardown", {
          body: logs.stdout + logs.stderr,
          contentType: "text/plain",
        });
      }
    } finally {
      page.off("request", started);
      page.off("requestfinished", finished);
      page.off("requestfailed", failed);
      page.off("response", received);
      for (const { method, listener } of listeners)
        client.off(method, listener);
      await client.detach();
      try {
        if (gamma) await stopGammaInstance(gamma);
      } finally {
        await stopDependencies(env);
      }
    }
  }
});
