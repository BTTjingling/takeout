import request from '@/utils/request'

// 获取商家信息
export function getMerchantInfo(shopId) {
  return request({
    url: `/merchants/${shopId}`,
    method: 'get'
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

// 修改密码
export function changePassword(data) {
  return request({
    url: '/merchant/password',
    method: 'put',
    data
  })
}

// 获取菜品列表
export function getDishList(shopId,params) {
  return request({
    url: `/merchants/${shopId}/dishes`,
    method: 'get',
    params
  })
}

// 添加菜品
export function addDish(data) {
  return request({
    url: `/api/merchants/${data.shopId}/dishes`,
    method: 'post',
    data
  })
}


// 删除菜品
export function deleteDish(shopId, dishId) { // 修正参数名
  return request({
    url: `/api/merchants/${shopId}/dishes/${dishId}`, // 补全反引号
    method: 'delete'
  })
}

export function updateDish(data) {
  return request({
    url: `/api/merchants/${data.shopId}/dishes/${data.id}`,
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
