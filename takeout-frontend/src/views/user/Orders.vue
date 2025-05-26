<template>
  <div class="user-orders">
    <el-card class="order-card">
      <template #header>
        <div class="card-header">
          <span class="header-title">我的订单</span>
        </div>
      </template>
      <el-table
        :data="orders"
        style="width: 100%"
        class="order-table"
        stripe
        border
        :row-style="{ height: '50px' }"
      >
        <el-table-column prop="orderId" label="订单号" width="180" />
        <el-table-column prop="merchantName" label="商家名称" width="180" />
        <el-table-column prop="dishName" label="菜品名称" width="180" />
        <el-table-column prop="quantity" label="数量" width="80" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">
            <span class="amount">¥{{ row.totalAmount.toFixed(2) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="ostatus" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.ostatus)" class="status-tag">
              {{ getStatusText(row.ostatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderTime" label="下单时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.orderTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="viewOrderDetail(row)">
              查看详情
            </el-button>
            <el-button
              v-if="row.ostatus === '待接单'"
              type="danger"
              size="small"
              @click="handleCancelOrder(row)"
            >
              取消订单
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          v-model:current-page="page"
          v-model:page-size="size"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          class="pagination-style"
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="订单详情"
      width="50%"
      :before-close="handleCloseDialog"
    >
      <div v-if="currentOrder" class="order-detail-container">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="detail-item">
              <span class="label">订单号：</span>
              <span class="value">{{ currentOrder.orderId }}</span>
            </div>
            <div class="detail-item">
              <span class="label">商家名称：</span>
              <span class="value">{{ currentOrder.merchantName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">菜品名称：</span>
              <span class="value">{{ currentOrder.dishName }}</span>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="detail-item">
              <span class="label">数量：</span>
              <span class="value">{{ currentOrder.quantity }}</span>
            </div>
            <div class="detail-item">
              <span class="label">下单时间：</span>
              <span class="value">{{ formatDate(currentOrder.orderTime) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">订单状态：</span>
              <span class="value">
                <el-tag :type="getStatusType(currentOrder.ostatus)">
                  {{ getStatusText(currentOrder.ostatus) }}
                </el-tag>
              </span>
            </div>
          </el-col>
        </el-row>
        <div class="total-amount">
          <span class="label">总金额：</span>
          <span class="value">¥{{ currentOrder.totalAmount.toFixed(2) }}</span>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderList } from '@/api/user'
import { apiCancelOrder } from '@/api/order'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const currentOrder = ref(null)

// 从 localStorage 中获取 userInfo
const userInfoStr = localStorage.getItem('userInfo')
const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null
// 从 userInfo 中获取 userId
const userId = ref(userInfo && userInfo.role === 'user' ? userInfo.userId : null)

const fetchOrders = async () => {
  if (!userId.value) {
    console.error('userId 为空，无法获取订单列表')
    ElMessage.error('用户信息缺失，请先登录')
    return
  }
  try {
    const res = await getOrderList(userId.value, page.value, size.value)
    console.log('获取到的订单数据:', res.data)
    res.data.records.forEach(order => {
      console.log('订单状态:', order.ostatus)
    })
    orders.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取订单列表失败:', error)
    ElMessage.error('获取订单列表失败')
  }
}

const handleSizeChange = (val) => {
  size.value = val
  fetchOrders()
}

const handleCurrentChange = (val) => {
  page.value = val
  fetchOrders()
}

const getStatusType = (status) => {
  const types = {
    '待接单': 'warning',
    '已接单制作中': 'info',
    '配送中': 'primary',
    '已完成': 'success',
    '用户已取消': 'danger',
    '商家已取消': 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    '待接单': '待接单',
    '已接单制作中': '已接单制作中',
    '配送中': '配送中',
    '已完成': '已完成',
    '用户已取消': '用户已取消',
    '商家已取消': '商家已取消'
  }
  return texts[status] || status
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString()
}

const viewOrderDetail = (order) => {
  console.log('当前选中订单数据:', order)
  currentOrder.value = order
  dialogVisible.value = true
}

const handleCancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    console.log('即将取消的订单 ID:', order.orderId)
    console.log('请求的 URL:', `/user/orders/${order.orderId}/cancel`)
    const res = await apiCancelOrder(order.orderId)
    if (res.code === 200) {
      ElMessage.success('订单取消成功')
      fetchOrders()
    } else {
      ElMessage.error('订单取消失败')
    }
  } catch (error) {
    if (error!== 'cancel') {
      console.error('取消订单失败:', error)
      console.error('请求配置:', error.config)
      ElMessage.error('取消订单失败')
    }
  }
}

const handleCloseDialog = () => {
  dialogVisible.value = false
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.user-orders {
  padding: 30px;
  background-color: #f5f7fa;
}

.order-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-radius: 8px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #fafafa;
  border-bottom: 1px solid #ebeef5;
  border-top-left-radius: 8px;
  border-top-right-radius: 8px;
}

.header-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.order-table {
  margin-top: 20px;
}

.amount {
  color: #f56c6c;
  font-weight: 500;
}

.status-tag {
  font-size: 12px;
  padding: 0 8px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
  padding: 15px 20px;
}

.pagination-style {
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.order-detail-container {
  padding: 20px;
}

.detail-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
}

.label {
  font-weight: bold;
  width: 100px;
  color: #606266;
}

.value {
  color: #303133;
}

.total-amount {
  margin-top: 20px;
  text-align: right;
  font-size: 18px;
  font-weight: bold;
}
</style>