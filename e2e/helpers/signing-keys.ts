import { createHash, createPublicKey, generateKeyPairSync } from "node:crypto";

export interface SigningKey {
  privateKeyPem: string;
  publicKeyPem: string;
}

export function generateSigningKey(
  format: "pkcs1" | "pkcs8" = "pkcs8",
): SigningKey {
  const { privateKey, publicKey } = generateKeyPairSync("rsa", {
    modulusLength: 2048,
    privateKeyEncoding: { type: format, format: "pem" },
    publicKeyEncoding: { type: "spki", format: "pem" },
  });
  return { privateKeyPem: privateKey, publicKeyPem: publicKey };
}

export function publishedJwk(key: SigningKey) {
  const { kty, e, n } = createPublicKey(key.publicKeyPem).export({
    format: "jwk",
  });
  const kid = createHash("sha256")
    .update(JSON.stringify({ e, kty, n }))
    .digest("base64url");
  return { kty, e, n, kid, use: "sig", alg: "RS256" };
}

let defaultSigningKey: SigningKey | undefined;

export function getDefaultSigningKey(): SigningKey {
  defaultSigningKey ??= generateSigningKey();
  return defaultSigningKey;
}
