export function uniqueLabel(prefix: string): string {
  const nonce = `${Date.now().toString(36)}${Math.floor(Math.random() * 1296)
    .toString(36)
    .padStart(2, "0")}`;
  return `${prefix}-${nonce}`;
}

export function uniqueCid(prefix = "e2e"): string {
  const alphabet = "abcdefghijklmnopqrstuvwxyz";
  const digitToLetter = (value: string) =>
    value.replace(/[0-9]/g, (digit) => alphabet[Number(digit)]);
  const normalizedPrefix = prefix.toLowerCase().replace(/[^a-z]/g, "");
  const entropy = digitToLetter(
    `${Date.now().toString(36)}${Math.random().toString(36).slice(2, 10)}`,
  ).replace(/[^a-z]/g, "");

  let cid = `${normalizedPrefix}${entropy}`;
  while (cid.length < 4) {
    cid += "a";
  }

  return cid.slice(0, 12);
}

export function uniqueEmail(prefix = "e2e"): string {
  return `${uniqueCid(prefix)}@example.org`;
}
