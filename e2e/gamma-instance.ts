import {
  GenericContainer,
  RandomPortGenerator,
  StartedTestContainer,
  Wait,
} from "testcontainers";
import type { Server as HttpServer } from "node:http";
import type { GammaEnvironment } from "./gamma-environment";
import { startGammaProxy, stopProxy } from "./gamma-proxy";

export interface GammaInstance {
  container: StartedTestContainer;
  proxy: HttpServer;
  url: string;
  adminCid?: string;
  adminPassword?: string;
  apiKeys?: Partial<Record<GammaBootstrapApiKeyType, GammaApiKeyCredentials>>;
}

export interface GammaFileToCopy {
  source: string;
  target: string;
  mode?: number;
}

export interface GammaStartOptions {
  image?: string;
  logLabel?: string;
  env?: Record<string, string>;
  filesToCopy?: GammaFileToCopy[];
  waitForAdminCredentials?: boolean;
  waitForBootstrapApiKeys?: boolean;
  apiKeys?: Partial<Record<GammaBootstrapApiKeyType, GammaApiKeyCredentials>>;
  publicProtocol?: "http" | "https";
  publicHostname?: string;
}

export interface GammaApiKeyCredentials {
  id: string;
  token: string;
}

export type GammaBootstrapApiKeyType =
  "INFO" | "ACCOUNT_SCAFFOLD" | "ALLOW_LIST";

let instanceCounter = 0;
const gammaMemoryLimitGigabytes = 2;

export async function startGammaInstance(
  env: GammaEnvironment,
  options: GammaStartOptions = {},
): Promise<GammaInstance> {
  const appPort = await new RandomPortGenerator().generatePort();
  const publicPort = await new RandomPortGenerator().generatePort();
  const publicProtocol = options.publicProtocol ?? "https";
  const publicHost =
    options.publicHostname ??
    (publicProtocol === "https" ? "localhost" : "127.0.0.1");
  const publicUrl = `${publicProtocol}://${publicHost}:${publicPort}`;

  const instanceId = instanceCounter++;
  console.log(`Starting Gamma instance ${instanceId}...`);

  let adminCid: string | undefined;
  let adminPassword: string | undefined;
  let credentialsFound = false;
  const apiKeys: Partial<
    Record<GammaBootstrapApiKeyType, GammaApiKeyCredentials>
  > = { ...options.apiKeys };

  const defaultEnvironment: Record<string, string> = {
    DB_HOST: "db",
    DB_PORT: "5432",
    DB_NAME: env.databaseName,
    DB_USER: "postgres",
    DB_PASSWORD: "postgres",
    DB_TRUSTED_PRIVATE_NETWORK: "true",
    // The local TLS proxy reaches Gamma through Docker's dynamic bridge gateway.
    // Trust forwarded headers only in this isolated test environment.
    GAMMA_TRUSTED_PROXIES: ".*",
    REDIS_HOST: "redis",
    REDIS_PORT: "6379",
    SERVER_PORT: "8080",
    ADMIN_SETUP: "true",
    BASE_URL: publicUrl,
    IS_MOCKING: "false",
    UPLOAD_FOLDER: "/tmp/uploads/",
    PRODUCTION: "true",
    GOTIFY_KEY: "123abc",
    GOTIFY_BASE_URL: "http://gotify:8080",
  };

  const effectiveEnvironment = {
    ...defaultEnvironment,
    ...options.env,
  };
  const filesToCopy = options.filesToCopy ?? [];

  const image = options.image ?? process.env.GAMMA_IMAGE ?? "gamma-app:test";
  const logLabel = options.logLabel ?? `GAMMA-${instanceId}`;
  let gammaContainerBuilder = new GenericContainer(image)
    .withNetwork(env.network)
    .withResourcesQuota({ memory: gammaMemoryLimitGigabytes })
    .withNetworkAliases(`gamma-${instanceId}`)
    .withEnvironment(effectiveEnvironment)
    .withExposedPorts({ container: 8080, host: appPort });

  gammaContainerBuilder =
    env.mediaRoot === undefined
      ? gammaContainerBuilder.withTmpFs({
          "/tmp/uploads": "rw,noexec,nosuid,size=100m",
        })
      : gammaContainerBuilder.withBindMounts([
          { source: env.mediaRoot, target: "/tmp/uploads", mode: "rw" },
        ]);

  if (filesToCopy.length > 0) {
    gammaContainerBuilder =
      gammaContainerBuilder.withCopyFilesToContainer(filesToCopy);
  }

  const gammaContainer = await gammaContainerBuilder
    .withLogConsumer((stream) => {
      stream.on("data", (line: Buffer | string) => {
        const logLine = String(line);
        const redactedLogLine = logLine
          .replace(
            /(Admin user created -> cid:[^,]+,password:)\S+/,
            "$1[REDACTED]",
          )
          .replace(
            /(Api key of type [A-Z_]+ has been generated with id: [0-9a-fA-F-]+ and code:) \S+/,
            "$1 [REDACTED]",
          );
        console.log(`[${logLabel}] ${redactedLogLine}`);

        const adminMatch = logLine.match(
          /Admin user created -> cid:([^,]+),password:(\S+)/,
        );
        if (adminMatch?.[1] && adminMatch[2]) {
          adminCid = adminMatch[1];
          adminPassword = adminMatch[2];
          credentialsFound = true;
        }

        const apiKeyMatch = logLine.match(
          /Api key of type ([A-Z_]+) has been generated with id: ([0-9a-fA-F-]+) and code: (\S+)/,
        );
        if (
          apiKeyMatch?.[1] &&
          isGammaBootstrapApiKeyType(apiKeyMatch[1]) &&
          apiKeyMatch[2] &&
          apiKeyMatch[3]
        ) {
          apiKeys[apiKeyMatch[1]] = {
            id: apiKeyMatch[2],
            token: apiKeyMatch[3],
          };
        }
      });
      stream.on("err", (line) => console.error(`[${logLabel}] ${line}`));
      stream.on("end", () => console.log(`[${logLabel}] Log stream ended`));
    })
    .withWaitStrategy(
      Wait.forHttp("/", 8080).forStatusCodeMatching(
        (status) => status >= 200 && status < 500,
      ),
    )
    .withStartupTimeout(300_000)
    .start();

  const port = gammaContainer.getMappedPort(8080);
  const proxy = await startGammaProxy(
    publicProtocol,
    publicPort,
    port,
    publicHost,
  );
  const url = publicUrl;

  console.log(`Gamma instance ${instanceId} started at ${url}`);

  try {
    if (options.waitForAdminCredentials ?? true) {
      await waitForCondition(() => credentialsFound);
      if (!credentialsFound) {
        throw new Error(
          `[${logLabel}] Admin bootstrap credentials were not available after 60 seconds`,
        );
      }
    }

    if (options.waitForBootstrapApiKeys ?? false) {
      const expectedApiKeyTypes = [
        "INFO",
        "ACCOUNT_SCAFFOLD",
        "ALLOW_LIST",
      ] as const;
      await waitForCondition(() =>
        expectedApiKeyTypes.every((type) => apiKeys[type] !== undefined),
      );
      const missingApiKeyTypes = expectedApiKeyTypes.filter(
        (type) => apiKeys[type] === undefined,
      );
      if (missingApiKeyTypes.length > 0) {
        throw new Error(
          `[${logLabel}] Bootstrap API keys were not available after 60 seconds: ${missingApiKeyTypes.join(", ")}`,
        );
      }
    }
  } catch (error) {
    await stopProxy(proxy);
    await gammaContainer.stop();
    throw error;
  }

  return {
    container: gammaContainer,
    proxy,
    url,
    ...(adminCid !== undefined ? { adminCid } : {}),
    ...(adminPassword !== undefined ? { adminPassword } : {}),
    ...(Object.keys(apiKeys).length > 0 ? { apiKeys } : {}),
  };
}

export async function stopGammaInstance(
  instance: GammaInstance,
): Promise<void> {
  console.log("Stopping Gamma instance...");
  await stopProxy(instance.proxy);
  await instance.container.stop();
}

function isGammaBootstrapApiKeyType(
  value: string,
): value is GammaBootstrapApiKeyType {
  return (
    value === "INFO" || value === "ACCOUNT_SCAFFOLD" || value === "ALLOW_LIST"
  );
}

async function waitForCondition(condition: () => boolean): Promise<void> {
  const timeoutAt = Date.now() + 60_000;
  while (!condition() && Date.now() < timeoutAt) {
    await new Promise((resolve) => setTimeout(resolve, 100));
  }
}
