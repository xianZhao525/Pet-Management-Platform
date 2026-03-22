<template>
  <header class="app-header">
    <div class="header-container">
      <div class="logo" @click="goToHome">
        <el-icon :size="28"><Paw /></el-icon>
        <span>宠物领养平台</span>
      </div>
      
      <el-menu
        :default-active="activeMenu"
        mode="horizontal"
        router
        class="header-menu"
      >
        <el-menu-item index="/">
          <el-icon><House /></el-icon>
          <span>首页</span>
        </el-menu-item>
        <el-menu-item index="/pet/list">
          <el-icon><Paw /></el-icon>
          <span>宠物列表</span>
        </el-menu-item>
        <el-menu-item index="/adoption/process">
          <el-icon><Heart /></el-icon>
          <span>领养流程</span>
        </el-menu-item>
        <el-menu-item index="/donation">
          <el-icon><HandHoldingHeart /></el-icon>
          <span>捐赠支持</span>
        </el-menu-item>
        <el-menu-item index="/about">
          <el-icon><Info /></el-icon>
          <span>关于我们</span>
        </el-menu-item>
      </el-menu>
      
      <div class="user-section">
        <template v-if="isLoggedIn">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="40" :icon="UserFilled" />
              <span class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                <el-dropdown-item command="pets">我的宠物</el-dropdown-item>
                <el-dropdown-item command="orders">我的订单</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </template>
        <template v-else>
          <el-button type="primary" @click="goToLogin">登录</el-button>
          <el-button @click="goToRegister">注册</el-button>
        </template>
      </div>
    </div>
  </header>
</template>

<script setup>
    import { computed } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import { useUserStore } from '@/stores/user'
    import { 
    Paw, House, Heart, HandHoldingHeart, Info, 
    UserFilled, ArrowDown 
    } from '@element-plus/icons-vue'
    import { ElMessage } from 'element-plus'

    const router = useRouter()
    const route = useRoute()
    const userStore = useUserStore()

    const activeMenu = computed(() => route.path)
    const isLoggedIn = computed(() => userStore.isLoggedIn)
    const username = computed(() => userStore.userInfo?.username || '用户')

    const goToHome = () => router.push('/')
    const goToLogin = () => router.push('/login')
    const goToRegister = () => router.push('/register')

    const handleCommand = (command) => {
    switch (command) {
        case 'profile':
        router.push('/user/profile')
        break
        case 'pets':
        router.push('/user/pets')
        break
        case 'orders':
        router.push('/user/orders')
        break
        case 'logout':
        userStore.logout()
        ElMessage.success('退出成功')
        router.push('/')
        break
    }
    }
</script>

<style scoped>
    .app-header {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    background: white;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    z-index: 1000;
    }

    .header-container {
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 20px;
    height: 60px;
    display: flex;
    align-items: center;
    justify-content: space-between;
    }

    .logo {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    font-size: 20px;
    font-weight: bold;
    color: #409eff;
    }

    .logo span {
    background: linear-gradient(135deg, #409eff, #66b1ff);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    }

    .header-menu {
    flex: 1;
    margin-left: 40px;
    border-bottom: none;
    }

    .user-section {
    display: flex;
    align-items: center;
    gap: 12px;
    }

    .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    padding: 5px 10px;
    border-radius: 25px;
    transition: background 0.3s;
    }

    .user-info:hover {
    background: #f5f5f5;
    }

    .username {
    font-size: 14px;
    color: #333;
    }
</style>