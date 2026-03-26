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
    return request.get('/user/logout')
}

// 获取当前用户信息（从 session）
export const getCurrentUser = () => {
    return request.get('/user/info')
}

// 用户个人中心
export const getUserDashboard = () => {
    return request.get('/user/dashboard')
}