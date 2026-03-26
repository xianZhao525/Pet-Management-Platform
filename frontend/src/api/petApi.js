import request from './index'

// 获取宠物列表
export const getPetList = (params) => {
    return request.get('/pet/api/list', { params })
}

// 获取宠物详情
export const getPetDetail = (id) => {
    return request.get('/pet/api/detail', { params: { id } })
}

// 获取可领养宠物
export const getAvailablePets = () => {
    return request.get('/pet/api/available')
}

// 搜索宠物
export const searchPets = (keyword) => {
    return request.get('/pet/api/search', { params: { keyword } })
}

// 获取相关宠物推荐
export const getRelatedPets = async (petId) => {
    // 暂时返回空数组，后续可扩展
    return Promise.resolve({
        code: 200,
        data: []
    })
}