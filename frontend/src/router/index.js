import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

// 独立页面（不需要导航栏和页脚）
import Login from '@/views/user/login.vue'
import Register from '@/views/user/register.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // ========== 独立页面：登录、注册（不使用布局） ==========
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

    // ========== 前端页面：使用 MainLayout 包裹 ==========
    {
      path: '/',
      component: MainLayout,
      children: [
        {
          path: '',           // 首页
          name: 'Home',
          component: () => import('@/views/Home.vue')
        },
        // 宠物领养服务 - 修改路径为绝对路径
        {
          path: '/adoption/petAdoption',           // 改为绝对路径 /
          name: 'PetAdoption',
          component: () => import('@/views/adoption/PetAdoption.vue')
        },
        {
          path: '/adoption/petAdoptionDetails/:id',    // 改为绝对路径，添加参数
          name: 'PetAdoptionDetails',
          component: () => import('@/views/adoption/PetAdoptionDetails.vue')
        },
        // 宠物爱心捐赠服务
        {
          path: '/donation/charityDonation',       // 改为绝对路径
          name: 'CharityDonation',
          component: () => import('@/views/donation/CharityDonation.vue')
        },
        // 宠物临时寄养服务
        {
          path: '/foster/temporaryFosterCare',     // 改为绝对路径
          name: 'TemporaryFosterCare',
          component: () => import('@/views/foster/TemporaryFosterCare.vue')
        }
      ]
    },

    // ========== 后台管理：使用 AdminLayout 包裹 ==========
    {
      path: '/admin',
      component: AdminLayout,
      children: [
        // 管理员控制台
        {
          path: 'user/administratorConsole',      // 相对路径，完整路径 /admin/user/administratorConsole
          name: 'AdministratorConsole',
          component: () => import('@/views/admin/user/AdministratorConsole.vue')
        },
        // 管理员宠物管理
        {
          path: 'pet/addPet',                     // 相对路径
          name: 'AddPet',
          component: () => import('@/views/admin/pet/AddPet.vue')
        },
        {
          path: 'pet/petManagement',              // 相对路径
          name: 'PetManagement',
          component: () => import('@/views/admin/pet/PetManagement.vue')
        },
        // 管理员宠物领养管理
        {
          path: 'adoption/adoptionApplication',           // 相对路径
          name: 'AdoptionApplication',
          component: () => import('@/views/admin/adoption/AdoptionApplication.vue')
        },
        {
          path: 'adoption/adoptionApplicationDetails/:id',    // 相对路径，添加参数
          name: 'AdoptionApplicationDetails',
          component: () => import('@/views/admin/adoption/AdoptionApplicationDetails.vue')
        }
      ]
    }
  ]
})

export default router