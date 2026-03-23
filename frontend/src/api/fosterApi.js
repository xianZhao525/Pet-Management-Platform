import request from './index'

// 获取寄养需求列表
export const getFosterList = (params) => {
    return request.get('/foster/list', { params })
}

// 获取寄养需求详情
export const getFosterDetail = (id) => {
    return request.get(`/foster/detail/${id}`)
}

// 发布寄养需求
export const createFoster = (data) => {
    return request.post('/foster/create', data)
}

// 申请寄养
export const applyFoster = (id, data) => {
    return request.post(`/foster/apply/${id}`, data)
}

// 获取我的寄养需求
export const getMyFosters = () => {
    return request.get('/foster/my-fosters')
}

// 获取我申请的寄养
export const getMyApplications = () => {
    return request.get('/foster/my-applications')
}

// 取消申请
export const cancelApplication = (id) => {
    return request.post(`/foster/cancel-application/${id}`)
}

// 确认开始寄养
export const confirmFoster = (id) => {
    return request.post(`/foster/confirm/${id}`)
}

// 完成寄养
export const completeFoster = (id, data) => {
    return request.post(`/foster/complete/${id}`, data)
}

// 编辑寄养需求
export const updateFoster = (id, data) => {
    return request.post(`/foster/edit/${id}`, data)
}

// 删除寄养需求
export const deleteFoster = (id) => {
    return request.post(`/foster/delete/${id}`)
}