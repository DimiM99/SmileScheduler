/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_CRYPTO_SECRET: string;
    readonly PS_KEY: string;
    readonly PS_TOKEN: string;
    // Add other environment variables here...
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}