import path from "node:path";
import { readFile, rm } from "node:fs/promises";
import { execFile } from "node:child_process";
import { promisify } from "node:util";

const execute = promisify(execFile);
const statePath = path.resolve(
  import.meta.dirname,
  ".generated/dev-state.json",
);
let serializedState;
try {
  serializedState = await readFile(statePath, "utf8");
} catch (error) {
  if (error instanceof Error && "code" in error && error.code === "ENOENT") {
    process.stdout.write("Local Gamma environment is not running.\n");
    process.exit(0);
  }
  throw error;
}
const state = JSON.parse(serializedState);
const identifiers = [...state.containers, state.network];
if (identifiers.some((value) => !/^[a-f0-9]{12,64}$/.test(value))) {
  throw new Error(
    "The local environment state contains an invalid Docker identifier",
  );
}
for (const container of state.containers) {
  await execute("docker", ["rm", "--force", container]).catch(() => undefined);
}
await execute("docker", ["network", "rm", state.network]).catch(
  () => undefined,
);
await rm(statePath, { force: true });
process.stdout.write("Local Gamma environment stopped.\n");
