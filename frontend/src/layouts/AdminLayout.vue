<template>
  <div class="main-layout">
    
    <!-- 公共侧边栏（如果有） -->
    <AppSidebar />
    
    <!-- 主内容区 - 这里会显示不同的页面内容 -->
    <main class="main-content">
      <router-view />
    </main>
    
    <!-- 公共页脚 -->
    <AppFooter />
  </div>
</template>

<script setup>
    import AppSidebar from '@/views/admin/user/sidebar.vue'
    import AppFooter from '@/components/common/AppFooter.vue'
</script>


<style scoped>
.main-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.main-content {
  flex: 1;
  margin-top: 60px; /* 导航栏高度 */
  margin-left: 220px; /* 侧边栏宽度 */
  padding: 20px;
}

/* 侧边栏收起时的样式 */
.main-content.sidebar-collapsed {
  margin-left: 64px;
}
</style>