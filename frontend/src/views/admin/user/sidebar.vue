<template>
  <div class="admin-sidebar">
    <div class="sidebar-header">
      <el-avatar :size="80" :src="userAvatar" class="admin-avatar" />
      <div class="admin-name">{{ username }}</div>
      <div class="admin-role">管理员</div>
    </div>

    <el-menu
      :default-active="activeMenu"
      class="sidebar-menu"
      :collapse="isCollapsed"
      :collapse-transition="false"
      router
    >
      <el-menu-item index="/admin/dashboard">
        <i class="fas fa-tachometer-alt"></i>
        <span>控制台</span>
      </el-menu-item>

      <el-menu-item index="/admin/pets">
        <i class="fas fa-paw"></i>
        <span>宠物管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/adoptions">
        <i class="fas fa-file-alt"></i>
        <span>领养申请</span>
      </el-menu-item>

      <el-menu-item index="/admin/users">
        <i class="fas fa-users"></i>
        <span>用户管理</span>
      </el-menu-item>

      <el-menu-item index="/admin/donations">
        <i class="fas fa-heart"></i>
        <span>捐赠管理</span>
      </el-menu-item>

      <el-menu-item index="/">
        <i class="fas fa-home"></i>
        <span>返回首页</span>
      </el-menu-item>

      <el-menu-item index="/logout" @click="handleLogout">
        <i class="fas fa-sign-out-alt"></i>
        <span>退出登录</span>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
  import { computed } from 'vue'
  import { useRouter, useRoute } from 'vue-router'
  import { useUserStore } from '@/stores/userStore'   // ✅ 必须导入
  import { ElMessage } from 'element-plus'
  // 删除所有 @element-plus/icons-vue 的导入

  const router = useRouter()
  const route = useRoute()
  const userStore = useUserStore()   // ✅ 现在可用

  const activeMenu = computed(() => route.path)
  const username = computed(() => userStore.userInfo?.username || '管理员')
  const userAvatar = computed(() => {
    const avatar = userStore.userInfo?.avatar
    if (!avatar) return '/images/avatars/default-avatar.png'
    if (avatar.startsWith('http')) return avatar
    return `/images/avatars/${avatar}`
  })

  const handleLogout = async () => {
    await userStore.logout()
    ElMessage.success('退出成功')
    router.push('/login')
  }

  const props = defineProps({
    isCollapsed: { type: Boolean, default: false }
  })
</script>

<style scoped>
    .admin-sidebar {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    min-height: 100vh;
    color: white;
    position: fixed;
    width: 250px;
    left: 0;
    top: 0;
    bottom: 0;
    overflow-y: auto;
    transition: width 0.3s;
    z-index: 1000;
    }

    .admin-sidebar.collapsed {
    width: 64px;
    }

    /* 滚动条样式 */
    .admin-sidebar::-webkit-scrollbar {
    width: 4px;
    }

    .admin-sidebar::-webkit-scrollbar-thumb {
    background: rgba(255, 255, 255, 0.3);
    border-radius: 4px;
    }

    .sidebar-header {
    padding: 30px 20px;
    text-align: center;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
    margin-bottom: 20px;
    }

    .admin-avatar {
    margin-bottom: 15px;
    border: 3px solid white;
    }

    .admin-name {
    font-size: 1.1rem;
    font-weight: 600;
    margin-bottom: 5px;
    color: white;
    }

    .admin-role {
    font-size: 0.85rem;
    opacity: 0.8;
    color: white;
    }

    /* 菜单样式 */
    .sidebar-menu {
    background: transparent;
    border-right: none;
    }

    :deep(.sidebar-menu .el-menu-item) {
    color: rgba(255, 255, 255, 0.8);
    height: 48px;
    line-height: 48px;
    margin: 5px 15px;
    border-radius: 8px;
    transition: all 0.3s ease;
    }

    :deep(.sidebar-menu .el-menu-item:hover) {
    background-color: rgba(255, 255, 255, 0.1);
    color: white;
    transform: translateX(5px);
    }

    :deep(.sidebar-menu .el-menu-item.is-active) {
    background-color: rgba(255, 255, 255, 0.2);
    color: white;
    border-left: 3px solid white;
    }

    :deep(.sidebar-menu .el-menu-item .el-icon) {
    margin-right: 12px;
    font-size: 18px;
    }

    :deep(.sidebar-menu .el-menu-item span) {
    font-size: 14px;
    }

    /* 折叠状态样式 */
    .admin-sidebar.collapsed :deep(.sidebar-menu .el-menu-item) {
    padding: 0 !important;
    justify-content: center;
    }

    .admin-sidebar.collapsed :deep(.sidebar-menu .el-menu-item .el-icon) {
    margin-right: 0;
    }

    .admin-sidebar.collapsed :deep(.sidebar-menu .el-menu-item span) {
    display: none;
    }

    .admin-sidebar.collapsed .sidebar-header {
    padding: 20px 10px;
    }

    .admin-sidebar.collapsed .admin-name,
    .admin-sidebar.collapsed .admin-role {
    display: none;
    }

    /* 响应式 */
    @media (max-width: 768px) {
    .admin-sidebar {
        position: static;
        width: 100%;
        min-height: auto;
    }
    
    .admin-sidebar.collapsed {
        width: 100%;
    }
    
    .sidebar-header {
        padding: 20px;
    }
    
    :deep(.sidebar-menu) {
        display: flex;
        flex-wrap: wrap;
    }
    
    :deep(.sidebar-menu .el-menu-item) {
        flex: 1;
        min-width: 100px;
        margin: 5px;
        justify-content: center;
    }
    
    :deep(.sidebar-menu .el-menu-item .el-icon) {
        margin-right: 8px;
    }
    }
</style>