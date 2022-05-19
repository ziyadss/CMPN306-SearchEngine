import { axiosAuth as axios } from '@/axios-instance';
import { defineStore } from 'pinia';

interface User {
  displayName: string;
  refreshToken: string;
  email: string;
  ID: string;
  token: string;
}

interface AuthForm {
  mode: string;
  form: { email: string; password: string };
}

let timer: NodeJS.Timeout;
export const useAuthStore = defineStore({
  id: 'auth',
  state: () => {
    return { user: undefined as User | undefined };
  },
  getters: {
    displayName: (state) => state.user?.displayName,
    id: (state) => state.user?.ID,
    token: (state) => state.user?.token,
    loggedIn: (state) => !!state.user?.token,
    email: (state) => state.user?.email
  },
  actions: {
    authenticate(payload: AuthForm) {
      const form = { returnSecureToken: true, ...payload.form };
      const modeURL = payload.mode === 'signup' ? 'signUp' : 'signInWithPassword';

      return axios
        .post(`:${modeURL}?key=${import.meta.env.VITE_APP_FIREBASE_KEY}`, form)
        .then(({ data }) => {
          const expiresIn = data.expiresIn * 1000;
          const userData = {
            displayName: data.displayName,
            refreshToken: data.refreshToken,
            email: data.email,
            ID: data.localId,
            token: data.idToken
          };

          timer = setTimeout(() => this.logout(), expiresIn);

          localStorage.setItem('userData', JSON.stringify(userData));
          localStorage.setItem('expiry', (expiresIn + new Date().getTime()).toString());

          this.user = userData;
        })
        .catch((e) => {
          throw e.response.data.error || { message: 'UNKNOWN_ERROR' };
        });
    },
    fetchUser() {
      const data = localStorage.getItem('userData');
      if (!data) return;
      const userData = JSON.parse(data);

      // if token has more than 1 min left
      const expiresIn = parseInt(localStorage.getItem('expiry') ?? '') - new Date().getTime();
      if (expiresIn > 1000 * 60) {
        timer = setTimeout(() => this.logout(), expiresIn);
        this.user = userData;
      }
    },
    logout() {
      localStorage.removeItem('userData');
      localStorage.removeItem('expiry');
      this.user = undefined;
      clearTimeout(timer);
    }
  }
});
