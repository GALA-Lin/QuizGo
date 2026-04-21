import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
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

// 路由守卫：检查登录状态
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  // 如果去的不是登录页，且没有 token，强制跳到登录页
  if (to.path !== '/login' && !token) {
    next('/login')
  }
  // 如果已经登录了还想去登录页，直接拦回到首页
  else if (to.path === '/login' && token) {
    next('/')
  }
  // 其他情况正常放行
  else {
    next()
  }
})

export default router