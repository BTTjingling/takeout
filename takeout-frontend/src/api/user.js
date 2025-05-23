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

    params: {
          pageNum: params.page,
          pageSize: params.size,
          name: params.keyword // 将keyword改为name
        }
  })
}

/**
 * 根据菜品名称搜索商户
 * @param {string} keyword 搜索关键词
 * @returns {Promise} 包含商户列表的Promise
 */
export function searchMerchantsByDishName(params) {
  return request({
    url: '/dishes/searchMerchantsByDishName',
    method: 'get',
    params: {
              pageNum: params.page,
              pageSize: params.size,
              keyword: params.keyword // 将keyword改为name
            }
  });
}
// 获取菜品列表
export function getDishList(shopId) {
  return request({
    url: `/dishes/shop/${shopId}`,
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

// 新增取消订单功能
export function cancelOrder(orderId) {
  return request({
    url: `/order/cancel/${orderId}`,
    method: 'put'
  })
}
// 新增用户注册功能
export function register(data) {
  return request({
    url: '/user/register',
    method: 'post',
    data: {
      username: data.username,
      phone: data.phone,
      password: data.password,
      userType: data.userType
    }
  })
}
// 新增获取订单列表
export function getOrderList(userId, pageNum, pageSize) {
  return request({
    url: '/user/orders/list',
    method: 'get',
    params: {
      userId,
      pageNum,
      pageSize
    }
  })
}
// 获取商家详情
export const getMerchantDetail = (shopId) => {
  return request({
    url: `/merchants/${shopId}`,
    method: 'get'
  })
}

// 获取用户地址列表
export function getUserAddresses(userId) {
  return request({
    url: `/addresses/user/${userId}`,
    method: 'get',
  })
}
// 新增用户地址
export function addUserAddress(userId, addressData) {
  return request({
    url: `/addresses/user/${userId}`,
    method: 'post',
    data: addressData  // 地址数据
  })
}

// 修改用户地址
export function updateUserAddress(addressId, addressData) {
  return request({
    url: `/addresses/${addressId}`,
    method: 'put',
    data: addressData  // 更新后的地址数据
  })
}

// 删除用户地址
export function deleteUserAddress(addressId) {
  return request({
    url: `/addresses/${addressId}`,
    method: 'delete'  // 删除地址
  })
}

// 设置默认地址
export function setDefaultAddress(userId, addressId) {
  return request({
    url: '/addresses/set-default',  // 保持一致的 URL
    method: 'put',
    params: {
      userId,       // 用户ID
      addressId     // 地址ID
    }
  })
}
/**
 * 获取菜品推荐
 * @param {number} userId 用户 ID
 * @param {string} userRequest 用户请求内容
 * @returns {Promise} 包含推荐菜品列表的 Promise
 */
export function getDishRecommendations(userId, userRequest) {
  return request({
    url: '/ai/recommend-dishes',
    method: 'get',
    params: {
      userId,
      userRequest
    }
  })
}