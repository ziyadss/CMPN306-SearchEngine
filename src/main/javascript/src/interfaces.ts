export interface SearchResult {
  title: string;
  url: string;
  snippet: string;
}

export interface QueryResult {
  total: number;
  results: SearchResult[];
}

export interface Error {
  message: string;
}

export interface User {
  displayName: string;
  refreshToken: string;
  email: string;
  ID: string;
  token: string;
}

export interface AuthForm {
  mode: string;
  form: { email: string; password: string };
}
