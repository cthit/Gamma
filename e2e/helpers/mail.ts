import { execFile } from "node:child_process";
import { setTimeout as delay } from "node:timers/promises";
import { promisify } from "node:util";
import type { StartedTestContainer } from "testcontainers";

const execFileAsync = promisify(execFile);
type MailPath = "/register" | "/forgot-password/finalize";

// Go's %#v uses quoted Go strings, not JSON: \a, \v, \xNN, octal and
// \UNNNNNNNN escapes are valid too. Decode byte escapes before UTF-8 decoding.
function unquoteGoString(quoted: string): string {
  const bytes: number[] = [];
  const escapes: Record<string, number> = {
    a: 7,
    b: 8,
    f: 12,
    n: 10,
    r: 13,
    t: 9,
    v: 11,
    '"': 34,
    "\\": 92,
  };
  for (let i = 1; i < quoted.length - 1; i++) {
    const character = quoted[i]!;
    if (character !== "\\") {
      const codePoint = quoted.codePointAt(i)!;
      bytes.push(...Buffer.from(String.fromCodePoint(codePoint)));
      if (codePoint > 0xffff) i++;
      continue;
    }
    const escape = quoted[++i]!;
    if (Object.hasOwn(escapes, escape)) {
      bytes.push(escapes[escape]!);
    } else if (escape === "x" || escape === "u" || escape === "U") {
      const length = escape === "x" ? 2 : escape === "u" ? 4 : 8;
      const digits = quoted.slice(i + 1, i + 1 + length);
      if (digits.length !== length || !/^[\da-f]+$/i.test(digits)) {
        throw new Error("Invalid hexadecimal escape in Gotify mail log");
      }
      const value = Number.parseInt(digits, 16);
      if (escape === "x") bytes.push(value);
      else bytes.push(...Buffer.from(String.fromCodePoint(value)));
      i += length;
    } else if (/[0-7]/.test(escape)) {
      const digits = quoted.slice(i, i + 3);
      if (!/^[0-7]{3}$/.test(digits) || Number.parseInt(digits, 8) > 255) {
        throw new Error("Invalid octal escape in Gotify mail log");
      }
      bytes.push(Number.parseInt(digits, 8));
      i += 2;
    } else {
      throw new Error("Unknown escape in Gotify mail log");
    }
  }
  return Buffer.from(bytes).toString("utf8");
}

export function latestMailLink(
  logs: string,
  baseUrl: string,
  recipient: string,
  pathname: MailPath,
): string | undefined {
  const marker = /Sending mail:\s*gotify\.Mail\{/g;
  let latest: string | undefined;
  while (marker.exec(logs) !== null) {
    const start = marker.lastIndex;
    let depth = 1;
    let quoted = false;
    let escaped = false;
    let end = start;
    // Braces and newlines may occur in mail content or attachment structs.
    // Only a complete outer struct is a complete log record.
    for (; end < logs.length && depth > 0; end++) {
      const character = logs[end];
      if (quoted) {
        if (escaped) escaped = false;
        else if (character === "\\") escaped = true;
        else if (character === '"') quoted = false;
      } else if (character === '"') quoted = true;
      else if (character === "{") depth++;
      else if (character === "}") depth--;
    }
    // A snapshot can end while Gotify is writing a record. Wait for the next
    // snapshot instead of returning an older match ahead of that record.
    if (depth !== 0) return undefined;
    const record = logs.slice(start, end - 1);
    marker.lastIndex = end;
    const fields = new Map<string, string>();
    // Match every quoted field, consuming strings whole so field-like text in
    // a subject/body cannot be mistaken for the top-level recipient or body.
    for (const field of record.matchAll(
      /\b(\w+):\s*("(?:[^"\\]|\\[\s\S])*")/g,
    )) {
      if (field[1] === "To" || field[1] === "Body") {
        fields.set(field[1], unquoteGoString(field[2]!));
      }
    }
    if (fields.get("To") !== recipient) continue;
    for (const link of (fields.get("Body") ?? "").matchAll(
      /https?:\/\/[^\s<>"']+/g,
    )) {
      const url = new URL(link[0]);
      if (url.pathname === pathname && url.searchParams.get("token")) {
        // Preserve the actual mailed path/query; BASE_URL uses the internal
        // container port, so navigate through Gamma's externally mapped origin.
        latest = `${baseUrl}${url.pathname}${url.search}`;
      }
    }
  }
  return latest;
}

export async function waitForMailLink(
  gotify: Pick<StartedTestContainer, "getId">,
  baseUrl: string,
  recipient: string,
  pathname: MailPath,
  timeoutMs = 15000,
): Promise<string> {
  const deadline = performance.now() + timeoutMs;
  while (performance.now() < deadline) {
    // Read a finite snapshot, not `docker logs --follow`. execFile buffers
    // partial stdout chunks with a UTF-8 decoder and closes its pipe listeners
    // when the process exits. Its timeout kills a stuck reader as well.
    const { stdout } = await execFileAsync("docker", ["logs", gotify.getId()], {
      encoding: "utf8",
      timeout: Math.max(1, Math.ceil(deadline - performance.now())),
      killSignal: "SIGKILL",
      maxBuffer: 4 * 1024 * 1024,
    });
    const link = latestMailLink(stdout, baseUrl, recipient, pathname);
    if (link) return link;
    await delay(Math.max(0, Math.min(200, deadline - performance.now())));
  }
  throw new Error(
    `No ${pathname} mail for ${recipient} in Gotify logs within ${timeoutMs}ms`,
  );
}
