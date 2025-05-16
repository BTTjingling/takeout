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
