import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'

// 独立页面（不需要导航栏和页脚）
import Login from '@/views/user/login.vue'
import Register from '@/views/user/register.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    // 独立页面：登录、注册（不使用布局）
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/register',
      name: 'Register',
      component: Register
    },

    // 需要布局的页面：使用 MainLayout 包裹
    {
      path: '/',
      component: MainLayout,
      children: [
        {
          path: '',
          name: 'Home',
          component: () => import('@/views/Home.vue')
        },
        {
          path: 'pet/list',
          name: 'PetList',
          component: () => import('@/views/pet/PetList.vue')
        },
        {
          path: 'user/dashboard',
          name: 'UserDashboard',
          component: () => import('@/views/user/UserDashboard.vue')
        },
        {
          path: 'user/profile',
          name: 'UserProfile',
          component: () => import('@/views/user/UserProfile.vue')
        },
        {
          path: 'adoption/my',
          name: 'MyAdoption',
          component: () => import('@/views/adoption/MyAdoption.vue')
        }
        // ... 其他需要布局的页面
      ]
    }
  ]
})

export default router