<template>
  <div class="merchant-layout">
    <el-container>
      <el-aside width="200px">
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical"
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
          router
        >
          <el-menu-item index="/merchant/home">
            <i class="el-icon-s-home"></i>
            <span slot="title">首页</span>
          </el-menu-item>
          <el-menu-item index="/merchant/dishes">
            <i class="el-icon-dish"></i>
            <span slot="title">菜品管理</span>
          </el-menu-item>
          <el-menu-item index="/merchant/orders">
            <i class="el-icon-s-order"></i>
            <span slot="title">订单管理</span>
          </el-menu-item>
          <el-menu-item index="/merchant/profile">
            <i class="el-icon-user"></i>
            <span slot="title">个人信息</span>
          </el-menu-item>
        </el-menu>
      </el-aside>
      <el-container>
        <el-header>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="el-dropdown-link">
                {{ merchantInfo.name }}<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </el-header>
        <el-main>
          <router-view></router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script>
import { getMerchantInfo } from '@/api/merchant'

export default {
  name: 'MerchantLayout',
  data() {
    return {
      merchantInfo: {}
    }
  },
  created() {
    this.fetchMerchantInfo()
  },
  methods: {
    async fetchMerchantInfo() {
      try {
         const userInfo =JSON.parse(localStorage.getItem('userInfo') || '{}');
         if (userInfo.role === 'merchant' && userInfo.shopId) {
                const res = await getMerchantInfo(userInfo.shopId); // 使用 userInfo.shopId
                this.merchantInfo = res.data;
              } else {
                console.error('用户信息不完整或角色错误');
              }
      } catch (error) {
        console.error('获取商家信息失败:', error)
      }
    },
    handleCommand(command) {
      if (command === 'profile') {
        this.$router.push('/merchant/profile')
      } else if (command === 'logout') {
        localStorage.removeItem('token')
        localStorage.removeItem('userInfo')
        this.$router.push('/login')
      }
    }
  }
}
</script>

<style scoped>
.merchant-layout {
  height: 100vh;
}
.el-container {
  height: 100%;
}
.el-aside {
  background-color: #545c64;
}
.el-menu {
  border-right: none;
}
.el-header {
  background-color: #fff;
  color: #333;
  line-height: 60px;
  border-bottom: 1px solid #ddd;
}
.header-right {
  float: right;
  margin-right: 20px;
}
.el-dropdown-link {
  cursor: pointer;
  color: #409EFF;
}
</style> 