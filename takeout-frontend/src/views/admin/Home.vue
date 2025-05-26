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
          <!-- 新增订单管理菜单项 -->
          <el-menu-item @click="activeTab = 'order'">
            <span>订单管理</span>
          </el-menu-item>
        </el-menu>
      </el-col>

      <!-- 右侧内容区 -->
      <el-col :span="20" class="content">
        <el-tabs v-model="activeTab">
          <!-- 用户管理 tab（修改 el-table-column 的 prop） -->
          <el-tab-pane name="user" label="用户管理">
            <el-card>
              <div class="clearfix">
                <span>用户管理</span>
                <el-button type="primary" size="small" @click="refreshUserData">刷新</el-button>
              </div>
              <el-table :data="userData" v-loading="userLoading">
                <el-table-column prop="userId" label="用户ID"></el-table-column>  <!-- 匹配 Postman 的 userId -->
                <el-table-column prop="username" label="用户名"></el-table-column>  <!-- 匹配 username -->
                <el-table-column prop="phone" label="手机号"></el-table-column>  <!-- 匹配 phone -->
                <el-table-column prop="email" label="邮箱"></el-table-column>  <!-- 匹配 email -->
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
          <!-- 商家管理 tab（修改 el-table-column 的 prop） -->
          <el-tab-pane name="merchant" label="商家管理">
            <el-card>
              <div class="clearfix">
                <span>商家管理</span>
                <el-button type="primary" size="small" @click="refreshMerchantData">刷新</el-button>
              </div>
              <el-table :data="merchantData" v-loading="merchantLoading">
                <el-table-column prop="shopId" label="商家ID"></el-table-column>  <!-- 匹配 Postman 的 shopId -->
                <el-table-column prop="name" label="店铺名称"></el-table-column>  <!-- 匹配 name -->
                <el-table-column prop="phone" label="联系电话"></el-table-column>  <!-- 匹配 phone -->
                <el-table-column prop="status" label="状态">  <!-- 匹配 status -->

                  <template #default="{row}">
                    <!-- 状态值 1 表示正常，其他表示异常（根据后端定义调整） -->
                    <el-tag :type="row.status === 1 ? 'success' : 'danger'">
                      {{ row.status === 1 ? '正常' : '异常' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="address" label="地址"></el-table-column>  <!-- 匹配 address -->
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
          <!-- 订单管理标签页 -->
          <el-tab-pane name="order" label="订单管理">
            <el-card>
              <div class="clearfix">
                <span>订单管理</span>
                <el-button type="primary" size="small" @click="refreshOrderData">刷新</el-button>
              </div>
              <el-table :data="orderData" v-loading="orderLoading">
                <!-- 订单ID：后端字段是orderId -->
                <el-table-column prop="orderId" label="订单ID"></el-table-column>
                <!-- 用户ID：后端字段是userId（与前端一致，无需修改） -->
                <el-table-column prop="userId" label="用户ID"></el-table-column>
                <!-- 商家ID：后端字段是shopId（前端原prop是merchantId） -->
                <el-table-column prop="shopId" label="商家ID"></el-table-column>
                <!-- 订单金额：后端字段是totalAmount（前端原prop是totalPrice） -->
                <el-table-column prop="totalAmount" label="订单金额"></el-table-column>
                <el-table-column prop="ostatus" label="订单状态">
                  <template #default="{row}">
                    <!-- 修改2：状态值改为字符串比较（后端返回的是字符串"1"） -->
                    <!-- 修改3：显示文本与 Order.java 的 ALLOWED_STATUSES 保持一致 -->
                    <el-tag :type="getTagType(row.ostatus)">
                      {{ getStatusText(row.ostatus) }}
                    </el-tag>
                  </template>
                </el-table-column>
                <!-- 下单时间：后端字段是orderTime（前端原prop是createTime） -->
                <el-table-column prop="orderTime" label="下单时间"></el-table-column>
              </el-table>
              <el-pagination
                  background
                  layout="prev, pager, next"
                  :total="orderTotal"
                  :page-size="pageSize"
                  @current-change="handleOrderPageChange">
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
      orderData: [],
      orderLoading: false,
      orderTotal: 0,
      currentOrderPage: 1,
      userLoading: false,
      merchantLoading: false,
      userTotal: 0,
      merchantTotal: 0,
      pageSize: 10,
      currentUserPage: 1,
      currentMerchantPage: 1,
      activeTab: 'user',// 默认显示用户管理

    }
  },
  methods: {
    getStatusText(ostatus) {
      const statusList = ["未接单", "制作中", "配送中", "已完成", "用户已取消", "商家已取消"];
      return statusList[parseInt(ostatus) - 1] || "未知状态";
    },
    // 新增：标签颜色映射方法
    getTagType(ostatus) {
      switch (ostatus) {
        case "1": return "info";     // 未接单 - 蓝色
        case "2": return "primary";  // 已接单制作中 - 紫色
        case "3": return "warning";  // 配送中 - 黄色
        case "4": return "success";  // 已完成 - 绿色
        case "5": return "danger";   // 用户已取消 - 红色
        case "6": return "danger";   // 商家已取消 - 红色
        default: return "default";   // 未知状态 - 灰色
      }
    },

    async fetchUserData() {
      this.userLoading = true;
      try {
        // 调用 AdminController 的 /api/admin/users 接口（与后端路径一致）
        const res = await this.$axios.get('/api/admin/users', {
          params: {
            pageNum: this.currentUserPage,  // 与后端 @RequestParam 名称一致
            pageSize: this.pageSize
          }
        });
        this.userData = res.data.records;  // 绑定用户列表
        this.userTotal = res.data.total;    // 绑定总数量（需后端返回正确 total）
      } catch (error) {
        console.error('获取用户数据失败:', error);
        this.$message.error('获取用户数据失败');
      } finally {
        this.userLoading = false;
      }
    },
    async fetchMerchantData() {
      this.merchantLoading = true;
      try {
        // 调用 AdminController 的 /api/admin/merchants 接口（与后端路径一致）
        const res = await this.$axios.get('/api/admin/merchants', {
          params: {
            pageNum: this.currentMerchantPage,  // 与后端 @RequestParam 名称一致
            pageSize: this.pageSize
          }
        });
        this.merchantData = res.data.records;  // 绑定商家列表
        this.merchantTotal = res.data.total;    // 绑定总数量（需后端返回正确 total）
      } catch (error) {
        console.error('获取商家数据失败:', error);
        this.$message.error('获取商家数据失败');
      } finally {
        this.merchantLoading = false;
      }
    },
    async fetchOrderData() {
      console.log('当前 $axios:', this.$axios); // 调试日志：检查是否为 undefined
      this.orderLoading = true
      try {
        // 调用后端订单接口（根据你的OrderController路径调整）
        const res = await this.$axios.get('/admin/orders', {
          params: {
            page: this.currentOrderPage,
            pageSize: this.pageSize
          }
        });
        this.orderData = res.data.records;  // 绑定订单列表到表格
        this.orderTotal = res.data.total;
      } catch (error) {
        console.error('获取订单数据失败:', error)
        this.$message.error('获取订单数据失败')
      } finally {
        this.orderLoading = false
      }
    },

    // 关闭商家订单对话框时重置数据
    resetShopOrders() {
      this.shopOrdersData = [];
      this.shopOrdersTotal = 0;
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
    } ,
    refreshOrderData() {
      this.currentOrderPage = 1; // 重置为第1页
      this.fetchOrderData();      // 重新加载数据
    },
    handleOrderPageChange(page) {
      this.currentOrderPage = page; // 更新当前页码
      this.fetchOrderData();        // 加载新页码数据
    }
  },

  created() {
    this.fetchOrderData();
    this.fetchUserData();    // 新增：自动加载用户数据
    this.fetchMerchantData();// 新增：自动加载商家数据
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