import axios from 'axios';
import type { QueryResult } from './interfaces';

const axiosSearch = axios.create({
  baseURL: import.meta.env.VITE_APP_SEARCH_ENGINE_API
});

export function search(query: string, page = 1, lucky = false): Promise<QueryResult> {
  return axiosSearch
    .get(`/search?q=${query}&page=${page}${lucky ? '&lucky' : ''}`)
    .then(({ data }) => {
      return { total: data.total, results: data.results };
    });
}

export const axiosAuth = axios.create({
  baseURL: 'https://identitytoolkit.googleapis.com/v1/accounts'
});

// headers: {
//   'Content-Type': 'application/json',
//   Authorization: `Bearer ${store.state.token}`,
// },
