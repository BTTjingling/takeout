import request from '@/utils/request'
import axios from 'axios'
// 获取上架菜品列表
export function getAvailableDishes(shopId, pageNum, pageSize) {
  return request({
    url: `/dishes/shop/${shopId}/available`,
    method: 'get',
    params: {
      pageNum,
      pageSize
    }
  })
}
// 获取商家信息
export function getMerchantInfo() {
  return axios.get('/api/merchants/1')  // 假设商家 shopId 为 1，实际应根据登录信息获取
}

// 更新商家信息
export function updateMerchantInfo(data) {
  return axios.put(`/api/merchants/${data.shopId}`, data)
}

// 修改密码
export function changePassword(data) {
  return axios.put('/api/merchants/change-password', data)
}

// 获取统计数据（如果有单独的接口）
export function getStatistics() {
  return axios.get('/api/merchants/statistics')
}

// 获取订单列表（同一模块也可放在 order.js 中，根据业务划分）
export function getOrderList(params) {
  return axios.get('/api/orders', { params })
}

// 更新订单状态
export function updateOrderStatus(data) {
  return axios.put(`/api/orders/${data.id}/status`, data)
}
