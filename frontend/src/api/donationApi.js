import request from './index'

// 获取捐赠项目列表
export const getDonationList = (params) => {
    return request.get('/donation/list', { params })
}

// 获取捐赠项目详情
export const getDonationDetail = (id) => {
    return request.get(`/donation/detail/${id}`)
}

// 获取捐赠统计数据
export const getDonationStats = () => {
    return request.get('/donation/statistics')
}

// 获取我的捐赠记录
export const getMyDonations = (params) => {
    return request.get('/donation/my-donations', { params })
}

// 获取捐赠证书
export const getDonationCertificate = (recordId) => {
    return request.get(`/donation/certificate/${recordId}`)
}

// 金钱捐赠
export const moneyDonation = (id, data) => {
    return request.post(`/donation/money/${id}`, data)
}

// 物资捐赠
export const itemsDonation = (id, data) => {
    return request.post(`/donation/items/${id}`, data)
}

// 志愿服务捐赠
export const volunteerDonation = (id, data) => {
    return request.post(`/donation/volunteer/${id}`, data)
}

// 通用捐赠提交（根据类型分发）
export const submitDonation = (data) => {
    if (data.type === 'money') {
        return moneyDonation(data.donationId, data);
    } else if (data.type === 'items') {
        return itemsDonation(data.donationId, data);
    } else if (data.type === 'volunteer') {
        return volunteerDonation(data.donationId, data);
    }
    return Promise.reject(new Error('Invalid donation type'));
};