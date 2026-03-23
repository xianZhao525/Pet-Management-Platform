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