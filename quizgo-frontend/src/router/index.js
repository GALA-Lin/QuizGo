import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('../layouts/MainLayout.vue'),
    redirect: '/question',
    children: [
      {
        path: 'question',
        name: 'QuestionList',
        component: () => import('../views/question/QuestionList.vue')
      },
      {
        path: 'exam',
        name: 'ExamPage',
        component: () => import('../views/exam/ExamPage.vue')
      },
      {
        path: 'user',
        name: 'UserCenter',
        component: () => import('../views/user/UserCenter.vue')
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
