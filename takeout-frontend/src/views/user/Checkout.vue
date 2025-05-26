<template>
  <div class="checkout-page">
    <h2>{{ checkoutData.merchantName }} - 订单结算</h2>

    <!-- 收货信息展示 -->
    <el-card class="address-card">
      <template #header>
        <span>收货信息</span>
        <el-button
          link
          @click="showAddressDialog = true"
          style="float: right; padding: 3px 0"
        >
          修改收货信息
        </el-button>
      </template>
      <div v-if="selectedAddress" class="selected-address">
        <p>收货人: {{ selectedAddress.recipientName }}</p>
        <p>联系电话: {{ selectedAddress.recipientPhone }}</p>
        <p>详细地址: {{ selectedAddress.fullAddress }}</p>
      </div>
      <div v-else class="no-address">
        <el-button type="text" @click="showAddressDialog = true">
          请选择收货地址
        </el-button>
      </div>
    </el-card>

    <!-- 地址选择弹窗 -->
    <el-dialog
      v-model="showAddressDialog"
      title="选择收货地址"
      width="60%"
    >
      <div class="address-grid">
        <el-radio-group v-model="selectedAddressId" style="width: 100%">
          <div
            v-for="address in addresses"
            :key="address.addressId"
            class="address-card-wrapper"
            @click="selectedAddressId = address.addressId"
          >
            <el-card
              :class="{ 'active-card': selectedAddressId === address.addressId }"
              shadow="hover"
            >
              <div class="address-item">
                <p>收货人: {{ address.recipientName }}</p>
                <p>联系电话: {{ address.recipientPhone }}</p>
                <p>详细地址: {{ address.fullAddress }}</p>
                <p v-if="address.isDefault" class="default-tag">默认地址</p>
                <el-radio :label="address.addressId" />
              </div>
            </el-card>
          </div>
        </el-radio-group>
      </div>

      <template #footer>
        <el-button @click="showAddressDialog = false">取消</el-button>
        <el-button type="primary" @click="confirmAddress">确定</el-button>
      </template>
    </el-dialog>

    <!-- 菜品订单表格 -->
    <el-table
      :data="checkoutData.items"
      border
      height="400"
    >
      <el-table-column prop="name" label="菜品名称" />
      <el-table-column prop="quantity" label="数量" width="100" />
      <el-table-column prop="price" label="单价" width="100" />
      <el-table-column label="小计" width="120">
        <template #default="{ row }">
          ¥{{ (row.price * row.quantity).toFixed(2) }}
        </template>
      </el-table-column>
    </el-table>

    <!-- 总价汇总 -->
    <div class="checkout-footer">
        <!-- 总价汇总 -->
        <div class="price-summary">
          <div class="price-row">
            <span>商品总价：</span>
            <span>¥{{ checkoutData.subtotal.toFixed(2) }}</span>
          </div>
          <div class="price-row">
            <span>配送费：</span>
            <span>¥{{ checkoutData.deliveryFee.toFixed(2) }}</span>
          </div>
          <div class="price-row total">
            <span>合计：</span>
            <span>¥{{ checkoutData.total.toFixed(2) }}</span>
          </div>
        </div>

        <el-button
          type="primary"
          size="large"
          class="pay-button"
          @click="handlePayment"
        >
          支付订单
        </el-button>
      </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getUserAddresses } from '@/api/user'
import { createOrder } from '@/api/order'
import moment from 'moment'

const route = useRoute()
const router = useRouter()

const checkoutData = ref({
  merchantName: '',
  merchantId: null,
  shopId: null,
  items: [],
  subtotal: 0,
  deliveryFee: 0,
  total: 0
})

const addresses = ref([])
const selectedAddressId = ref(null)
const selectedAddress = ref(null)
const showAddressDialog = ref(false)

const confirmAddress = () => {
  if (selectedAddressId.value) {
    selectedAddress.value = addresses.value.find(
      addr => addr.addressId === selectedAddressId.value
    )
    showAddressDialog.value = false
  } else {
    ElMessage.warning('请选择一个收货地址')
  }
}

onMounted(async () => {
  if (history.state?.checkoutData) {
    checkoutData.value = history.state.checkoutData
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    if (userInfo.userId) {
      const res = await getUserAddresses(userInfo.userId)
      addresses.value = res.data
      const defaultAddress = addresses.value.find(addr => addr.isDefault)
      if (defaultAddress) {
        selectedAddressId.value = defaultAddress.addressId
        selectedAddress.value = defaultAddress
      }
    }
  } else {
    ElMessage.error('订单数据不存在')
    router.push('/')
  }
})

const handlePayment = async () => {
  try {
    if (!selectedAddress.value) {
      ElMessage.error('请选择收货地址')
      return
    }

    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
    const userId = userInfo.userId

    if (!userId) {
      ElMessage.error('用户未登录，请先登录')
      await router.push('/login')
      return
    }

    for (const item of checkoutData.value.items) {
      const orderData = {
        userId: parseInt(userId),
        merchantId: checkoutData.value.merchantId,
        shopId: checkoutData.value.shopId,
        dishId: item.dishId,
        dishName: item.name,
        quantity: item.quantity,
        price: item.price,
        totalAmount: item.price * item.quantity,
        deliveryFee: parseFloat(checkoutData.value.deliveryFee),
        orderTime: moment().format('YYYY-MM-DDTHH:mm:ss'),
        recipientName: selectedAddress.value.recipientName,
        recipientPhone: selectedAddress.value.recipientPhone,
        fullAddress: selectedAddress.value.fullAddress
      }

      const res = await createOrder(orderData)
      if (res.code !== 200) {
        throw new Error(res.message || `菜品${item.name}提交失败`)
      }
    }

    ElMessage.success('订单提交成功')
    router.push(`/merchantdetail/${checkoutData.value.shopId}`)
  } catch (error) {
    console.error('订单提交失败:', error)
    ElMessage.error(error.message || '订单提交失败，请重试')
  }
}
</script>

<style scoped>
.checkout-page {
  padding: 20px;
  max-width: 800px;
  margin: 0 auto;
  padding-bottom: 140px; /* 预留底部固定区域空间 */
  min-height: 100vh;
  box-sizing: border-box;
}

.address-card {
  margin-bottom: 20px;
}

.selected-address {
  margin-top: 15px;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.no-address {
  padding: 15px;
  text-align: center;
}

.address-grid {
  width: 100%;
}

.address-card-wrapper {
  width: 100%;
  margin-bottom: 15px;
}

.address-item {
  display: flex;
  flex-direction: column;
  font-size: 14px;
  line-height: 1.6;
}

.address-item p {
  margin: 4px 0;
  font-size: 14px;
}

.default-tag {
  color: #409EFF;
  font-weight: bold;
}

.active-card {
  border: 2px solid #409EFF;
  transition: border-color 0.3s;
}



.checkout-footer {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 800px;
  background: #fff;
  border-top: 1px solid #eee;
  padding: 16px 24px;
  box-shadow: 0 -2px 8px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 999;
  flex-wrap: wrap;
  box-sizing: border-box;
}

.price-summary {
  display: flex;
  flex-direction: column;
  font-size: 14px;
  line-height: 1.8;
  text-align: left;
}

.price-row {
  display: flex;
  justify-content: space-between;
  font-size: 16px;
  margin: 4px 0;
}

.price-row.total {
  font-weight: bold;
  font-size: 18px;
  color: #f56c6c;
}


.pay-button {
  margin-left: auto;
  margin-top: 10px;
  min-width: 160px;
}
</style>
