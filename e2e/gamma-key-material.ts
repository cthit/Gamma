import { execFile } from "node:child_process";
import { access, mkdir, rename, rm } from "node:fs/promises";
import path from "node:path";

const generatedKeyStoreDir = path.resolve(__dirname, ".generated", "e2e");

export const e2eKeyMaterialPaths = {
  proxyCertificate: path.join(generatedKeyStoreDir, `proxy-${process.pid}.crt`),
  proxyPrivateKey: path.join(generatedKeyStoreDir, `proxy-${process.pid}.key`),
  oauthKeyStore: path.join(generatedKeyStoreDir, `signing-${process.pid}.p12`),
  redisCertificate: path.join(generatedKeyStoreDir, `redis-${process.pid}.crt`),
  redisPrivateKey: path.join(generatedKeyStoreDir, `redis-${process.pid}.key`),
  redisTrustStore: path.join(
    generatedKeyStoreDir,
    `redis-trust-${process.pid}.p12`,
  ),
} as const;

let e2eKeyMaterialPromise: Promise<void> | undefined;

export async function ensureE2eKeyMaterial(): Promise<void> {
  if (e2eKeyMaterialPromise === undefined) {
    e2eKeyMaterialPromise = createOrReuseE2eKeyMaterial().catch(
      (error: unknown) => {
        e2eKeyMaterialPromise = undefined;
        throw error;
      },
    );
  }

  return e2eKeyMaterialPromise;
}

async function createOrReuseE2eKeyMaterial(): Promise<void> {
  const filesExist = await Promise.all([
    fileExists(e2eKeyMaterialPaths.proxyCertificate),
    fileExists(e2eKeyMaterialPaths.proxyPrivateKey),
    fileExists(e2eKeyMaterialPaths.oauthKeyStore),
    fileExists(e2eKeyMaterialPaths.redisCertificate),
    fileExists(e2eKeyMaterialPaths.redisPrivateKey),
    fileExists(e2eKeyMaterialPaths.redisTrustStore),
  ]);
  if (filesExist.every(Boolean)) return;

  await mkdir(generatedKeyStoreDir, { recursive: true });

  const suffix = `${process.pid}-${Date.now()}-${Math.random().toString(16).slice(2)}`;
  const tempCertificatePath = path.join(
    generatedKeyStoreDir,
    `signing.${suffix}.crt.tmp`,
  );
  const tempPrivateKeyPath = path.join(
    generatedKeyStoreDir,
    `signing.${suffix}.key.tmp`,
  );
  const tempKeyStorePath = path.join(
    generatedKeyStoreDir,
    `signing.${suffix}.p12.tmp`,
  );
  const tempRedisCertificatePath = path.join(
    generatedKeyStoreDir,
    `redis.${suffix}.crt.tmp`,
  );
  const tempRedisPrivateKeyPath = path.join(
    generatedKeyStoreDir,
    `redis.${suffix}.key.tmp`,
  );
  const tempRedisTrustStorePath = path.join(
    generatedKeyStoreDir,
    `redis-trust.${suffix}.p12.tmp`,
  );

  try {
    try {
      await runOpenSslGenerateE2eCertificate(
        tempCertificatePath,
        tempPrivateKeyPath,
      );
    } catch (error: unknown) {
      if (error instanceof OpenSslMissingError) {
        throw new Error(
          "OpenSSL is required to create the e2e HTTPS proxy certificate and OAuth signing key store.",
          { cause: error },
        );
      }
      throw error;
    }

    await runOpenSslExportKeyStore(
      tempCertificatePath,
      tempPrivateKeyPath,
      tempKeyStorePath,
    );
    await runOpenSslGenerateCertificate(
      "redis",
      "DNS:redis",
      tempRedisCertificatePath,
      tempRedisPrivateKeyPath,
    );
    await runKeytoolCreateTrustStore(
      tempRedisCertificatePath,
      tempRedisTrustStorePath,
    );
    await rename(tempCertificatePath, e2eKeyMaterialPaths.proxyCertificate);
    await rename(tempPrivateKeyPath, e2eKeyMaterialPaths.proxyPrivateKey);
    await rename(tempKeyStorePath, e2eKeyMaterialPaths.oauthKeyStore);
    await rename(
      tempRedisCertificatePath,
      e2eKeyMaterialPaths.redisCertificate,
    );
    await rename(tempRedisPrivateKeyPath, e2eKeyMaterialPaths.redisPrivateKey);
    await rename(tempRedisTrustStorePath, e2eKeyMaterialPaths.redisTrustStore);
  } finally {
    await Promise.all([
      rm(tempCertificatePath, { force: true }),
      rm(tempPrivateKeyPath, { force: true }),
      rm(tempKeyStorePath, { force: true }),
      rm(tempRedisCertificatePath, { force: true }),
      rm(tempRedisPrivateKeyPath, { force: true }),
      rm(tempRedisTrustStorePath, { force: true }),
    ]);
  }
}

async function runOpenSslExportKeyStore(
  certificatePath: string,
  privateKeyPath: string,
  keyStorePath: string,
): Promise<void> {
  await runOpenSsl([
    "pkcs12",
    "-export",
    "-name",
    "current",
    "-in",
    certificatePath,
    "-inkey",
    privateKeyPath,
    "-passout",
    "pass:e2e-oauth-keystore-password",
    "-out",
    keyStorePath,
  ]);
}

async function fileExists(filePath: string): Promise<boolean> {
  try {
    await access(filePath);
    return true;
  } catch {
    return false;
  }
}

async function runOpenSslGenerateE2eCertificate(
  certificatePath: string,
  privateKeyPath: string,
): Promise<void> {
  await runOpenSslGenerateCertificate(
    "localhost",
    "DNS:localhost,IP:127.0.0.1",
    certificatePath,
    privateKeyPath,
  );
}

async function runOpenSslGenerateCertificate(
  commonName: string,
  subjectAlternativeName: string,
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
    `/CN=${commonName}`,
    "-addext",
    `subjectAltName=${subjectAlternativeName}`,
    "-keyout",
    privateKeyPath,
    "-out",
    certificatePath,
  ];

  await runOpenSsl(args);
}

async function runKeytoolCreateTrustStore(
  certificatePath: string,
  trustStorePath: string,
): Promise<void> {
  await new Promise<void>((resolve, reject) => {
    execFile(
      "keytool",
      [
        "-importcert",
        "-noprompt",
        "-alias",
        "redis",
        "-file",
        certificatePath,
        "-keystore",
        trustStorePath,
        "-storetype",
        "PKCS12",
        "-storepass",
        "changeit",
      ],
      { encoding: "utf8" },
      (error, _stdout, stderr) => {
        if (error) {
          reject(new Error(`Failed to create Redis trust store: ${stderr}`));
          return;
        }
        resolve();
      },
    );
  });
}

async function runOpenSsl(args: string[]): Promise<void> {
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
              `Failed to generate e2e key material with OpenSSL: ${stderr}`,
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
