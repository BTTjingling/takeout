import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'

// 创建axios实例
const service = axios.create({
  baseURL: '/api',
  timeout: 20000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'X-Requested-With': 'XMLHttpRequest'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    console.log('请求参数:', config.params || config.data);
    console.log('Request Config:', {
      url: config.url,
      method: config.method,
      headers: config.headers,
      data: config.data
    })
    // 从localStorage获取token
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  error => {
    console.log('Request Error:', error)
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    console.log('[响应拦截] 完整响应对象:', response)

    // 统一处理所有成功响应（状态码2xx）
    if (typeof response.data === 'string') {
      // 🔨 修复点1：将字符串响应转换为标准结构
      return {
        code: 200,
        message: response.data.message,
        data: {
              ...response.data,
              message: undefined // 移除冗余字段
        }
      }
    }

    // 🔨 修复点2：确保数据结构一致性
    const res = {
      code: response.data.code || 200, // 兼容无code字段的情况
      message: response.data.message || '操作成功',
      data: response.data.data || response.data // 兼容不同结构
    }

    // 统一错误处理逻辑
    if (res.code !== 200) {
      ElMessage({
        message: `[${res.code}] ${res.message}`,
        type: 'error',
        duration: 3000
      })

      if (res.code === 401) {
        localStorage.clear()
        router.push('/login').then(() => location.reload())
      }

      return Promise.reject(new Error(res.message))
    }

    console.log('[响应拦截] 标准化响应:', res)
    return res
  },
  error => {
    // 🔨 修复点3：增强错误处理
    console.error('[响应拦截] 请求异常:', error)

    const errorInfo = {
      code: error.response?.status || 500,
      message: error.response?.data?.message
        || error.message
        || '网络连接异常'
    }

    ElMessage({
      message: `[${errorInfo.code}] ${errorInfo.message}`,
      type: 'error',
      duration: 5000
    })

    // 特殊处理网络错误
    if (!error.response) {
      errorInfo.message = '无法连接服务器，请检查网络'
    }

    return Promise.reject(errorInfo)
  }
)


export default service 