<template>
  <div class="user-layout">
    <el-container>
      <el-header>
        <div class="header-left">
          <h2>外卖系统</h2>
        </div>
        <div class="header-right">
          <el-menu mode="horizontal" :default-active="$route.path" router>
            <el-menu-item index="/user/home">首页</el-menu-item>
            <el-menu-item index="/user/orders">我的订单</el-menu-item>
            <el-menu-item index="/user/profile">个人信息</el-menu-item>
          </el-menu>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ userInfo.username }}<el-icon class="el-icon--right"><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      <el-main>
        <router-view></router-view>
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowDown } from '@element-plus/icons-vue'
import { getUserInfo } from '@/api/user'

const router = useRouter()
const userInfo = ref({})

const fetchUserInfo = async () => {
  try {
    const storedUserInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    const userId = storedUserInfo.userId;
    if (userId) {
      const res = await getUserInfo(userId);
      userInfo.value = res.data;
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/user/profile');
  } else if (command === 'logout') {
    localStorage.removeItem('token');
    localStorage.removeItem('userInfo');
    router.push('/login');
  }
}

onMounted(() => {
  fetchUserInfo();
})
</script>

<style scoped>
.user-layout {
  height: 100vh;
}
.el-container {
  height: 100%;
}
.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.header-left h2 {
  margin: 0;
  color: #409EFF;
}
.header-right {
  display: flex;
  align-items: center;
}
.el-menu {
  border-bottom: none;
}
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
  margin-left: 20px;
  display: flex;
  align-items: center;
}
</style>