import axios from 'axios';

export default axios.create({
  baseURL: import.meta.env.VITE_APP_SEARCH_ENGINE_API
});

export const axiosAuth = axios.create({
  baseURL: 'https://identitytoolkit.googleapis.com/v1/accounts'
});

// headers: {
//   'Content-Type': 'application/json',
//   Authorization: `Bearer ${store.state.token}`,
// },
