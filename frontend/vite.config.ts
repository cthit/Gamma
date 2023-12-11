import {defineConfig} from "vite";
import react from "@vitejs/plugin-react";

/** @type {import('vite').UserConfig} */
export default defineConfig({
  plugins: [react()],
  server: {
    origin: "http://gamma:3000",
    host: "gamma",
    port: 3000,
    proxy: {
      "/api/": {
        target: "http://gamma:8081",
        changeOrigin: true,
        secure: false,
        configure: (proxy) => {
          proxy.on("error", (err) => {
            console.log("proxy error", err);
          });
          proxy.on("proxyReq", (proxyReq, req) => {
            console.log("Sending Request to the Target:", req.method, req.url);
          });
          proxy.on("proxyRes", (proxyRes, req) => {
            console.log(
              "Received Response from the Target:",
              proxyRes.statusCode,
              req.url,
            );
          });
        },
      },
    },
  },
});
