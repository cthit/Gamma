import path from "node:path";
import { mkdir, rm, writeFile } from "node:fs/promises";
import {
  startDependencies,
  startGammaInstance,
  stopDependencies,
  stopGammaInstance,
} from "./gamma-setup";

async function main(): Promise<void> {
  const statePath = path.resolve(__dirname, ".generated/dev-state.json");
  const environment = await startDependencies();
  const instance = await startGammaInstance(environment, {
    image: process.env.GAMMA_IMAGE ?? "gamma-app:test",
  });

  try {
    await mkdir(path.dirname(statePath), { recursive: true });
    await writeFile(
      statePath,
      JSON.stringify({
        containers: [
          instance.container.getId(),
          environment.postgres.getId(),
          environment.redis?.getId(),
        ].filter(Boolean),
        network: environment.network.getId(),
      }),
    );

    console.log(`Gamma is ready at ${instance.url}`);
    console.log(`Local administrator: ${instance.adminCid}`);
    console.log(`Local password: ${instance.adminPassword}`);
    console.log("Press Ctrl+C to stop the environment.");

    await new Promise<void>((resolve) => {
      process.once("SIGINT", resolve);
      process.once("SIGTERM", resolve);
    });
  } finally {
    await stopGammaInstance(instance);
    await stopDependencies(environment);
    await rm(statePath, { force: true });
  }
}

main().catch((error: unknown) => {
  console.error(error);
  process.exitCode = 1;
});
