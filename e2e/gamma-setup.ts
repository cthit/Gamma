import {
  GenericContainer,
  Network,
  StartedNetwork,
  StartedTestContainer,
  Wait,
} from "testcontainers";
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
}

export interface GammaEnvironment {
  network: StartedNetwork;
  postgres: StartedPostgreSqlContainer;
  redis: StartedRedisContainer;
  gotify: StartedTestContainer;
}

let instanceCounter = 0;

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
): Promise<GammaInstance> {
  const instanceId = instanceCounter++;
  console.log(`Starting Gamma instance ${instanceId}...`);

  let adminCid: string | undefined;
  let adminPassword: string | undefined;
  let credentialsFound = false;

  const gammaContainer = await new GenericContainer("gamma-app:test")
    .withNetwork(env.network)
    .withNetworkAliases(`gamma-${instanceId}`)
    .withEnvironment({
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
      BASE_URL: `http://localhost:8080`,
      PRODUCTION: "true",
      IS_MOCKING: "false",
      UPLOAD_FOLDER: "/uploads/",
    })
    .withExposedPorts(8080)
    .withLogConsumer((stream) => {
      stream.on("data", (line) => {
        const logLine = line.toString();
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
      });
      stream.on("err", (line) =>
        console.error(`[GAMMA-${instanceId}] ${line}`),
      );
      stream.on("end", () =>
        console.log(`[GAMMA-${instanceId}] Log stream ended`),
      );
    })
    .withWaitStrategy(
      Wait.forHttp("/", 8080).forStatusCodeMatching(
        (status) => status >= 200 && status < 500,
      ),
    )
    .start();

  const port = gammaContainer.getMappedPort(8080);
  const url = `http://localhost:${port}`;

  console.log(`Gamma instance ${instanceId} started at ${url}`);

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

  return {
    container: gammaContainer,
    url,
    adminCid,
    adminPassword,
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
