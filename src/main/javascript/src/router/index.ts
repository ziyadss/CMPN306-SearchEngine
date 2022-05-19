import { useAuthStore } from '@/stores/auth';
import { createRouter, createWebHistory } from 'vue-router';
import { login, signup } from './auth';
import home from './home';
import notFound from './notFound';

const routes = [home, login, signup, notFound];

const router = createRouter({ history: createWebHistory(import.meta.env.BASE_URL), routes });

router.beforeEach((to, _from, next) => {
  console.log('from: ', _from);
  console.log('to: ', to);
  const needsAuth = to.meta.requiresAuth;
  const needsUnauth = to.meta.requiresUnauth;
  const loggedIn = useAuthStore().loggedIn;

  if (needsAuth && !loggedIn) return next({ path: '/login', query: { redirect: to.fullPath } });

  if (needsUnauth && loggedIn) return next({ path: '/' });

  return next();
});

export default router;
