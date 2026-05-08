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
    // 将默认首页由 /question 改为 /dashboard
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        // 指向你即将新建的 dashboard 页面
        component: () => import('../views/dashboard/Index.vue')
      },
      {
        path: 'question',
        name: 'QuestionList',
        component: () => import('../views/question/QuestionList.vue')
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
  else {
    next()
  }
})

export default router