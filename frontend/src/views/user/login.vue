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
    import { useUserStore } from '@/stores/user'
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
    .auth-container {
    min-height: 100vh;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 80px 20px;
    }

    .auth-card {
    background: white;
    border-radius: 20px;
    box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
    width: 100%;
    max-width: 450px;
    padding: 40px;
    animation: fadeInUp 0.6s ease;
    }

    @keyframes fadeInUp {
    from {
        opacity: 0;
        transform: translateY(30px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
    }

    .auth-header {
    text-align: center;
    margin-bottom: 30px;
    }

    .auth-logo {
    display: inline-block;
    width: 60px;
    height: 60px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
    font-size: 28px;
    color: white;
    transition: transform 0.3s;
    }

    .auth-logo:hover {
    transform: scale(1.05);
    }

    .auth-title {
    font-size: 28px;
    font-weight: 600;
    color: #333;
    margin-bottom: 8px;
    }

    .auth-subtitle {
    color: #666;
    font-size: 14px;
    }

    .error-message {
    background-color: #fee;
    color: #f56c6c;
    padding: 12px 16px;
    border-radius: 8px;
    margin-bottom: 20px;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 8px;
    border-left: 3px solid #f56c6c;
    }

    .success-message {
    background-color: #e8f5e9;
    color: #67c23a;
    padding: 12px 16px;
    border-radius: 8px;
    margin-bottom: 20px;
    font-size: 14px;
    display: flex;
    align-items: center;
    gap: 8px;
    border-left: 3px solid #67c23a;
    }

    .form-group {
    margin-bottom: 20px;
    }

    .form-label {
    display: block;
    margin-bottom: 8px;
    font-weight: 500;
    color: #333;
    }

    .input-with-icon {
    position: relative;
    }

    .input-icon {
    position: absolute;
    left: 12px;
    top: 50%;
    transform: translateY(-50%);
    color: #999;
    }

    .form-control {
    width: 100%;
    padding: 12px 12px 12px 40px;
    border: 1px solid #ddd;
    border-radius: 8px;
    font-size: 14px;
    transition: all 0.3s;
    }

    .form-control:focus {
    outline: none;
    border-color: #667eea;
    box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
    }

    .remember-forgot {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 25px;
    font-size: 14px;
    }

    .remember-me {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
    }

    .remember-me input {
    cursor: pointer;
    }

    .remember-me label {
    color: #666;
    cursor: pointer;
    }

    .forgot-password {
    color: #667eea;
    text-decoration: none;
    }

    .forgot-password:hover {
    text-decoration: underline;
    }

    .auth-btn {
    width: 100%;
    padding: 12px;
    font-size: 16px;
    font-weight: 600;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border: none;
    color: white;
    cursor: pointer;
    transition: transform 0.3s, box-shadow 0.3s;
    }

    .auth-btn:hover:not(:disabled) {
    transform: translateY(-2px);
    box-shadow: 0 5px 15px rgba(102, 126, 234, 0.4);
    }

    .auth-btn:disabled {
    opacity: 0.7;
    cursor: not-allowed;
    }

    .auth-divider {
    text-align: center;
    margin: 25px 0;
    position: relative;
    }

    .auth-divider::before,
    .auth-divider::after {
    content: '';
    position: absolute;
    top: 50%;
    width: calc(50% - 50px);
    height: 1px;
    background-color: #e0e0e0;
    }

    .auth-divider::before {
    left: 0;
    }

    .auth-divider::after {
    right: 0;
    }

    .auth-divider span {
    background-color: white;
    padding: 0 15px;
    color: #999;
    font-size: 14px;
    }

    .social-login {
    display: flex;
    gap: 15px;
    margin-bottom: 25px;
    }

    .social-btn {
    flex: 1;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 8px;
    background: white;
    cursor: pointer;
    font-size: 14px;
    font-weight: 500;
    transition: all 0.3s;
    }

    .social-btn.wechat {
    color: #09b83e;
    }

    .social-btn.wechat:hover {
    background-color: #09b83e;
    border-color: #09b83e;
    color: white;
    }

    .social-btn.qq {
    color: #12b7f5;
    }

    .social-btn.qq:hover {
    background-color: #12b7f5;
    border-color: #12b7f5;
    color: white;
    }

    .social-btn i {
    margin-right: 8px;
    }

    .auth-footer {
    text-align: center;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #f0f0f0;
    color: #666;
    font-size: 14px;
    }

    .auth-link {
    color: #667eea;
    text-decoration: none;
    font-weight: 500;
    }

    .auth-link:hover {
    text-decoration: underline;
    }

    @media (max-width: 480px) {
    .auth-card {
        padding: 30px 20px;
    }
    
    .auth-title {
        font-size: 24px;
    }
    
    .social-login {
        flex-direction: column;
    }
    }
</style>