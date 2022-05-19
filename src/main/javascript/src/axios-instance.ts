import axios from 'axios';
import type { QueryResult } from './interfaces';

const axiosSearch = axios.create({
  baseURL: import.meta.env.VITE_APP_SEARCH_ENGINE_API
});

export function searchAPI(query: string, page = 1, lucky = false): Promise<QueryResult> {
  return axiosSearch
    .get(`/search?q=${encodeURIComponent(query)}&page=${page}${lucky ? '&lucky' : ''}`)
    .then(({ data }) => {
      return { total: data.total, results: data.results, tokens: data.tokens };
    });
}

export function suggestAPI(query: string): Promise<string[]> {
  return axiosSearch.get(`/suggest?q=${encodeURIComponent(query)}`).then(({ data }) => {
    console.log(data);
    return data.suggestions;
  });
}
