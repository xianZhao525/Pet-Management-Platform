<template>
  <div class="register-container">
    <div class="register-card">
      <div class="register-header">
        <router-link to="/" class="register-logo">
          <i class="fas fa-paw"></i>
        </router-link>
        <h1 class="register-title">创建新账号</h1>
        <p class="register-subtitle">加入我们，为流浪宠物提供一个温暖的家</p>
      </div>

      <!-- 错误消息 -->
      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        :closable="true"
        show-icon
        @close="errorMessage = ''"
        class="mb-3"
      />

      <!-- 注册表单 -->
      <el-form
        ref="registerFormRef"
        :model="registerForm"
        :rules="rules"
        label-width="0"
        @submit.prevent="handleRegister"
      >
        <!-- 用户名 -->
        <el-form-item prop="username">
          <div class="input-with-icon">
            <i class="fas fa-user-circle input-icon"></i>
            <el-input
              v-model="registerForm.username"
              placeholder="请输入用户名"
              size="large"
              @blur="validateField('username')"
            />
          </div>
        </el-form-item>

        <!-- 手机号 -->
        <el-form-item prop="phone">
          <div class="input-with-icon">
            <i class="fas fa-phone input-icon"></i>
            <el-input
              v-model="registerForm.phone"
              placeholder="请输入手机号码"
              size="large"
              @blur="validateField('phone')"
            />
          </div>
        </el-form-item>

        <!-- 邮箱 -->
        <el-form-item prop="email">
          <div class="input-with-icon">
            <i class="fas fa-envelope input-icon"></i>
            <el-input
              v-model="registerForm.email"
              placeholder="请输入电子邮箱"
              size="large"
              @blur="validateField('email')"
            />
          </div>
        </el-form-item>

        <!-- 密码 -->
        <el-form-item prop="password">
          <div class="input-with-icon">
            <i class="fas fa-lock input-icon"></i>
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="请输入密码（至少6位）"
              size="large"
              show-password
              @input="checkPasswordStrength"
              @blur="validateField('password')"
            />
          </div>

          <!-- 密码强度指示器 -->
          <div class="password-strength">
            <div class="password-strength-bar" :style="{ width: strengthPercent + '%', backgroundColor: strengthColor }"></div>
          </div>
          <div class="password-strength-text" :style="{ color: strengthColor }">
            密码强度：{{ strengthText }}
          </div>

          <!-- 密码要求 -->
          <div class="password-requirements">
            <div :class="['requirement', passwordRules.lengthReq ? 'valid' : 'invalid']">
              <i :class="passwordRules.lengthReq ? 'fas fa-check-circle' : 'fas fa-times-circle'"></i>
              至少6个字符
            </div>
            <div :class="['requirement', passwordRules.uppercaseReq ? 'valid' : 'invalid']">
              <i :class="passwordRules.uppercaseReq ? 'fas fa-check-circle' : 'fas fa-times-circle'"></i>
              至少一个大写字母
            </div>
            <div :class="['requirement', passwordRules.lowercaseReq ? 'valid' : 'invalid']">
              <i :class="passwordRules.lowercaseReq ? 'fas fa-check-circle' : 'fas fa-times-circle'"></i>
              至少一个小写字母
            </div>
            <div :class="['requirement', passwordRules.numberReq ? 'valid' : 'invalid']">
              <i :class="passwordRules.numberReq ? 'fas fa-check-circle' : 'fas fa-times-circle'"></i>
              至少一个数字
            </div>
          </div>
        </el-form-item>

        <!-- 确认密码 -->
        <el-form-item prop="confirmPassword">
          <div class="input-with-icon">
            <i class="fas fa-lock input-icon"></i>
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="请再次输入密码"
              size="large"
              show-password
              @input="checkPasswordMatch"
              @blur="validateField('confirmPassword')"
            />
          </div>
          <div v-if="passwordMatchText" class="password-match-text" :style="{ color: passwordMatchColor }">
            {{ passwordMatchText }}
          </div>
        </el-form-item>

        <!-- 角色选择 -->
        <el-form-item>
          <div class="role-selection">
            <label class="role-option">
              <el-radio v-model="registerForm.role" value="USER" size="large">
                <i class="fas fa-user"></i> 普通用户
              </el-radio>
            </label>
            <label class="role-option">
              <el-radio v-model="registerForm.role" value="ADMIN" size="large">
                <i class="fas fa-user-shield"></i> 管理员
              </el-radio>
            </label>
          </div>
        </el-form-item>

        <!-- 管理员注册码 -->
        <el-form-item v-if="registerForm.role === 'ADMIN'" prop="adminCode">
          <div class="input-with-icon">
            <i class="fas fa-key input-icon"></i>
            <el-input
              v-model="registerForm.adminCode"
              type="password"
              placeholder="请输入管理员注册码"
              size="large"
              @blur="validateField('adminCode')"
            />
          </div>
          <div class="form-text text-muted">只有管理员才需要填写此项</div>
        </el-form-item>

        <!-- 服务条款 -->
        <el-form-item prop="agreeTerms">
          <div class="terms-agreement">
            <el-checkbox v-model="registerForm.agreeTerms" @change="validateField('agreeTerms')">
              我已阅读并同意
              <router-link to="/terms" class="terms-link">服务条款</router-link> 和
              <router-link to="/privacy" class="terms-link">隐私政策</router-link>
            </el-checkbox>
          </div>
        </el-form-item>

        <el-button type="primary" size="large" native-type="submit" :loading="loading" class="register-btn">
          <i class="fas fa-user-plus"></i> 注册账号
        </el-button>
      </el-form>

      <div class="register-footer">
        <p>已有账号？ <router-link to="/login" class="register-link">立即登录</router-link></p>
      </div>
    </div>
  </div>
</template>

<script setup>
    import { ref, reactive, computed } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { register } from '@/api/userApi'

    const router = useRouter()
    const registerFormRef = ref(null)
    const loading = ref(false)
    const errorMessage = ref('')
    const passwordMatchText = ref('')

    // 表单数据
    const registerForm = reactive({
    username: '',
    phone: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: 'USER',
    adminCode: '',
    agreeTerms: false
    })

    // 密码强度规则
    const passwordRules = reactive({
    lengthReq: false,
    uppercaseReq: false,
    lowercaseReq: false,
    numberReq: false
    })

    // 密码强度计算
    const strengthPercent = computed(() => {
    let count = 0
    if (passwordRules.lengthReq) count++
    if (passwordRules.uppercaseReq) count++
    if (passwordRules.lowercaseReq) count++
    if (passwordRules.numberReq) count++
    return (count / 4) * 100
    })

    const strengthColor = computed(() => {
    const count = [passwordRules.lengthReq, passwordRules.uppercaseReq, passwordRules.lowercaseReq, passwordRules.numberReq].filter(Boolean).length
    switch(count) {
        case 0: return '#ccc'
        case 1: return '#ff5252'
        case 2: return '#ffb74d'
        case 3: return '#4caf50'
        case 4: return '#2e7d32'
        default: return '#ccc'
    }
    })

    const strengthText = computed(() => {
    const count = [passwordRules.lengthReq, passwordRules.uppercaseReq, passwordRules.lowercaseReq, passwordRules.numberReq].filter(Boolean).length
    switch(count) {
        case 0: return '未输入'
        case 1: return '弱'
        case 2: return '一般'
        case 3: return '强'
        case 4: return '非常强'
        default: return '未输入'
    }
    })

    const passwordMatchColor = computed(() => {
    return passwordMatchText.value === '密码匹配 ✓' ? '#2e7d32' : '#d32f2f'
    })

    // 验证规则
    const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度需在3-20位之间', trigger: 'blur' }
    ],
    phone: [
        { required: true, message: '请输入手机号码', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确，请输入11位手机号码', trigger: 'blur' }
    ],
    email: [
        { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, message: '密码长度至少为6位', trigger: 'blur' },
        {
        validator: (rule, value, callback) => {
            if (!passwordRules.uppercaseReq) {
            callback(new Error('密码至少包含一个大写字母'))
            } else if (!passwordRules.lowercaseReq) {
            callback(new Error('密码至少包含一个小写字母'))
            } else if (!passwordRules.numberReq) {
            callback(new Error('密码至少包含一个数字'))
            } else {
            callback()
            }
        },
        trigger: 'blur'
        }
    ],
    confirmPassword: [
        { required: true, message: '请确认密码', trigger: 'blur' },
        {
        validator: (rule, value, callback) => {
            if (value !== registerForm.password) {
            callback(new Error('两次输入的密码不一致'))
            } else {
            callback()
            }
        },
        trigger: 'blur'
        }
    ],
    adminCode: [
        {
        required: true,
        message: '管理员注册码不能为空',
        trigger: 'blur',
        validator: (rule, value, callback) => {
            if (registerForm.role === 'ADMIN' && !value) {
            callback(new Error('管理员注册码不能为空'))
            } else {
            callback()
            }
        }
        }
    ],
    agreeTerms: [
        {
        validator: (rule, value, callback) => {
            if (!value) {
            callback(new Error('请同意服务条款和隐私政策'))
            } else {
            callback()
            }
        },
        trigger: 'change'
        }
    ]
    }

    // 检查密码强度
    const checkPasswordStrength = (password) => {
    passwordRules.lengthReq = password.length >= 6
    passwordRules.uppercaseReq = /[A-Z]/.test(password)
    passwordRules.lowercaseReq = /[a-z]/.test(password)
    passwordRules.numberReq = /[0-9]/.test(password)
    
    if (registerForm.confirmPassword) {
        checkPasswordMatch()
    }
    }

    // 检查密码匹配
    const checkPasswordMatch = () => {
    if (!registerForm.confirmPassword) {
        passwordMatchText.value = ''
    } else if (registerForm.password === registerForm.confirmPassword) {
        passwordMatchText.value = '密码匹配 ✓'
    } else {
        passwordMatchText.value = '密码不匹配 ✗'
    }
    }

    // 验证单个字段
    const validateField = (field) => {
    registerFormRef.value?.validateField(field)
    }

    // 注册提交
    const handleRegister = async () => {
    if (!registerFormRef.value) return

    await registerFormRef.value.validate(async (valid) => {
        if (!valid) {
        ElMessage.warning('请修正表单中的错误后再提交')
        return
        }

        loading.value = true
        try {
        const response = await register({
            username: registerForm.username,
            phone: registerForm.phone,
            email: registerForm.email,
            password: registerForm.password,
            role: registerForm.role,
            adminCode: registerForm.adminCode
        })

        if (response.code === 200) {
            ElMessage.success('注册成功，请登录')
            router.push('/login')
        } else {
            errorMessage.value = response.message || '注册失败'
        }
        } catch (error) {
        errorMessage.value = '注册失败，请稍后重试'
        } finally {
        loading.value = false
        }
    })
    }
</script>

<style scoped>
    @import '@/assets/css/register.css';
    @import '@/assets/css/style.css';
</style>