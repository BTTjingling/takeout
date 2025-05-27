<template>
  <div class="login-container">
    <div class="login-background"></div>
    <el-card class="login-card">
      <h2>登录</h2>
      <el-form :model="loginForm" :rules="rules" ref="loginFormRef">
        <el-form-item prop="username">
          <el-input v-model="loginForm.username" placeholder="用户名">
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="loginForm.password" type="password" placeholder="密码">
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="role">
          <el-radio-group v-model="loginForm.role">
            <el-radio :value="'user'">用户</el-radio>
            <el-radio :value="'merchant'">商家</el-radio>
            <el-radio :value="'admin'">管理员</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleLogin" style="width: 100%">登录</el-button>
        </el-form-item>
        <el-form-item>
          <el-link type="primary" @click="goToRegister">注册账号</el-link>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { User, Lock } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, merchantLogin, adminLogin } from '@/api/user'

const router = useRouter()
const loginFormRef = ref(null)

const loginForm = reactive({
  username: '',
  password: '',
  role: 'user'
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ],
  role: [
    { required: true, message: '请选择角色', trigger: 'change' }
  ]
}
const handleLogin = async () => {
  try {
    // 校验表单
    if (!loginFormRef.value) return
    await loginFormRef.value.validate()

    // 构造请求参数
    const loginRequestMap = {
      user: () => login({
        phone: loginForm.username,
        password: loginForm.password
      }),
      merchant: () => merchantLogin({
        phone: loginForm.username,
        password: loginForm.password
      }),
      admin: () => adminLogin({
        username: loginForm.username,
        password: loginForm.password
      })
    }

    // 执行登录请求
    const response = await loginRequestMap[loginForm.role]()
    const resData = response.data || {}

    // 处理登录失败情况
    if (resData.message === 'Invalid credentials!') {
      ElMessage.error('用户名或密码错误')
      return
    }

    // 存储用户信息（增强型）
    const userInfo = {
      username: loginForm.username,
      role: loginForm.role,
      userId: loginForm.role === 'user' ? resData.userId : undefined,
      shopId: loginForm.role === 'merchant' ? resData.shopId : undefined,
      token: resData.token // 假设返回包含token
    }
    localStorage.setItem('userInfo', JSON.stringify(userInfo))

    // 路由跳转逻辑（使用命名路由）
    const routeConfig = {
      user: {
        name: 'UserHome',
        params: { userId: resData.userId },
        query: { freshLogin: '1' } // 添加登录标记
      },
      merchant: {
        name: 'MerchantHome',
        params: { shopId: resData.shopId }
      },
      admin: {
        name: 'AdminHome'
      }
    }[loginForm.role]

    if (!routeConfig) {
      throw new Error('无效的用户角色')
    }

    // 带错误处理的跳转
    try {
      await router.push(routeConfig)
      ElMessage.success(`欢迎回来，${userInfo.username}`)
    } catch (routerError) {
      console.error('路由跳转失败:', routerError)
      ElMessage.warning('跳转失败，将返回首页')
      await router.push('/')
    }

  } catch (error) {
    // 增强错误处理
    console.error('登录流程异常:', error)

    const errorMessage = error.response?.status === 401
      ? '身份验证失败'
      : error.response?.data?.message
1      ? `服务器错误：${error.response.data.message}`
      : error.message.includes('Network Error')
      ? '网络连接失败'
      : '系统异常，请稍后重试'

    ElMessage.error(errorMessage)

    // 清除无效的登录状态
    if (error.response?.status === 401) {
      localStorage.removeItem('userInfo')
    }
  }
}


const goToRegister = () => {
  router.push('/register')
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #f5f7fa;
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-image: url('/images/1.png');
  background-size: cover; /* 让图片覆盖整个容器 */
  background-position: center; /* 图片居中显示 */
  background-repeat: no-repeat; /* 不重复显示图片 */
  opacity: 0.8; /* 设置图片透明度 */
  z-index: 0;
}

.login-card {
  width: 400px;
  padding: 20px;
  z-index: 1;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.login-card h2 {
  text-align: center;
  color: #303133;
  margin-bottom: 30px;
}

.el-form-item {
  margin-bottom: 20px;
}

.el-input {
  height: 40px;
}

.el-button {
  height: 40px;
  font-size: 16px;
}

.el-link {
  display: block;
  text-align: center;
  margin-top: 10px;
}
</style> 