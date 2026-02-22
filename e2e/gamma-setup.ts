import {
  GenericContainer,
  Network,
  StartedNetwork,
  StartedTestContainer,
  Wait,
} from "testcontainers";
import path from "node:path";
import { access, mkdir, rename, rm } from "node:fs/promises";
import { execFile } from "node:child_process";
import {
  PostgreSqlContainer,
  StartedPostgreSqlContainer,
} from "@testcontainers/postgresql";
import { RedisContainer, StartedRedisContainer } from "@testcontainers/redis";

export interface GammaInstance {
  container: StartedTestContainer;
  url: string;
  adminCid?: string;
  adminPassword?: string;
  apiKeys?: Partial<Record<GammaBootstrapApiKeyType, GammaApiKeyCredentials>>;
}

export interface GammaEnvironment {
  network: StartedNetwork;
  postgres: StartedPostgreSqlContainer;
  redis: StartedRedisContainer;
  gotify: StartedTestContainer;
}

export interface GammaFileToCopy {
  source: string;
  target: string;
  mode?: number;
}

export interface GammaStartOptions {
  env?: Record<string, string>;
  filesToCopy?: GammaFileToCopy[];
  waitForAdminCredentials?: boolean;
  waitForBootstrapApiKeys?: boolean;
}

export interface GammaApiKeyCredentials {
  id: string;
  token: string;
}

export type GammaBootstrapApiKeyType =
  | "INFO"
  | "ACCOUNT_SCAFFOLD"
  | "ALLOW_LIST";

let instanceCounter = 0;
const generatedTlsDir = path.resolve(__dirname, ".generated", "tls");
const tlsFilePrefix = `localhost-${process.pid}`;
const generatedTlsCertificatePath = path.join(
  generatedTlsDir,
  `${tlsFilePrefix}.crt`,
);
const generatedTlsPrivateKeyPath = path.join(generatedTlsDir, `${tlsFilePrefix}.key`);
let localhostTlsFilesPromise: Promise<boolean> | undefined;

export async function startDependencies(): Promise<GammaEnvironment> {
  const network = await new Network().start();

  console.log("Starting PostgreSQL container...");
  const postgres = await new PostgreSqlContainer("postgres:16")
    .withNetwork(network)
    .withNetworkAliases("db")
    .withDatabase("postgres")
    .withUsername("postgres")
    .withPassword("postgres")
    .withLogConsumer((stream) => {
      stream.on("data", (line) => console.log(`[POSTGRES] ${line}`));
      stream.on("err", (line) => console.error(`[POSTGRES] ${line}`));
    })
    .start();

  console.log("Starting Redis container...");
  const redis = await new RedisContainer("redis:5.0")
    .withNetwork(network)
    .withNetworkAliases("redis")
    .withLogConsumer((stream) => {
      stream.on("data", (line) => console.log(`[REDIS] ${line}`));
      stream.on("err", (line) => console.error(`[REDIS] ${line}`));
    })
    .start();

  console.log("Starting Gotify container...");
  const gotify = await new GenericContainer("cthit/gotify:latest")
    .withNetwork(network)
    .withNetworkAliases("gotify")
    .withEnvironment({
      "GOTIFY_PRE-SHARED-KEY": "123abc",
      "GOTIFY_MOCK-MODE": "true",
      "GOTIFY_DEBUG-MODE": "true",
    })
    .withExposedPorts(80)
    .withLogConsumer((stream) => {
      stream.on("data", (line) => console.log(`[GOTIFY] ${line}`));
      stream.on("err", (line) => console.error(`[GOTIFY] ${line}`));
    })
    .withWaitStrategy(Wait.forLogMessage("Serving application on port 8080"))
    .start();

  console.log("Dependencies started successfully!");

  return {
    network,
    postgres,
    redis,
    gotify,
  };
}

export async function startGammaInstance(
  env: GammaEnvironment,
  options: GammaStartOptions = {},
): Promise<GammaInstance> {
  const localhostTlsFilesAvailable = await ensureLocalhostTlsFiles();

  const instanceId = instanceCounter++;
  console.log(`Starting Gamma instance ${instanceId}...`);

  let adminCid: string | undefined;
  let adminPassword: string | undefined;
  let credentialsFound = false;
  const apiKeys: Partial<
    Record<GammaBootstrapApiKeyType, GammaApiKeyCredentials>
  > = {};

  const defaultEnvironment: Record<string, string> = {
    DB_HOST: "db",
    DB_PORT: "5432",
    DB_NAME: "postgres",
    DB_USER: "postgres",
    DB_PASSWORD: "postgres",
    REDIS_HOST: "redis",
    REDIS_PORT: "6379",
    SERVER_PORT: "8080",
    GOTIFY_KEY: "123abc",
    GOTIFY_BASE_URL: "http://gotify:80",
    ADMIN_SETUP: "true",
    BASE_URL: `https://localhost:8080`,
    PRODUCTION: "true",
    IS_MOCKING: "false",
    SERVER_SSL_ENABLED: "true",
    UPLOAD_FOLDER: "/tmp/uploads/",
  };

  if (localhostTlsFilesAvailable) {
    defaultEnvironment.SERVER_SSL_CERTIFICATE = "file:/tmp/tls/localhost.crt";
    defaultEnvironment.SERVER_SSL_CERTIFICATE_PRIVATE_KEY =
      "file:/tmp/tls/localhost.key";
  }

  const tlsFiles: GammaFileToCopy[] = localhostTlsFilesAvailable
    ? [
        {
          source: generatedTlsCertificatePath,
          target: "/tmp/tls/localhost.crt",
          mode: 0o644,
        },
        {
          source: generatedTlsPrivateKeyPath,
          target: "/tmp/tls/localhost.key",
          mode: 0o644,
        },
      ]
    : [];

  const filesToCopy = [...tlsFiles, ...(options.filesToCopy ?? [])];

  let gammaContainerBuilder = new GenericContainer("gamma-app:test")
    .withNetwork(env.network)
    .withNetworkAliases(`gamma-${instanceId}`)
    .withEnvironment({
      ...defaultEnvironment,
      ...options.env,
    })
    .withTmpFs({ "/tmp/uploads": "rw,noexec,nosuid,size=100m" })
    .withExposedPorts(8080);

  if (filesToCopy.length > 0) {
    gammaContainerBuilder =
      gammaContainerBuilder.withCopyFilesToContainer(filesToCopy);
  }

  const gammaContainer = await gammaContainerBuilder
    .withLogConsumer((stream) => {
      stream.on("data", (line: Buffer | string) => {
        const logLine = String(line);
        console.log(`[GAMMA-${instanceId}] ${logLine}`);

        const adminMatch = logLine.match(
          /Admin user created -> cid:([^,]+),password:(\S+)/,
        );
        if (adminMatch) {
          adminCid = adminMatch[1];
          adminPassword = adminMatch[2];
          credentialsFound = true;
          console.log(
            `[GAMMA-${instanceId}] Captured admin credentials: cid=${adminCid}`,
          );
        }

        const apiKeyMatch = logLine.match(
          /Api key of type ([A-Z_]+) has been generated with id: ([0-9a-fA-F-]+) and code: (\S+)/,
        );
        if (apiKeyMatch) {
          const keyType = apiKeyMatch[1];
          const keyId = apiKeyMatch[2];
          const keyToken = apiKeyMatch[3];
          if (
            keyType &&
            isGammaBootstrapApiKeyType(keyType) &&
            keyId &&
            keyToken
          ) {
            apiKeys[keyType] = {
              id: keyId,
              token: keyToken,
            };
            console.log(
              `[GAMMA-${instanceId}] Captured ${keyType} api key credentials`,
            );
          }
        }
      });
      stream.on("err", (line) =>
        console.error(`[GAMMA-${instanceId}] ${line}`),
      );
      stream.on("end", () =>
        console.log(`[GAMMA-${instanceId}] Log stream ended`),
      );
    })
    .withWaitStrategy(
      Wait.forHttp("/", 8080)
        .usingTls()
        .allowInsecure()
        .forStatusCodeMatching((status) => status >= 200 && status < 500),
    )
    .start();

  const port = gammaContainer.getMappedPort(8080);
  const url = `https://127.0.0.1:${port}`;

  console.log(`Gamma instance ${instanceId} started at ${url}`);

  if (options.waitForAdminCredentials ?? true) {
    const maxWaitTime = 30000;
    const startTime = Date.now();
    while (!credentialsFound && Date.now() - startTime < maxWaitTime) {
      await new Promise((resolve) => setTimeout(resolve, 100));
    }

    if (!credentialsFound) {
      throw new Error(
        `[GAMMA-${instanceId}] Admin credentials not found in logs after ${maxWaitTime}ms`,
      );
    }
  }

  if (options.waitForBootstrapApiKeys ?? false) {
    const expectedApiKeyTypes: GammaBootstrapApiKeyType[] = [
      "INFO",
      "ACCOUNT_SCAFFOLD",
      "ALLOW_LIST",
    ];
    const maxWaitTime = 30000;
    const startTime = Date.now();
    while (
      expectedApiKeyTypes.some((type) => apiKeys[type] === undefined) &&
      Date.now() - startTime < maxWaitTime
    ) {
      await new Promise((resolve) => setTimeout(resolve, 100));
    }

    if (expectedApiKeyTypes.some((type) => apiKeys[type] === undefined)) {
      throw new Error(
        `[GAMMA-${instanceId}] Bootstrap api key credentials not found in logs after ${maxWaitTime}ms`,
      );
    }
  }

  return {
    container: gammaContainer,
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
  await instance.container.stop();
}

export async function stopDependencies(env: GammaEnvironment): Promise<void> {
  console.log("Stopping Gotify...");
  await env.gotify.stop();

  console.log("Stopping Redis...");
  await env.redis.stop();

  console.log("Stopping PostgreSQL...");
  await env.postgres.stop();

  console.log("Stopping network...");
  await env.network.stop();

  console.log("All dependencies stopped.");
}

function isGammaBootstrapApiKeyType(
  value: string,
): value is GammaBootstrapApiKeyType {
  return (
    value === "INFO" || value === "ACCOUNT_SCAFFOLD" || value === "ALLOW_LIST"
  );
}

async function ensureLocalhostTlsFiles(): Promise<boolean> {
  if (localhostTlsFilesPromise === undefined) {
    localhostTlsFilesPromise = createOrReuseLocalhostTlsFiles().catch(
      (error: unknown) => {
        localhostTlsFilesPromise = undefined;
        throw error;
      },
    );
  }

  return localhostTlsFilesPromise;
}

async function createOrReuseLocalhostTlsFiles(): Promise<boolean> {
  const [certificateExists, privateKeyExists] = await Promise.all([
    fileExists(generatedTlsCertificatePath),
    fileExists(generatedTlsPrivateKeyPath),
  ]);

  if (certificateExists && privateKeyExists) {
    return true;
  }

  await mkdir(generatedTlsDir, { recursive: true });

  const suffix = `${process.pid}-${Date.now()}-${Math.random().toString(16).slice(2)}`;
  const tempCertificatePath = path.join(
    generatedTlsDir,
    `localhost.${suffix}.crt.tmp`,
  );
  const tempPrivateKeyPath = path.join(
    generatedTlsDir,
    `localhost.${suffix}.key.tmp`,
  );

  try {
    try {
      await runOpenSslGenerateLocalhostCertificate(
        tempCertificatePath,
        tempPrivateKeyPath,
      );
    } catch (error: unknown) {
      if (error instanceof OpenSslMissingError) {
        console.warn(
          "OpenSSL is not available; continuing with app default TLS certificate.",
        );
        return false;
      }
      throw error;
    }

    await rename(tempCertificatePath, generatedTlsCertificatePath);
    await rename(tempPrivateKeyPath, generatedTlsPrivateKeyPath);
    return true;
  } finally {
    await Promise.all([
      rm(tempCertificatePath, { force: true }),
      rm(tempPrivateKeyPath, { force: true }),
    ]);
  }
}

async function fileExists(filePath: string): Promise<boolean> {
  try {
    await access(filePath);
    return true;
  } catch {
    return false;
  }
}

async function runOpenSslGenerateLocalhostCertificate(
  certificatePath: string,
  privateKeyPath: string,
): Promise<void> {
  const args = [
    "req",
    "-x509",
    "-nodes",
    "-newkey",
    "rsa:2048",
    "-sha256",
    "-days",
    "3650",
    "-subj",
    "/CN=localhost",
    "-addext",
    "subjectAltName=DNS:localhost,IP:127.0.0.1",
    "-keyout",
    privateKeyPath,
    "-out",
    certificatePath,
  ];

  await new Promise<void>((resolve, reject) => {
    execFile(
      "openssl",
      args,
      { encoding: "utf8" },
      (error, _stdout, stderr) => {
        if (error) {
          if (error.code === "ENOENT") {
            reject(new OpenSslMissingError("OpenSSL command was not found"));
            return;
          }
          reject(
            new Error(
              `Failed to generate localhost TLS certificate with OpenSSL: ${stderr}`,
            ),
          );
          return;
        }

        resolve();
      },
    );
  });
}

class OpenSslMissingError extends Error {}
