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

    /* ========== 颜色覆盖（与注册页面统一） ========== */

    /* 全局背景渐变 */
    .auth-container {
      background: linear-gradient(135deg, #f8f7ff 0%, #e9e4ff 100%);
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 40px 20px;
    }

    /* 卡片样式增强（半透背景、圆角阴影） */
    .auth-card {
      background: rgba(255, 255, 255, 0.95);
      backdrop-filter: blur(2px);
      border-radius: 28px;
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.08), 0 4px 12px rgba(0, 0, 0, 0.02);
      transition: transform 0.3s ease, box-shadow 0.3s ease;
    }

    .auth-card:hover {
      transform: translateY(-4px);
      box-shadow: 0 28px 48px rgba(0, 0, 0, 0.12);
    }

    /* Logo 动画 */
    .auth-logo {
      transition: transform 0.2s ease;
    }
    .auth-logo:hover {
      transform: scale(1.05);
    }

    /* 标题渐变文字 */
    .auth-title {
      background: linear-gradient(135deg, #5f4b8b, #7b5ea0);
      -webkit-background-clip: text;
      background-clip: text;
      color: transparent;
    }

    /* 输入框容器宽度占满 */
    .auth-card .form-control {
      width: 100%;
      padding: 14px 16px;
      border: 2px solid #e2e8f0;
      border-radius: 20px;
      background: white;
      transition: all 0.2s ease;
    }

    /* 输入框聚焦效果（渐变边框） */
    .auth-card .form-control:focus {
      border-color: transparent;
      outline: none;
      box-shadow: 0 0 0 3px rgba(123, 94, 160, 0.2);
      background-image: linear-gradient(white, white), linear-gradient(135deg, #667eea, #764ba2);
      background-clip: padding-box, border-box;
      background-origin: padding-box, border-box;
    }

    /* 按钮样式（渐变背景） */
    .auth-btn {
      background: linear-gradient(135deg, #667eea, #764ba2);
      border: none;
      border-radius: 40px;
      font-weight: 600;
      letter-spacing: 0.5px;
      transition: all 0.3s ease;
      box-shadow: 0 4px 10px rgba(102, 126, 234, 0.3);
    }

    .auth-btn:hover {
      transform: translateY(-2px);
      box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
    }

    /* 链接颜色 */
    .forgot-password,
    .auth-link {
      color: #7b5ea0;
    }
    .forgot-password:hover,
    .auth-link:hover {
      text-decoration: underline;
    }

    /* 社交登录按钮悬停边框颜色 */
    .social-btn:hover {
      border-color: #7b5ea0;
      background: #f5f0ff;
    }

    /* 错误消息与成功消息保留原样式，但可微调背景（可选） */
    .error-message {
      background: #fee2e2;
      border-left: 4px solid #f87171;
    }
    .success-message {
      background: #e0f2fe;
      border-left: 4px solid #38bdf8;
    }
</style>