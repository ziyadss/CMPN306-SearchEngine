/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_APP_FIREBASE_KEY: string;
  readonly VITE_APP_SEARCH_ENGINE_API: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
