<template>
  <div class="user-orders">
    <el-card>
      <template #header>
        <span>我的订单</span>
      </template>
      <el-table :data="orders" style="width: 100%">
        <el-table-column prop="id" label="订单号" width="180" />
        <el-table-column prop="merchantName" label="商家名称" width="180" />
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">
            ¥{{ row.totalAmount.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button type="text" @click="viewOrderDetail(row)">查看详情</el-button>
            <el-button 
              v-if="row.status === 'PENDING'" 
              type="text" 
              @click="cancelOrder(row)"
            >取消订单</el-button>
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
        />
      </div>
    </el-card>

    <el-dialog
      v-model="dialogVisible"
      title="订单详情"
      width="50%"
    >
      <div v-if="currentOrder">
        <div class="order-info">
          <p>订单号：{{ currentOrder.id }}</p>
          <p>商家名称：{{ currentOrder.merchantName }}</p>
          <p>下单时间：{{ formatDate(currentOrder.createTime) }}</p>
          <p>订单状态：{{ getStatusText(currentOrder.status) }}</p>
        </div>
        <el-table :data="currentOrder.items" style="width: 100%">
          <el-table-column prop="dishName" label="菜品名称" />
          <el-table-column prop="price" label="单价">
            <template #default="{ row }">
              ¥{{ row.price.toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" />
          <el-table-column label="小计">
            <template #default="{ row }">
              ¥{{ (row.price * row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
        </el-table>
        <div class="order-total">
          <p>总计：¥{{ currentOrder.totalAmount.toFixed(2) }}</p>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getOrderList, cancelOrder } from '@/api/user'
import { ElMessage, ElMessageBox } from 'element-plus'

const orders = ref([])
const page = ref(1)
const size = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const currentOrder = ref(null)

const fetchOrders = async () => {
  try {
    const res = await getOrderList({
      page: page.value,
      size: size.value
    })
    orders.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取订单列表失败:', error)
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
    'PENDING': 'warning',
    'ACCEPTED': 'primary',
    'COMPLETED': 'success',
    'CANCELLED': 'info'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    'PENDING': '待接单',
    'ACCEPTED': '已接单',
    'COMPLETED': '已完成',
    'CANCELLED': '已取消'
  }
  return texts[status] || status
}

const formatDate = (date) => {
  return new Date(date).toLocaleString()
}

const viewOrderDetail = (order) => {
  currentOrder.value = order
  dialogVisible.value = true
}

const cancelOrder = async (order) => {
  try {
    await ElMessageBox.confirm('确认取消该订单吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await cancelOrder(order.id)
    ElMessage.success('订单已取消')
    fetchOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败:', error)
    }
  }
}

onMounted(() => {
  fetchOrders()
})
</script>

<style scoped>
.user-orders {
  padding: 20px;
}
.pagination {
  margin-top: 20px;
  text-align: right;
}
.order-info {
  margin-bottom: 20px;
}
.order-info p {
  margin: 5px 0;
}
.order-total {
  margin-top: 20px;
  text-align: right;
  font-weight: bold;
}
</style> 