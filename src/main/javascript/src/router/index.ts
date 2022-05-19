import { createRouter, createWebHistory } from 'vue-router';
import home from './home';
import notFound from './notFound';
import results from './results';
import about from './about';

const routes = [results, home, about, notFound];

export default createRouter({ history: createWebHistory(import.meta.env.BASE_URL), routes });
