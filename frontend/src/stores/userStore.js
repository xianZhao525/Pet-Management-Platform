import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, register as registerApi, logout as logoutApi, getCurrentUser } from '@/api/userApi'

export const useUserStore = defineStore('user', () => {
    // ========== 状态 ==========
    const token = ref(localStorage.getItem('token') || '')
    const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

    // ========== 计算属性 ==========
    // 是否已登录
    const isLoggedIn = computed(() => !!token.value && !!userInfo.value)

    // 是否为管理员
    const isAdmin = computed(() => userInfo.value?.role === 'ADMIN')

    // 用户名
    const username = computed(() => userInfo.value?.username || '')

    // 用户头像
    const avatar = computed(() => userInfo.value?.avatar || '/images/avatars/default-avatar.png')

    // ========== 方法 ==========
    // 登录
    const login = async (credentials) => {
        try {
            const response = await loginApi(credentials)
            if (response.code === 200) {
                const user = response.data
                // 如果需要 token，这里可以设置
                // const { token, user } = response.data
                token.value = 'dummy-token' // 根据实际 token 情况
                userInfo.value = user
                localStorage.setItem('token', 'dummy-token')
                localStorage.setItem('userInfo', JSON.stringify(user))
                return { success: true, data: response.data }
            } else {
                return { success: false, message: response.message || '登录失败' }
            }
        } catch (error) {
            return { success: false, message: error.message || '登录失败' }
        }
    }

    // 注册
    const register = async (userData) => {
        try {
            const response = await registerApi(userData)
            if (response.code === 200) {
                return { success: true, data: response.data }
            } else {
                return { success: false, message: response.message || '注册失败' }
            }
        } catch (error) {
            return { success: false, message: error.message || '注册失败' }
        }
    }

    // 退出登录
    const logout = async () => {
        try {
            await logoutApi()
        } catch (error) {
            // 忽略退出时的错误
        } finally {
            token.value = ''
            userInfo.value = null
            localStorage.removeItem('token')
            localStorage.removeItem('userInfo')
        }
    }

    // 获取当前用户信息
    const fetchUserInfo = async () => {
        if (!token.value) return

        try {
            const response = await getCurrentUser()
            if (response.code === 200) {
                userInfo.value = response.data
                localStorage.setItem('userInfo', JSON.stringify(response.data))
            }
        } catch (error) {
            console.error('获取用户信息失败', error)
        }
    }

    // 更新用户信息
    const updateUserInfo = (info) => {
        userInfo.value = { ...userInfo.value, ...info }
        localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
    }

    return {
        // 状态
        token,
        userInfo,
        // 计算属性
        isLoggedIn,
        isAdmin,
        username,
        avatar,
        // 方法
        login,
        register,
        logout,
        fetchUserInfo,
        updateUserInfo
    }
})