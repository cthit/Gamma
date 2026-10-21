import { Agent, get } from "node:https";
import { setTimeout as delay } from "node:timers/promises";

function getContent(
  url: URL,
  signal: AbortSignal,
  agent: Agent,
): Promise<string> {
  return new Promise((resolve, reject) => {
    const request = get(url, { agent, signal }, (response) => {
      const chunks: Buffer[] = [];
      let size = 0;
      response.on("data", (chunk: Buffer) => {
        size += chunk.length;
        if (size > 2 * 1024 * 1024) {
          response.destroy(
            new Error(`Readiness response too large: ${url.pathname}`),
          );
        } else chunks.push(chunk);
      });
      response.on("error", reject);
      response.on("end", () => {
        if (response.statusCode !== 200) {
          reject(
            new Error(`${url.pathname} returned HTTP ${response.statusCode}`),
          );
        } else if (size === 0) {
          reject(new Error(`${url.pathname} returned an empty response`));
        } else resolve(Buffer.concat(chunks).toString("utf8"));
      });
    });
    request.on("error", (error) =>
      reject(
        new Error(`Readiness request failed: ${url.pathname}`, {
          cause: error,
        }),
      ),
    );
  });
}

export async function waitForLoginReady(
  baseUrl: string,
  timeoutMs = 30000,
): Promise<void> {
  const controller = new AbortController();
  // Keep probe connections and TLS sessions scoped to this short-lived server.
  // https.get speaks HTTP/1.1; browser requests still negotiate HTTP/2 normally.
  const agent = new Agent({
    rejectUnauthorized: false,
    keepAlive: false,
    maxCachedSessions: 0,
    ALPNProtocols: ["http/1.1"],
  });
  const timer = setTimeout(() => controller.abort(), timeoutMs);
  const login = new URL("/login", baseUrl);
  let lastError: unknown;
  try {
    while (!controller.signal.aborted) {
      try {
        const html = await getContent(login, controller.signal, agent);
        if (
          !/name=["']username["']/.test(html) ||
          !/name=["']password["']/.test(html)
        ) {
          throw new Error("/login did not render the login form");
        }
        const assets = new Set<string>();
        for (const [tag] of html.matchAll(/<(?:script|link)\b[^>]*>/gi)) {
          if (/^<link/i.test(tag) && !/\brel=["']stylesheet["']/i.test(tag))
            continue;
          const source = /\b(?:src|href)=["']([^"']+)["']/i.exec(tag)?.[1];
          if (source)
            assets.add(new URL(source.replaceAll("&amp;", "&"), login).href);
        }
        if (assets.size === 0)
          throw new Error("/login did not include scripts or stylesheets");
        // Use the assets the rendered page actually references, so versioned
        // WebJar paths do not have to be duplicated in the test setup.
        for (const asset of assets)
          await getContent(new URL(asset), controller.signal, agent);
        return;
      } catch (error) {
        lastError = error;
        if (!controller.signal.aborted) {
          await delay(100, undefined, { signal: controller.signal }).catch(
            () => {},
          );
        }
      }
    }
    throw new Error(`Gamma login/assets were not ready within ${timeoutMs}ms`, {
      cause: lastError,
    });
  } finally {
    clearTimeout(timer);
    controller.abort();
    agent.destroy();
  }
}
