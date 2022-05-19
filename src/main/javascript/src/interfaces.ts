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
