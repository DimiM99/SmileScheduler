/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_CRYPTO_SECRET: string;
    // Add other environment variables here...
}

interface ImportMeta {
    readonly env: ImportMetaEnv;
}