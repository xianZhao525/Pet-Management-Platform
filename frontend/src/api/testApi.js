import request from './index'

// 测试接口
export const testApi = () => {
    return request.get('/test')
}