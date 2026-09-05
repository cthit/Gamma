import {
  GenericContainer,
  Network,
  StartedNetwork,
  StartedTestContainer,
  Wait,
} from "testcontainers";
import { readdir } from "node:fs/promises";
import path from "node:path";
import {
  PostgreSqlContainer,
  StartedPostgreSqlContainer,
} from "@testcontainers/postgresql";

export interface GammaEnvironment {
  network: StartedNetwork;
  postgres: StartedPostgreSqlContainer;
  databaseName: string;
  redis: StartedTestContainer;
  gotify: StartedTestContainer;
  mediaRoot?: string;
}

let databaseCounter = 0;
const gammaMemoryLimitGigabytes = 2;

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
  const redis = await new GenericContainer("redis:7-alpine")
    .withNetwork(network)
    .withNetworkAliases("redis")
    .withExposedPorts(6379)
    .withWaitStrategy(Wait.forLogMessage("Ready to accept connections"))
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
    .withExposedPorts(8080)
    .withLogConsumer((stream) => {
      stream.on("data", (line) => console.log(`[GOTIFY] ${line}`));
      stream.on("err", (line) => console.error(`[GOTIFY] ${line}`));
    })
    .withWaitStrategy(Wait.forLogMessage("Serving application on port 8080"))
    .start();

  await initializeDatabase(network, postgres);
  console.log("Dependencies started successfully!");

  return {
    network,
    postgres,
    databaseName: "postgres",
    redis,
    gotify,
  };
}

export async function createIsolatedEnvironment(
  sharedEnv: GammaEnvironment,
): Promise<GammaEnvironment> {
  const databaseName = `gamma_e2e_${process.pid}_${databaseCounter++}`;
  const created = await sharedEnv.postgres.exec([
    "createdb",
    "--username",
    "postgres",
    "--template",
    sharedEnv.databaseName,
    databaseName,
  ]);
  if (created.exitCode !== 0) {
    throw new Error(
      `Could not clone the E2E database: ${created.output.trim()}`,
    );
  }

  const cleared = await sharedEnv.redis.exec(["redis-cli", "FLUSHDB"]);
  if (cleared.exitCode !== 0 || cleared.output.trim() !== "OK") {
    throw new Error("Could not clear the dedicated E2E Redis database");
  }

  return { ...sharedEnv, databaseName };
}

export async function removeIsolatedEnvironment(
  env: GammaEnvironment,
): Promise<void> {
  const dropped = await env.postgres.exec([
    "dropdb",
    "--username",
    "postgres",
    "--force",
    env.databaseName,
  ]);
  if (dropped.exitCode !== 0) {
    throw new Error(
      `Could not remove isolated E2E database ${env.databaseName}: ${dropped.output.trim()}`,
    );
  }
}

async function initializeDatabase(
  network: StartedNetwork,
  postgres: StartedPostgreSqlContainer,
): Promise<void> {
  const image =
    "flyway/flyway@sha256:eaaaef9e81578af3226576a6ff2b8ae949588e0ba298e9cc2e1b8c2fdb14b85a";
  const migrationDirectory = path.resolve(
    __dirname,
    "../app/src/main/resources/db/migration",
  );
  const expectedMigrationCount = (
    await readdir(migrationDirectory, { withFileTypes: true })
  ).filter(
    (entry) => entry.isFile() && /^V\d+(?:[._]\d+)*__.+\.sql$/.test(entry.name),
  ).length;
  if (expectedMigrationCount === 0) {
    throw new Error(
      `No versioned Flyway migrations found in '${migrationDirectory}'`,
    );
  }
  console.log("Initializing the database schema...");

  await new GenericContainer(image)
    .withNetwork(network)
    .withResourcesQuota({ memory: gammaMemoryLimitGigabytes })
    .withBindMounts([
      { source: migrationDirectory, target: "/flyway/sql", mode: "ro" },
    ])
    .withCommand([
      "-url=jdbc:postgresql://db:5432/postgres",
      "-user=postgres",
      "-password=postgres",
      "-connectRetries=30",
      "migrate",
    ])
    .withWaitStrategy(Wait.forOneShotStartup())
    .withStartupTimeout(300_000)
    .start();

  const history = await postgres.exec([
    "psql",
    "--username",
    "postgres",
    "--dbname",
    "postgres",
    "--tuples-only",
    "--no-align",
    "--command",
    "SELECT COUNT(*) || '|' || (SELECT COUNT(*) FROM g_user) " +
      "FROM flyway_schema_history WHERE success",
  ]);
  const expectedHistory = `${expectedMigrationCount}|0`;
  if (history.exitCode !== 0 || history.output.trim() !== expectedHistory) {
    throw new Error(
      `Schema initialization returned '${history.output.trim()}', expected ${expectedMigrationCount} migrations and no domain rows`,
    );
  }
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
