import { createRouter, createWebHistory } from 'vue-router'
import MainLayout from '@/layouts/MainLayout.vue'
import AdminLayout from '@/layouts/AdminLayout.vue'

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
          component: () => import('@/App.vue')
        },
        //宠物领养服务
        {
          path: 'adoption/petAdoption',
          name: 'PetAdoption',
          component: () => import('@/views/adoption/PetAdoption.vue')
        },
        {
          path: 'adoption/petAdoptionDetails',
          name: 'PetAdoptionDetails',
          component: () => import('@/views/adoption/PetAdoptionDetails.vue')
        },
        //宠物爱心捐赠服务
        {
          path: 'donation/charityDonation',
          name: 'CharityDonation',
          component: () => import('@/views/donation/CharityDonation.vue')
        },
        //宠物临时寄养服务
        {
          path: 'foster/temporaryFosterCare',
          name: 'TemporaryFosterCare',
          component: () => import('@/views/foster/TemporaryFosterCare.vue')
        }
      ]
    },

    //需要用AdminLayout包裹的页面：
    {
      path: '/admin',
      component: AdminLayout,
      children: [
        //管理员服务
        //管理员控制台
        {
          path: 'admin/user/administratorConsole',
          name: 'AdministratorConsole',
          component: () => import('@/views/admin/user/AdministratorConsole.vue')
        },
        //管理员宠物管理
        {
          path: 'admin/pet/addPet',
          name: 'AddPet',
          component: () => import('@/views/admin/pet/AddPet.vue')
        },
        {
          path: 'admin/pet/petManagement',
          name: 'PetManagement',
          component: () => import('@/views/admin/pet/PetManagement.vue')
        },
        //管理员宠物领养管理
        {
          path: 'admin/adoption/adoptionApplication',
          name: 'AdoptionApplication',
          component: () => import('@/views/admin/adoption/AdoptionApplication.vue')
        },
        {
          path: 'admin/adoption/adoptionApplicationDetails',
          name: 'AdoptionApplicationDetails',
          component: () => import('@/views/admin/adoption/AdoptionApplicationDetails.vue')
        }
      ]
    }
  ]
})

export default router