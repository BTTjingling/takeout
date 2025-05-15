<template>
  <div class="admin-home">
    <h1>管理员控制台</h1>
    <p>欢迎使用外卖平台管理系统</p>

    <el-row :gutter="20" class="dashboard">
      <!-- 左侧菜单栏 -->
      <el-col :span="3" class="sidebar">
        <el-menu>
          <el-menu-item @click="activeTab = 'user'">
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item @click="activeTab = 'merchant'">
            <span>商家管理</span>
          </el-menu-item>
        </el-menu>
      </el-col>

      <!-- 右侧内容区 -->
      <el-col :span="20" class="content">
        <el-tabs v-model="activeTab">
          <!-- 用户管理 tab -->
          <el-tab-pane name="user" label="用户管理">
            <el-card>
              <div class="clearfix">
                <span>用户管理</span>
                <el-button type="primary" size="small" @click="refreshUserData">刷新</el-button>
              </div>
              <el-table :data="userData" v-loading="userLoading">
                <el-table-column prop="id" label="用户ID"></el-table-column>
                <el-table-column prop="username" label="用户名"></el-table-column>
                <el-table-column prop="phone" label="手机号"></el-table-column>
                <el-table-column prop="createTime" label="注册时间"></el-table-column>
              </el-table>
              <el-pagination
                  background
                  layout="prev, pager, next"
                  :total="userTotal"
                  :page-size="pageSize"
                  @current-change="handleUserPageChange">
              </el-pagination>
            </el-card>
          </el-tab-pane>

          <!-- 商家管理 tab -->
          <el-tab-pane name="merchant" label="商家管理">
            <el-card>
              <div class="clearfix">
                <span>商家管理</span>
                <el-button type="primary" size="small" @click="refreshMerchantData">刷新</el-button>
              </div>
              <el-table :data="merchantData" v-loading="merchantLoading">
                <el-table-column prop="id" label="商家ID"></el-table-column>
                <el-table-column prop="shopName" label="店铺名称"></el-table-column>
                <el-table-column prop="phone" label="联系电话"></el-table-column>
                <el-table-column prop="status" label="状态">
                  <template #default="{row}">
                    <el-tag :type="row.status === '正常' ? 'success' : 'danger'">
                      {{ row.status }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
              <el-pagination
                  background
                  layout="prev, pager, next"
                  :total="merchantTotal"
                  :page-size="pageSize"
                  @current-change="handleMerchantPageChange">
              </el-pagination>
            </el-card>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: 'AdminHome',
  data() {
    return {
      userData: [],
      merchantData: [],
      userLoading: false,
      merchantLoading: false,
      userTotal: 0,
      merchantTotal: 0,
      pageSize: 10,
      currentUserPage: 1,
      currentMerchantPage: 1,
      activeTab: 'user' // 默认显示用户管理
    }
  },
  methods: {
    async fetchUserData() {
      this.userLoading = true
      try {
        // 这里调用获取用户数据的API
      } catch (error) {
        console.error('获取用户数据失败:', error)
      } finally {
        this.userLoading = false
      }
    },
    async fetchMerchantData() {
      this.merchantLoading = true
      try {
        // 这里调用获取商家数据的API
      } catch (error) {
        console.error('获取商家数据失败:', error)
      } finally {
        this.merchantLoading = false
      }
    },
    refreshUserData() {
      this.currentUserPage = 1
      this.fetchUserData()
    },
    refreshMerchantData() {
      this.currentMerchantPage = 1
      this.fetchMerchantData()
    },
    handleUserPageChange(page) {
      this.currentUserPage = page
      this.fetchUserData()
    },
    handleMerchantPageChange(page) {
      this.currentMerchantPage = page
      this.fetchMerchantData()
    }
  }
}
</script>

<style scoped>
.admin-home {
  padding: 20px;
}

.dashboard {
  margin-top: 20px;
}

.clearfix {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.el-pagination {
  margin-top: 15px;
  text-align: right;
}

.sidebar {
  background-color: #f5f5f5;
  height: calc(100vh - 100px);
  position: sticky;
  top: 0;
}

.content {
  padding-left: 20px;
}
</style>