import request from './index'

// 获取领养申请页面数据（用于表单回显）
export const getApplyForm = (petId) => {
    return request.get(`/adoption/apply/${petId}`)
}

// ✅ 修正：applyAdoption 应接收包含 petId 的对象，并返回 Promise
export const applyAdoption = (data) => {
    // 假设后端 API 是 POST /adoption/apply，接收 JSON 数据
    return request.post('/adoption/apply', data)
}

// 获取我的领养申请列表
export const getMyAdoptions = () => {
    return request.get('/adoption/my')
}

// 获取领养申请详情
export const getAdoptionDetail = (id) => {
    return request.get(`/adoption/detail/${id}`)
}

// REST API: 申请领养
export const applyAdoptionApi = (data) => {
    return request.post('/adoption/api/apply', data)
}

/**
 * 取消领养申请
 * @param {number} id 申请ID
 * @returns {Promise}
 */
export const cancelAdoption = (id) => {
    // 根据后端接口设计，路径参数方式更符合 RESTful 风格
    return request.post(`/adoption/cancel/${id}`)
}