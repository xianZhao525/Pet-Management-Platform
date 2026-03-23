<template>
  <nav class="navbar">
    <div class="container navbar-container">
      <!-- Logo -->
      <a href="#" class="logo" @click.prevent="navigateTo('/')">
        <i class="fas fa-paw"></i> 
        宠物领养平台
      </a>

      <!-- 导航链接 -->
      <ul class="nav-links">
        <li v-for="item in navItems" :key="item.path">
          <a 
            href="#" 
            @click.prevent="navigateTo(item.path)"
            :class="{ active: isActive(item.path) }"
          >
            {{ item.name }}
          </a>
        </li>
      </ul>

      <!-- 用户区域（移动端显示在底部，桌面端显示在右侧） -->
      <div class="user-area">
        <!-- 已登录状态 -->
        <div v-if="isLoggedIn" class="user-menu">
          <el-dropdown @command="handleUserCommand" trigger="click">
            <div class="user-info">
              <el-avatar :size="36" :icon="UserFilled" />
              <span class="username">{{ username }}</span>
              <el-icon><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <!-- 管理员菜单 -->
                <template v-if="isAdmin">
                  <el-dropdown-item command="adminDashboard">
                    <i class="fas fa-cog"></i> 管理员控制台
                  </el-dropdown-item>
                  <el-dropdown-item divided command="adminCenter">
                    <i class="fas fa-user-circle"></i> 管理员中心
                  </el-dropdown-item>
                </template>
                
                <!-- 普通用户菜单 -->
                <template v-else>
                  <el-dropdown-item command="userDashboard">
                    <i class="fas fa-user-circle"></i> 个人中心
                  </el-dropdown-item>
                </template>
                
                <!-- 公共菜单 -->
                <el-dropdown-item command="myApplications">
                  <i class="fas fa-file-alt"></i> 我的申请
                </el-dropdown-item>
                <el-dropdown-item command="editProfile">
                  <i class="fas fa-edit"></i> 编辑资料
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <i class="fas fa-sign-out-alt"></i> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>

        <!-- 未登录状态 -->
        <div v-else class="auth-buttons">
          <el-button class="btn-outline" @click="goToLogin">
            登录
          </el-button>
          <el-button type="primary" @click="goToRegister">
            注册
          </el-button>
        </div>
      </div>
    </div>
  </nav>
</template>

<script setup>
    import { ref, computed, onMounted } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import { useUserStore } from '@/stores/userStore'
    import { ElMessage } from 'element-plus'
    import { UserFilled, ArrowDown } from '@element-plus/icons-vue'

    const router = useRouter()
    const route = useRoute()
    const userStore = useUserStore()

    // 导航菜单配置
    const navItems = ref([
    { name: '首页', path: '/' },
    { name: '领养宠物', path: '/pet/list' },
    { name: '临时寄养', path: '/foster/list' },
    { name: '爱心捐赠', path: '/donation/list' }
    ])

    // 计算属性
    const isLoggedIn = computed(() => userStore.isLoggedIn)
    const username = computed(() => userStore.userInfo?.username || '用户')
    const isAdmin = computed(() => userStore.userInfo?.role === 'ADMIN')

    // 判断当前路由是否激活
    const isActive = (path) => {
    if (path === '/') {
        return route.path === '/'
    }
    return route.path.startsWith(path)
    }

    // 导航方法
    const navigateTo = (path) => {
    router.push(path)
    }

    const goToLogin = () => {
    router.push('/login')
    }

    const goToRegister = () => {
    router.push('/register')
    }

    // 用户菜单命令处理
    const handleUserCommand = (command) => {
    switch (command) {
        case 'adminDashboard':
        router.push('/admin/dashboard')
        break
        case 'adminCenter':
        router.push('/admin/center')
        break
        case 'userDashboard':
        router.push('/user/dashboard')
        break
        case 'myApplications':
        router.push('/adoption/my')
        break
        case 'editProfile':
        router.push('/user/profile')
        break
        case 'logout':
        handleLogout()
        break
    }
    }

    // 退出登录
    const handleLogout = async () => {
    try {
        await userStore.logout()
        ElMessage.success('退出成功')
        router.push('/')
    } catch (error) {
        ElMessage.error('退出失败')
    }
    }

    // 监听路由变化，更新滚动条位置
    onMounted(() => {
    // 滚动时导航栏效果
    const navbar = document.querySelector('.navbar')
    const handleScroll = () => {
        if (window.scrollY > 50) {
        navbar.style.boxShadow = '0 5px 20px rgba(0, 0, 0, 0.15)'
        } else {
        navbar.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.1)'
        }
    }
    window.addEventListener('scroll', handleScroll)
    })
</script>

<style scoped>
    .navbar {
    background-color: white;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    position: fixed;
    top: 0;
    width: 100%;
    z-index: 1000;
    transition: box-shadow 0.3s ease;
    }

    .navbar-container {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    max-width: 1200px;
    margin: 0 auto;
    }

    .logo {
    font-size: 1.5rem;
    font-weight: 700;
    color: var(--primary-color, #4e97fd);
    text-decoration: none;
    display: flex;
    align-items: center;
    cursor: pointer;
    transition: opacity 0.3s;
    }

    .logo:hover {
    opacity: 0.9;
    }

    .logo i {
    margin-right: 10px;
    }

    .nav-links {
    display: flex;
    list-style: none;
    margin: 0;
    padding: 0;
    }

    .nav-links li {
    margin: 0 15px;
    }

    .nav-links a {
    text-decoration: none;
    color: var(--dark-color, #343a40);
    font-weight: 500;
    padding: 5px 0;
    position: relative;
    transition: color 0.3s ease;
    cursor: pointer;
    }

    .nav-links a:hover {
    color: var(--primary-color, #4e97fd);
    }

    .nav-links a.active {
    color: var(--primary-color, #4e97fd);
    }

    .nav-links a.active::after {
    content: '';
    position: absolute;
    bottom: -5px;
    left: 0;
    width: 100%;
    height: 3px;
    background-color: var(--primary-color, #4e97fd);
    border-radius: 3px;
    }

    .user-area {
    display: flex;
    align-items: center;
    }

    .user-menu {
    cursor: pointer;
    }

    .user-info {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 5px 10px;
    border-radius: 25px;
    transition: background 0.3s;
    }

    .user-info:hover {
    background: #f5f5f5;
    }

    .username {
    font-size: 14px;
    font-weight: 500;
    color: #333;
    }

    .auth-buttons {
    display: flex;
    gap: 10px;
    }

    .btn-outline {
    background-color: transparent;
    color: var(--primary-color, #4e97fd);
    border: 2px solid var(--primary-color, #4e97fd);
    }

    .btn-outline:hover {
    background-color: var(--primary-color, #4e97fd);
    color: white;
    }

    /* 响应式设计 */
    @media (max-width: 768px) {
    .navbar-container {
        flex-direction: column;
        padding: 12px 15px;
    }
    
    .logo {
        margin-bottom: 10px;
    }
    
    .nav-links {
        margin: 10px 0;
        flex-wrap: wrap;
        justify-content: center;
    }
    
    .nav-links li {
        margin: 5px 12px;
    }
    
    .user-area {
        margin-top: 8px;
    }
    
    body {
        padding-top: 120px;
    }
    }

    /* 移动端适配 */
    @media (max-width: 480px) {
    .nav-links li {
        margin: 5px 8px;
    }
    
    .nav-links a {
        font-size: 14px;
    }
    
    .username {
        display: none;
    }
    
    .auth-buttons .el-button {
        padding: 6px 12px;
        font-size: 12px;
    }
    }

    /* 下拉菜单图标样式 */
    :deep(.el-dropdown-menu__item i) {
    margin-right: 8px;
    width: 16px;
    }
</style>