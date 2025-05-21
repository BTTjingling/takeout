<template>
  <div class="orders-manage">
    <div class="search-bar">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="订单状态">
          <el-select v-model="searchForm.status" placeholder="请选择">
            <el-option label="全部" value=""></el-option>
            <el-option label="待接单" :value="1"></el-option>
            <el-option label="制作中" :value="2"></el-option>
            <el-option label="已完成" :value="3"></el-option>
            <el-option label="已取消" :value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <el-table :data="orders" style="width: 100%">
      <el-table-column prop="orderId" label="订单号" width="180"></el-table-column>
      <el-table-column prop="createTime" label="下单时间" width="180"></el-table-column>
      <el-table-column prop="amount" label="金额" width="100">
        <template slot-scope="scope">
          ¥{{ scope.row.amount }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row.status)">
            {{ getStatusText(scope.row.status) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200">
        <template slot-scope="scope">
          <el-button 
            size="mini" 
            type="primary" 
            @click="handleUpdateStatus(scope.row)"
            :disabled="scope.row.status === 3 || scope.row.status === 4">
            {{ getNextStatusText(scope.row.status) }}
          </el-button>
          <el-button 
            size="mini" 
            type="info" 
            @click="handleViewDetail(scope.row)">
            查看详情
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="page"
      :page-sizes="[10, 20, 50, 100]"
      :page-size="size"
      layout="total, sizes, prev, pager, next, jumper"
      :total="total">
    </el-pagination>

    <!-- 订单详情对话框 -->
    <el-dialog title="订单详情" :visible.sync="detailVisible" width="50%">
      <div v-if="currentOrder">
        <div class="order-info">
          <p><strong>订单号：</strong>{{ currentOrder.orderId }}</p>
          <p><strong>下单时间：</strong>{{ currentOrder.createTime }}</p>
          <p><strong>订单状态：</strong>
            <el-tag :type="getStatusType(currentOrder.status)">
              {{ getStatusText(currentOrder.status) }}
            </el-tag>
          </p>
          <p><strong>订单金额：</strong>¥{{ currentOrder.amount }}</p>
        </div>
        <el-table :data="currentOrder.items" style="width: 100%">
          <el-table-column prop="name" label="菜品名称"></el-table-column>
          <el-table-column prop="price" label="单价">
            <template slot-scope="scope">
              ¥{{ scope.row.price }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量"></el-table-column>
          <el-table-column label="小计">
            <template slot-scope="scope">
              ¥{{ (scope.row.price * scope.row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getOrderList, updateOrderStatus } from '@/api/merchant'

export default {
  name: 'OrdersManage',
  data() {
    return {
      searchForm: {
        status: ''
      },
      orders: [],
      page: 1,
      size: 10,
      total: 0,
      detailVisible: false,
      currentOrder: null
    }
  },
  created() {
    this.fetchOrders()
  },
  methods: {
    async fetchOrders() {
      try {
        const res = await getOrderList({
          page: this.page,
          size: this.size,
          status: this.searchForm.status
        })
        this.orders = res.data.records
        this.total = res.data.total
      } catch (error) {
        console.error('获取订单列表失败:', error)
      }
    },
    handleSearch() {
      this.page = 1
      this.fetchOrders()
    },
    resetSearch() {
      this.searchForm.status = ''
      this.handleSearch()
    },
    handleSizeChange(val) {
      this.size = val
      this.fetchOrders()
    },
    handleCurrentChange(val) {
      this.page = val
      this.fetchOrders()
    },
    getStatusType(status) {
      const types = {
        1: 'info',    // 待接单
        2: 'warning', // 制作中
        3: 'success', // 已完成
        4: 'danger'   // 已取消
      }
      return types[status] || 'info'
    },
    getStatusText(status) {
      const texts = {
        1: '待接单',
        2: '制作中',
        3: '已完成',
        4: '已取消'
      }
      return texts[status] || '未知状态'
    },
    getNextStatusText(status) {
      const texts = {
        1: '接单',
        2: '完成',
        3: '已完成',
        4: '已取消'
      }
      return texts[status] || '未知操作'
    },
    async handleUpdateStatus(order) {
      try {
        const nextStatus = order.status + 1
        await updateOrderStatus({
          id: order.orderId,
          status: nextStatus
        })
        this.$message.success('订单状态更新成功')
        this.fetchOrders()
      } catch (error) {
        console.error('更新订单状态失败:', error)
      }
    },
    handleViewDetail(order) {
      this.currentOrder = order
      this.detailVisible = true
    }
  }
}
</script>

<style scoped>
.orders-manage {
  padding: 20px;
}
.search-bar {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  text-align: right;
}
.order-info {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f5f7fa;
  border-radius: 4px;
}
.order-info p {
  margin: 10px 0;
}
</style> 