import UserAuth from '@/views/auth/UserAuth.vue';

export const login = {
  path: '/login',
  name: 'Log In',
  props: { mode: 'login' },
  component: UserAuth,
  meta: { requiresUnauth: true }
};

export const signup = {
  path: '/signup',
  name: 'Sign Up',
  props: { mode: 'signup' },
  component: UserAuth,
  meta: { requiresUnauth: true }
};
