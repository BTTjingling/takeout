import request from '@/utils/request'

// 创建订单
export function createOrder(data) {
  return request({
    url: '/user/submitOrder',
    method: 'post',
    data
  })
}

// 获取订单列表
export function getOrderList(params) {
  return request({
    url: '/order/list',
    method: 'get',
    params
  })
}

// 获取订单详情
export function getOrderDetail(orderId) {
  return request({
    url: `/order/detail/${orderId}`,
    method: 'get'
  })
}

// 取消订单
export function apiCancelOrder(orderId) {
  return request({
    // 使用模板字符串动态插入 orderId
    url: `/user/orders/${orderId}/cancel`,
    method: 'post'
  })
}