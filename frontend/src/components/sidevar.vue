<template>
  <div class="card">
    <div class="card-header">
      <h5 class="mb-0">用户中心</h5>
    </div>
    <div class="list-group list-group-flush">
      <a 
        v-for="item in menuItems" 
        :key="item.path"
        href="#"
        class="list-group-item list-group-item-action"
        :class="{ active: isActive(item.path) }"
        @click.prevent="navigateTo(item.path)"
      >
        <i :class="item.icon"></i> {{ item.name }}
      </a>
    </div>
  </div>
</template>

<script setup>
    import { useRouter, useRoute } from 'vue-router'

    const router = useRouter()
    const route = useRoute()

    const menuItems = [
    { name: '个人主页', path: '/user/dashboard', icon: 'fas fa-home' },
    { name: '个人资料', path: '/user/profile', icon: 'fas fa-user' },
    { name: '我的领养申请', path: '/adoption/my', icon: 'fas fa-file-alt' },
    { name: '我的收藏', path: '/user/favorites', icon: 'fas fa-heart' },
    { name: '账户设置', path: '/user/settings', icon: 'fas fa-cog' }
    ]

    const isActive = (path) => {
    return route.path === path
    }

    const navigateTo = (path) => {
    router.push(path)
    }
</script>

<style scoped>
    .card {
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    overflow: hidden;
    }

    .card-header {
    background-color: #f8f9fa;
    padding: 15px 20px;
    border-bottom: 1px solid #e0e0e0;
    }

    .card-header h5 {
    margin: 0;
    font-size: 16px;
    font-weight: 600;
    }

    .list-group-item {
    display: flex;
    align-items: center;
    gap: 10px;
    padding: 12px 20px;
    color: #333;
    text-decoration: none;
    transition: all 0.3s ease;
    border: none;
    border-bottom: 1px solid #f0f0f0;
    }

    .list-group-item i {
    width: 20px;
    font-size: 14px;
    }

    .list-group-item:hover {
    background-color: #f8f9fa;
    color: #409eff;
    padding-left: 25px;
    }

    .list-group-item.active {
    background-color: #409eff;
    color: white;
    border-color: #409eff;
    }

    .list-group-item.active i {
    color: white;
    }
</style>