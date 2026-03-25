import request from './index'

// 获取宠物列表（支持关键词搜索）
export const getPetList = (params) => {
    return request.get('/pet/list', { params })
}

// 获取宠物详情
export const getPetDetail = (id) => {
    return request.get('/pet/detail', { params: { id } })
}

// 获取可领养宠物
export const getAvailablePets = () => {
    return request.get('/pet/available')
}

// 搜索宠物
export const searchPets = (keyword) => {
    return request.get('/pet/search', { params: { keyword } })
}

// 获取相关宠物推荐（临时模拟）
export const getRelatedPets = async (petId) => {
    // 如果后端有接口，请替换为真实请求
    // return request.get(`/pet/${petId}/related`)

    // 临时返回空数组，让页面能正常渲染
    return Promise.resolve({
        code: 200,
        data: []
    })
}