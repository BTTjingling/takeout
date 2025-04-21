import request from '@/utils/request'

// 用户登录
export function login(data) {
  return request({
    url: '/login/user',
    method: 'post',
    data: {
      phone: data.phone,
      password: data.password
    }
  })
}

// 商家登录
export function merchantLogin(data) {
  return request({
    url: '/login/merchant',
    method: 'post',
    data: {
      phone: data.phone,
      password: data.password
    }
  })
}

// 管理员登录
export function adminLogin(data) {
  return request({
    url: '/login/admin',
    method: 'post',
    data: {
      username: data.username,
      password: data.password
    }
  })
}

// 获取用户信息
export function getUserInfo(userId) {
  return request({
    url: '/user/info',
    method: 'get',
    params: {
      userId
    }
  })
}

// 修改用户信息
export function updateUserInfo(data) {
  return request({
    url: '/user/update',
    method: 'put',
    data
  })
}

// 修改密码
export function changePassword(data) {
  return request({
    url: '/user/password',
    method: 'put',
    data
  })
}

// 获取商家列表
export function getMerchantList(params) {
  return request({
    url: '/merchants/list',
    method: 'get',
    params
  })
}

// 获取菜品列表
export function getDishList(merchantId) {
  return request({
    url: `/dish/list/${merchantId}`,
    method: 'get'
  })
}

// 创建订单
export function createOrder(data) {
  return request({
    url: '/order/create',
    method: 'post',
    data
  })
} 