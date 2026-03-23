import axios from 'axios'

// 创建 axios 实例
const request = axios.create({
    baseURL: 'http://localhost:8080',  // 后端地址
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json'
    }
})

// 请求拦截器（添加 token）
request.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token')
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config
    },
    error => {
        return Promise.reject(error)
    }
)

// 响应拦截器
request.interceptors.response.use(
    response => {
        const res = response.data
        // 如果是 ApiResponse 格式
        if (res.code !== undefined) {
            if (res.code === 200) {
                return res
            } else {
                return Promise.reject(new Error(res.message || '请求失败'))
            }
        }
        return res
    },
    error => {
        console.error('Network Error:', error.message)
        return Promise.reject(error)
    }
)

export default request