import { createRouter, createWebHistory } from 'vue-router';
import type { RouteRecordRaw } from 'vue-router';
import ProcessManagement from '../views/ProcessManagement.vue';
import ProcessDesigner from '../views/ProcessDesigner.vue';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/',
    name: 'ProcessManagement',
    component: ProcessManagement
  },
  {
    path: '/designer',
    name: 'ProcessDesigner',
    component: ProcessDesigner
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;