import { createRouter, createWebHistory } from 'vue-router'
import UserHome from '@/views/user/Home.vue'
// 路由配置
const routes = [
  {
    path: '/checkout',
    name: 'Checkout',
    component: () => import('@/views/user/Checkout.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
              path: '/merchantdetail/:shopId',
              name: 'UserMerchantDetail',
              component: () => import('@/views/user/MerchantDetail.vue'),
              meta: { title: '店铺详情' },
              props: true
  },
  {
    path: '/user',
    component: () => import('@/views/user/Layout.vue'),
    meta: { requiresAuth: true, userType: 1 },
    children: [
      {
        path: 'home/:userId',
        name: 'UserHome',
        component: () => import('@/views/user/Home.vue'),
        meta: { title: '用户首页' },
        props: true
      },
      {
        path: 'profile',
        name: 'UserProfile',
        component: () => import('@/views/user/Profile.vue'),
        meta: { title: '个人信息' }
      },
      {
        path: 'orders',
        name: 'UserOrders',
        component: () => import('@/views/user/Orders.vue'),
        meta: { title: '我的订单' }
      }
    ]
  },
  {
    path: '/merchant',
    component: () => import('@/views/merchant/Layout.vue'),
    meta: { requiresAuth: true, userType: 2 },
    children: [
      {
        path: 'home/:shopId',
        name: 'MerchantHome',
        component: () => import('@/views/merchant/Home.vue'),
        props: true, // 启用props接收参数
        meta: { title: '商家首页' }
      },
      {
        path: 'profile',
        name: 'MerchantProfile',
        component: () => import('@/views/merchant/Profile.vue'),
        meta: { title: '商家信息' }
      },
      {
        path: 'dishes',
        name: 'MerchantDishes',
        component: () => import('@/views/merchant/Dishes.vue'),
        meta: { title: '菜品管理' }
      },
      {
        path: 'orders',
        name: 'MerchantOrders',
        component: () => import('@/views/merchant/Orders.vue'),
        meta: { title: '订单管理' }
      }
    ]
  },
  {
    path: '/admin',
    component: () => import('@/views/admin/Layout.vue'),
    meta: { requiresAuth: true, userType: 3 },
    children: [
      {
        path: 'home',
        name: 'AdminHome',
        component: () => import('@/views/admin/Home.vue'),
        meta: { title: '管理员首页' }
      }
    ]
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 设置页面标题
  document.title = to.meta.title ? `${to.meta.title} - 外卖平台` : '外卖平台'

  // 检查是否需要登录
  if (to.matched.some(record => record.meta.requiresAuth)) {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    
    // 未登录跳转登录页
    if (!userInfo.username) {
      return next('/login')
    }

    // 角色类型映射表
    const roleMap = {
      user: {
        type: 1,
        home: 'UserHome',
        params: { userId: userInfo.userId }
      },
      merchant: {
        type: 2,
        home: 'MerchantHome',
        params: { shopId: userInfo.shopId }
      },
      admin: {
        type: 3,
        home: 'AdminHome'
      }
    }

    // 获取当前用户角色配置
    const currentRole = roleMap[userInfo.role] || roleMap.user

    // 角色类型校验
    if (to.meta.userType && to.meta.userType !== currentRole.type) {
      return next({
        name: currentRole.home,
        params: currentRole.params,
        // 保留原始查询参数
        query: { ...to.query, from: to.name }
      })
    }

    // 参数校验中间件
    const paramValidations = {
      UserHome: () => !!userInfo.userId,
      MerchantHome: () => !!userInfo.shopId
    }

    // 执行路由参数校验
    if (paramValidations[to.name]?.(to.params) === false) {
      console.error('路由参数校验失败', to)
      return next('/error/403')
    }

    next()
  } else {
    next()
  }
})

export default router 