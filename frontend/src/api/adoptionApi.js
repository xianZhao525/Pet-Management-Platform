import request from './index'

// 获取领养申请页面数据（用于表单回显）
export const getApplyForm = (petId) => {
    return request.get(`/adoption/apply/${petId}`)
}

// 提交领养申请
export const applyAdoption = (data) => {
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