<template>
  <div class="auth-container">
    <div class="auth-card">
      <div class="auth-header">
        <router-link to="/" class="auth-logo">
          <i class="fas fa-paw"></i>
        </router-link>
        <h1 class="auth-title">欢迎回来</h1>
        <p class="auth-subtitle">登录您的账号，继续帮助小生命</p>
      </div>

      <!-- 错误消息 -->
      <div v-if="errorMessage" class="error-message">
        <i class="fas fa-exclamation-circle"></i> {{ errorMessage }}
      </div>

      <!-- 成功消息 -->
      <div v-if="successMessage" class="success-message">
        <i class="fas fa-check-circle"></i> {{ successMessage }}
      </div>

      <!-- 登录表单 -->
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="username" class="form-label">用户名</label>
          <div class="input-with-icon">
            <i class="fas fa-user input-icon"></i>
            <input 
              type="text" 
              id="username" 
              v-model="form.username"
              class="form-control" 
              placeholder="请输入用户名" 
              required
              autofocus
            >
          </div>
        </div>

        <div class="form-group">
          <label for="password" class="form-label">密码</label>
          <div class="input-with-icon">
            <i class="fas fa-lock input-icon"></i>
            <input 
              type="password" 
              id="password" 
              v-model="form.password"
              class="form-control" 
              placeholder="请输入密码" 
              required
            >
          </div>
        </div>

        <div class="remember-forgot">
          <div class="remember-me">
            <input type="checkbox" id="remember" v-model="form.remember">
            <label for="remember">记住我</label>
          </div>
          <router-link to="/forgot-password" class="forgot-password">忘记密码？</router-link>
        </div>

        <button type="submit" class="btn btn-primary auth-btn" :disabled="loading">
          <i v-if="loading" class="fas fa-spinner fa-spin"></i>
          <i v-else class="fas fa-sign-in-alt"></i>
          {{ loading ? '登录中...' : '登录' }}
        </button>
      </form>

      <div class="auth-divider">
        <span>或使用以下方式登录</span>
      </div>

      <div class="social-login">
        <button type="button" class="social-btn wechat" @click="socialLogin('wechat')">
          <i class="fab fa-weixin"></i> 微信
        </button>
        <button type="button" class="social-btn qq" @click="socialLogin('qq')">
          <i class="fab fa-qq"></i> QQ
        </button>
      </div>

      <div class="auth-footer">
        <p>还没有账号？ <router-link to="/register" class="auth-link">立即注册</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
    import { ref, onMounted, onUnmounted } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import { useUserStore } from '@/stores/userStore'
    import { ElMessage } from 'element-plus'

    const router = useRouter()
    const route = useRoute()
    const userStore = useUserStore()

    const form = ref({
    username: '',
    password: '',
    remember: false
    })

    const loading = ref(false)
    const errorMessage = ref('')
    const successMessage = ref('')

    // 检查URL参数中的消息
    const checkUrlParams = () => {
    if (route.query.error) {
        errorMessage.value = '用户名或密码错误'
    }
    if (route.query.logout) {
        successMessage.value = '您已成功退出登录'
    }
    if (route.query.registered) {
        successMessage.value = '注册成功，请登录您的账号'
    }
    }

    // 登录处理
    const handleLogin = async () => {
    // 表单验证
    if (!form.value.username.trim()) {
        errorMessage.value = '请输入用户名'
        return
    }
    if (!form.value.password) {
        errorMessage.value = '请输入密码'
        return
    }
    if (form.value.password.length < 6) {
        errorMessage.value = '密码长度至少为6位'
        return
    }

    loading.value = true
    errorMessage.value = ''

    try {
        const result = await userStore.login({
        username: form.value.username,
        password: form.value.password,
        remember: form.value.remember
        })

        if (result.success) {
        // 登录成功，跳转到首页或之前的页面
        const redirectPath = route.query.redirect || '/'
        router.push(redirectPath)
        } else {
        errorMessage.value = result.message || '用户名或密码错误'
        }
    } catch (error) {
        errorMessage.value = '登录失败，请稍后重试'
    } finally {
        loading.value = false
    }
    }

    // 社交登录
    const socialLogin = (type) => {
    ElMessage.info(`即将通过${type === 'wechat' ? '微信' : 'QQ'}登录，此功能正在开发中`)
    }

    // 自动隐藏消息
    let messageTimer = null

    const autoHideMessage = () => {
    if (messageTimer) clearTimeout(messageTimer)
    if (errorMessage.value || successMessage.value) {
        messageTimer = setTimeout(() => {
        errorMessage.value = ''
        successMessage.value = ''
        }, 5000)
    }
    }

    onMounted(() => {
    checkUrlParams()
    autoHideMessage()
    })

    onUnmounted(() => {
    if (messageTimer) clearTimeout(messageTimer)
    })
</script>

<style scoped>
    @import '@/assets/css/login.css';
    @import '@/assets/css/style.css';
</style>