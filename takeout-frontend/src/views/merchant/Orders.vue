<template>
  <div class="merchant-orders">
    <h2>订单管理</h2>
    <el-card>
      <div class="clearfix">
        <el-select v-model="searchForm.status" placeholder="订单状态" @change="handleStatusChange">
          <el-option label="全部" value=""></el-option>
          <el-option label="待接单" value="待接单"></el-option>
          <el-option label="制作中" value="制作中"></el-option>
          <el-option label="配送中" value="配送中"></el-option>
          <el-option label="已完成" value="已完成"></el-option>
          <el-option label="用户已取消" value="用户已取消"></el-option>
          <el-option label="商家已取消" value="商家已取消"></el-option>
        </el-select>
        <el-button type="primary" size="small" @click="handleSearch">查询</el-button>
        <el-button type="primary" size="small" @click="refreshOrderData">刷新</el-button>
      </div>
      <el-table :data="orderData" v-loading="orderLoading">
        <el-table-column prop="orderId" label="订单ID"></el-table-column>
        <el-table-column prop="userId" label="用户ID"></el-table-column>
        <el-table-column prop="shopId" label="商家ID"></el-table-column>
        <el-table-column prop="totalAmount" label="订单金额"></el-table-column>
        <el-table-column prop="ostatus" label="订单状态">
          <template #default="{row}">
            <el-tag :type="getTagType(row.ostatus)">{{ row.ostatus }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{row}">
            <el-button
                v-if="row.ostatus === '待接单'"
                size="mini"
                type="primary"
                @click="handleUpdateStatus(row, '制作中')">
              接单
            </el-button>
            <el-button
                v-if="row.ostatus === '待接单'"
                size="mini"
                type="danger"
                @click="handleUpdateStatus(row, '商家已取消')">
              取消
            </el-button>
          </template>
        </el-table-column>
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
  </div>
</template>

<script>
import { updateOrderStatus } from '@/api/merchant';
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
        status: ''
      }
    }
  },
  methods: {
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
    async fetchOrderData() {
      this.orderLoading = true
      try {
        const params = {
          pageNum: this.currentOrderPage,
          pageSize: this.pageSize,
          status: this.searchForm.status
        }
        const res = await this.$axios.get(`/api/admin/merchants/${this.shopId}/orders`, {
          params
        })
        this.orderData = res.data.records
        this.orderTotal = res.data.total
      } catch (error) {
        console.error('获取商家订单数据失败:', error)
        this.$message.error('获取商家订单数据失败')
      } finally {
        this.orderLoading = false
      }
    },
    async handleUpdateStatus(order, targetStatus) {
      try {
        // 使用后端提供的 updateOrderStatus 方法
        await updateOrderStatus(order.orderId, targetStatus)
        this.$message.success('订单状态更新成功')
        this.fetchOrderData()
      } catch (error) {
        console.error('更新订单状态失败:', error)
        this.$message.error('更新订单状态失败')
      }
    },
    handleSearch() {
      this.currentOrderPage = 1
      this.fetchOrderData()
    },
    refreshOrderData() {
      this.currentOrderPage = 1
      this.searchForm.status = ''
      this.fetchOrderData()
    },
    handleOrderPageChange(page) {
      this.currentOrderPage = page
      this.fetchOrderData()
    },
    handleStatusChange() {
      this.currentOrderPage = 1
      this.fetchOrderData()
    }
  },
  created() {
    this.fetchOrderData()
  }
}
</script>

<style scoped>
.merchant-orders {
  padding: 20px;
}

.clearfix {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.el-pagination {
  margin-top: 15px;
  text-align: right;
}
</style>