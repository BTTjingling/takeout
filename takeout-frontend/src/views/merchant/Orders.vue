<template>
  <div class="merchant-orders">
    <h2>订单管理</h2>
    <el-card>
      <div class="search-form">
        <el-row :gutter="10">
          <el-col :span="8">
            <el-input v-model="searchForm.orderId" placeholder="请输入订单ID" clearable @keyup.enter.native="handleSearch">
              <template #append>
                <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
              </template>
            </el-input>
          </el-col>
          <el-col :span="8">
            <el-input v-model="searchForm.userId" placeholder="请输入用户ID" clearable @keyup.enter.native="handleSearch">
              <template #append>
                <el-button slot="append" icon="el-icon-search" @click="handleSearch"></el-button>
              </template>
            </el-input>
          </el-col>
          <el-col :span="8">
            <el-select v-model="searchForm.status" placeholder="订单状态" clearable @change="handleStatusChange">
              <el-option label="全部" value=""></el-option>
              <el-option label="待接单" value="待接单"></el-option>
              <el-option label="制作中" value="制作中"></el-option>
              <el-option label="配送中" value="配送中"></el-option>
              <el-option label="已完成" value="已完成"></el-option>
              <el-option label="用户已取消" value="用户已取消"></el-option>
              <el-option label="商家已取消" value="商家已取消"></el-option>
            </el-select>
          </el-col>
        </el-row>
        <div class="button-group" style="margin-top: 10px; text-align: right;">
          <el-button type="primary" @click="handleSearch">
            <i class="el-icon-search"></i> 查询
          </el-button>
          <el-button @click="resetSearch">
            <i class="el-icon-refresh"></i> 重置
          </el-button>
          <el-button type="primary" @click="refreshOrderData">
            <i class="el-icon-refresh-right"></i> 刷新
          </el-button>
        </div>
      </div>

      <el-table :data="orderData" v-loading="orderLoading" stripe>
        <el-table-column prop="orderId" label="订单ID"></el-table-column>
        <el-table-column prop="userId" label="用户ID"></el-table-column>
        <el-table-column prop="shopId" label="商家ID"></el-table-column>
        <el-table-column prop="totalAmount" label="订单金额">
          <template #default="{row}">
            <span style="color: #f56c6c; font-weight: bold;">¥{{ row.totalAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ostatus" label="订单状态">
          <template #default="{row}">
            <el-tag :type="getTagType(row.ostatus)">{{ row.ostatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180">
          <template #default="{row}">
            <el-button
                v-if="row.ostatus === '待接单'"
                size="mini"
                type="primary"
                @click="handleUpdateStatus(row, '制作中')">
              <i class="el-icon-check"></i> 接单
            </el-button>
            <el-button
                v-if="row.ostatus === '待接单'"
                size="mini"
                type="danger"
                @click="handleUpdateStatus(row, '商家已取消')">
              <i class="el-icon-close"></i> 取消
            </el-button>
            <el-button
                v-if="row.ostatus === '制作中'"
                size="mini"
                type="warning"
                @click="handleUpdateStatus(row, '配送中')">
              <i class="el-icon-truck"></i> 配送
            </el-button>
            <el-button
                v-if="row.ostatus === '配送中'"
                size="mini"
                type="success"
                @click="handleUpdateStatus(row, '已完成')">
              <i class="el-icon-circle-check"></i> 完成
            </el-button>
          </template>
        </el-table-column>
        <el-table-column prop="orderTime" label="下单时间">
          <template #default="{row}">
            {{ formatDate(row.orderTime) }}
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
          background
          layout="total, sizes, prev, pager, next, jumper"
          :total="orderTotal"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pageSize"
          @size-change="handleSizeChange"
          @current-change="handleOrderPageChange">
      </el-pagination>
    </el-card>
  </div>
</template>

<script>
export default {
  name: 'MerchantOrders',
  data() {
    return {
      orderData: [],
      orderLoading: false,
      orderTotal: 0,
      currentOrderPage: 1,
      pageSize: 10,
      shopId: JSON.parse(localStorage.getItem('userInfo') || '{}').shopId || 1,
      searchForm: {
        orderId: '',
        userId: '',
        status: ''
      },
      // 保存原始搜索条件用于重置
      originalSearchForm: {}
    }
  },
  methods: {
    // 格式化日期
    formatDate(time) {
      if (!time) return '';
      const date = new Date(time);
      return date.toLocaleString();
    },

    // 获取标签类型
    getTagType(ostatus) {
      const tagTypes = {
        '待接单': 'info',
        '制作中': 'primary',
        '配送中': 'warning',
        '已完成': 'success',
        '用户已取消': 'danger',
        '商家已取消': 'danger'
      };
      return tagTypes[ostatus] || 'default';
    },

    // 获取订单数据
    async fetchOrderData() {
      this.orderLoading = true;
      try {
        // 构建查询参数
        const params = {
          pageNum: this.currentOrderPage,
          pageSize: this.pageSize,
          orderId: this.searchForm.orderId,
          userId: this.searchForm.userId,
          status: this.searchForm.status
        };

        console.log('请求参数:', params); // 调试用，可删除

        // 保持原有的API调用方式，不修改后端接口
        const res = await this.$axios.get(`/api/admin/merchants/${this.shopId}/orders`, {
          params
        });

        console.log('响应数据:', res.data); // 调试用，可删除

        this.orderData = res.data.records || [];
        this.orderTotal = res.data.total || 0;
      } catch (error) {
        console.error('获取商家订单数据失败:', error);
        this.$message.error('获取商家订单数据失败');
      } finally {
        this.orderLoading = false;
      }
    },

    // 更新订单状态
    async handleUpdateStatus(order, targetStatus) {
      try {
        // 保持原有的API调用方式，不修改后端接口
        await this.$axios.put(`/api/admin/orders/${order.orderId}/status`, {
          status: targetStatus
        });

        this.$message.success('订单状态更新成功');
        this.fetchOrderData();
      } catch (error) {
        console.error('更新订单状态失败:', error);
        this.$message.error('更新订单状态失败');
      }
    },

    // 处理搜索
    handleSearch() {
      this.currentOrderPage = 1;
      this.fetchOrderData();
    },

    // 重置搜索条件
    resetSearch() {
      // 恢复原始搜索条件
      this.searchForm = { ...this.originalSearchForm };
      this.currentOrderPage = 1;
      this.fetchOrderData();
    },

    // 刷新订单数据
    refreshOrderData() {
      // 保持当前搜索条件，只刷新数据
      this.fetchOrderData();
    },

    // 处理页码变化
    handleOrderPageChange(page) {
      this.currentOrderPage = page;
      this.fetchOrderData();
    },

    // 处理每页数量变化
    handleSizeChange(size) {
      this.pageSize = size;
      this.currentOrderPage = 1;
      this.fetchOrderData();
    },

    // 处理状态变化
    handleStatusChange() {
      this.currentOrderPage = 1;
      this.fetchOrderData();
    }
  },
  created() {
    // 保存初始搜索条件
    this.originalSearchForm = { ...this.searchForm };
    this.fetchOrderData();
  }
}
</script>

<style scoped>
.merchant-orders {
  padding: 20px;
}

.search-form {
  margin-bottom: 20px;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.el-pagination {
  margin-top: 15px;
  text-align: right;
}
</style>