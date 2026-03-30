import request from './index'

// 用户登录
export const login = (data) => {
    return request.post('/api/user/login', data)
}

// 用户注册
export const register = (data) => {
    return request.post('/api/user/register', data)
}

// 用户登出
export const logout = () => {
    return request.get('/api/user/logout')   // ✅ 改为 /api 前缀
}

// 获取当前用户信息
export const getCurrentUser = () => {
    return request.get('/api/user/info')     // ✅ 改为 /api 前缀
}

// 用户个人中心
export const getUserDashboard = () => {
    return request.get('/api/user/dashboard') // ✅ 改为 /api 前缀（如果后端有对应接口）
}