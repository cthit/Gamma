import { readFile } from "node:fs/promises";
import {
  createServer as createHttpServer,
  request as httpRequest,
  type Server as HttpServer,
} from "node:http";
import { createServer as createHttpsServer } from "node:https";
import {
  e2eKeyMaterialPaths,
  ensureE2eKeyMaterial,
} from "./gamma-key-material";

export async function startGammaProxy(
  publicProtocol: "http" | "https",
  publicPort: number,
  upstreamPort: number,
  publicHostname: string,
): Promise<HttpServer> {
  return publicProtocol === "https"
    ? startHttpsProxy(publicPort, upstreamPort, publicHostname)
    : startHttpProxy(publicPort, upstreamPort, publicHostname);
}

async function startHttpsProxy(
  publicPort: number,
  upstreamPort: number,
  publicHostname: string,
): Promise<HttpServer> {
  await ensureE2eKeyMaterial();
  const [certificate, privateKey] = await Promise.all([
    readFile(e2eKeyMaterialPaths.proxyCertificate),
    readFile(e2eKeyMaterialPaths.proxyPrivateKey),
  ]);
  const server = createHttpsServer(
    { cert: certificate, key: privateKey },
    (request, response) => {
      // Chromium serializes the origin as "null" after accepting this
      // self-signed test certificate. Production certificates retain the real
      // origin, so make the local TLS terminator reproduce that behavior.
      const origin =
        request.headers.origin === "null"
          ? `https://${publicHostname}:${publicPort}`
          : request.headers.origin;
      const upstream = httpRequest(
        {
          host: "127.0.0.1",
          port: upstreamPort,
          path: request.url,
          method: request.method,
          headers: {
            ...request.headers,
            host: `${publicHostname}:${publicPort}`,
            ...(origin === undefined ? {} : { origin }),
            "x-forwarded-host": `${publicHostname}:${publicPort}`,
            "x-forwarded-port": String(publicPort),
            "x-forwarded-proto": "https",
          },
        },
        (upstreamResponse) => {
          response.writeHead(
            upstreamResponse.statusCode ?? 502,
            upstreamResponse.headers,
          );
          upstreamResponse.pipe(response);
        },
      );
      upstream.on("error", (error) => {
        if (!response.headersSent) response.writeHead(502);
        response.end(String(error));
      });
      request.pipe(upstream);
    },
  );
  await new Promise<void>((resolve, reject) => {
    server.once("error", reject);
    // Listen on every local interface. The public hostname belongs in the
    // forwarded request headers, but using it as the bind address makes the
    // harness depend on whether the host resolves localhost to IPv4 or IPv6.
    server.listen(publicPort, resolve);
  });
  return server;
}

async function startHttpProxy(
  publicPort: number,
  upstreamPort: number,
  publicHostname: string,
): Promise<HttpServer> {
  const server = createHttpServer((request, response) => {
    const origin =
      request.headers.origin === "null"
        ? `http://${publicHostname}:${publicPort}`
        : request.headers.origin;
    const upstream = httpRequest(
      {
        host: "127.0.0.1",
        port: upstreamPort,
        path: request.url,
        method: request.method,
        headers: {
          ...request.headers,
          host: `${publicHostname}:${publicPort}`,
          ...(origin === undefined ? {} : { origin }),
          "x-forwarded-host": `${publicHostname}:${publicPort}`,
          "x-forwarded-port": String(publicPort),
          "x-forwarded-proto": "http",
        },
      },
      (upstreamResponse) => {
        response.writeHead(
          upstreamResponse.statusCode ?? 502,
          upstreamResponse.headers,
        );
        upstreamResponse.pipe(response);
      },
    );
    upstream.on("error", (error) => {
      if (!response.headersSent) response.writeHead(502);
      response.end(String(error));
    });
    request.pipe(upstream);
  });
  await new Promise<void>((resolve, reject) => {
    server.once("error", reject);
    server.listen(publicPort, resolve);
  });
  return server;
}

export async function stopProxy(server: HttpServer): Promise<void> {
  await new Promise<void>((resolve, reject) => {
    server.close((error) => (error === undefined ? resolve() : reject(error)));
    // Browser contexts retain HTTP keep-alive connections until their test
    // fixture is torn down, which happens after this application cleanup.
    server.closeAllConnections();
  });
}
