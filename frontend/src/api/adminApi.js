import request from './index'

// ==================== 管理员控制台 ====================
// 获取管理员仪表盘数据
export const getAdminDashboard = () => {
    return request.get('/admin/dashboard')
}

// ==================== 宠物管理 ====================
// 获取宠物管理列表
export const getAdminPets = (params) => {
    return request.get('/admin/pets', { params })
}

// 添加宠物
export const addPet = (data) => {
    return request.post('/admin/pets/add', data)
}

// 获取编辑宠物页面数据
export const getPetForEdit = (id) => {
    return request.get(`/admin/pets/edit/${id}`)
}

// 更新宠物
export const updatePet = (data) => {
    return request.post('/admin/pets/edit', data)
}

// 删除宠物
export const deletePet = (id) => {
    return request.post(`/admin/pets/delete/${id}`)
}

// ==================== 领养申请管理 ====================
// 获取领养申请列表
export const getAdminAdoptions = (params) => {
    return request.get('/admin/adoptions', { params })
}

// 获取领养申请详情
export const getAdminAdoptionDetail = (id) => {
    return request.get(`/admin/adoptions/detail/${id}`)
}

// 批准领养申请
export const approveAdoption = (data) => {
    return request.post('/admin/adoptions/approve', data)
}

// 拒绝领养申请
export const rejectAdoption = (data) => {
    return request.post('/admin/adoptions/reject', data)
}

// 完成领养
export const completeAdoption = (id) => {
    return request.post(`/admin/adoptions/complete/${id}`)
}

// ==================== 用户管理 ====================
// 获取用户列表
export const getAdminUsers = (params) => {
    return request.get('/admin/users', { params })
}

// ==================== 捐赠管理 ====================
// 获取捐赠项目管理列表
export const getAdminDonations = (params) => {
    return request.get('/donation/manage', { params })
}

// 创建捐赠项目
export const createDonation = (data) => {
    return request.post('/donation/create', data)
}

// 编辑捐赠项目
export const updateDonation = (id, data) => {
    return request.post(`/donation/edit/${id}`, data)
}

// 更新捐赠状态
export const updateDonationStatus = (id, status) => {
    return request.post(`/donation/update-status/${id}`, null, {
        params: { status }
    })
}

// 删除捐赠项目
export const deleteDonation = (id) => {
    return request.post(`/donation/delete/${id}`)
}

// 获取捐赠统计
export const getDonationStatistics = () => {
    return request.get('/donation/statistics')
}