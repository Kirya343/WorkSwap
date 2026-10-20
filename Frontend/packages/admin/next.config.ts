import path from "path";
import { type NextConfig } from "next";

const nextConfig: NextConfig = {
    // Остальные твои настройки Next.js
    transpilePackages: ["@workswap/frontend-core"],

    sassOptions: {
        // Указываем папку core/src/css как includePath
        includePaths: [path.join(__dirname, "../core/src/css")],
    },

    output: "standalone"
};

export default nextConfig;