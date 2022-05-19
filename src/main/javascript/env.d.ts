/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_APP_SEARCH_ENGINE_API: string;
  readonly VITE_APP_PAGINATION_LIMIT: number;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
