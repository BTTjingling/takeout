import request from '@/utils/request'

// 获取商家信息
export function getMerchantInfo(shopId) {
  return request({
    url: `/merchants/${shopId}`,
    method: 'get'
  })
}
//修改商家状态
export function updateMerchantStatus(shopId, status) {
  return request({
    url: '/merchants/updateStatus',
    method: 'post',
    data: {
      shopId,
      status
    }
  })
}
/**
 * 获取商家今日订单数量
 * @param {number} shopId 商家ID
 * @returns {Promise} 包含今日订单数量的Promise
 */
export function getTodayOrderCount(shopId) {
  return request({
    url: 'orders/todayCount',
    method: 'get',
    params: {
      shopId
    }
  })
}
export function getTotalDishes(shopId) {
  return request({
    url: '/merchants/dishes/total',
    method: 'get',
    params: {
      shopId
    }
  })
}
export function getTodayRevenue(shopId) {
  return request({
    url: '/merchants/revenue/today',
    method: 'get',
    params: {
      shopId
    }
  })
}
export function getPendingOrders(shopId) {
  return request({
    url: '/merchants/orders/pending-count',
    method: 'get',
    params: { shopId }
  })
}
export function getRevenueLast7Days(shopId) {
  return request({
    url: '/merchants/revenue/last7days',
    method: 'get',
    params: { shopId }
  })
}

// 修改商家信息
export function updateMerchantInfo(shopId, data) {
  return request({
    url: `/merchants/${shopId}`,
    method: 'put',
    data
  })
}
export function uploadAvatar(data) {
  return request({
    url: '/merchants/uploadAvatar',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
/**
 * 获取商家头像URL
 * @param {number} shopId 商家ID
 * @returns {Promise} 包含头像URL的Promise
 */
export function getAvatarUrl(shopId) {
  return request({
    url: `/merchants/avatar/${shopId}`,
    method: 'get'
  })
}
// 修改密码
export function changePassword(data) {
  return request({
    url: '/merchants/password',
    method: 'post',
    data
  })
}

// 获取菜品列表
export function getDishList(shopId, params) {
  return request({
    url: `/dishes/shop/${shopId}`,
    method: 'get',
    params: {
      pageNum: params.page,
      pageSize: params.size
    }
  })
}

// 添加菜品
export function addDish(data) {
  return request({
    url: '/dishes',
    method: 'post',
    data
  })
}

// 删除菜品
export function deleteDish(dishId) {
  return request({
    url: `/dishes/${dishId}`,
    method: 'delete'
  })
}
// 更新菜品
export function updateDish(data) {
  return request({
    url: `/dishes/${data.dishId}`,
    method: 'put',
    data
  })
}

// 获取订单列表
export function getOrderList(shopId,params) {
  return request({
    url: `/merchants/${shopId}/orders`,
    method: 'get',
    params
  })
}

// 更新订单状态
export function updateOrderStatus(shopId,data) {
  return request({
    url: `/api/merchants/${shopId}/orders/${data.id}/status`,
    method: 'put',
    data
  })
}

// 获取统计数据
export function getStatistics(shopId) {
  return request({
    url: `/api/merchants/${shopId}/statistics`,
    method: 'get'
  })
}

// 上传菜品图片
export function uploadDishImage(file) {
  const formData = new FormData();
  formData.append('file', file);

  return request({
    url: '/merchants/uploadImage', // 后端上传接口
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data', // 需要指定为 multipart/form-data
    }
  })
}
/**
 * 商家 AI 助手聊天
 * @param {number} merchantId 商家 ID
 * @param {string} question 商家提出的问题
 * @returns {Promise} 包含 AI 回复的 Promise
 */
export function merchantAIChat(merchantId, question) {
  return request({
    url: '/ai/merchant-chat',
    method: 'get',
    params: {
      merchantId,
      question
    }
  })
}
