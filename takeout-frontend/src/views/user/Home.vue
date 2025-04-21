<template>
  <div class="user-home">
    <div class="search-bar">
      <el-input
        v-model="searchKeyword"
        placeholder="搜索商家或菜品"
        class="search-input"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">
            <el-icon><search /></el-icon>
          </el-button>
        </template>
      </el-input>
    </div>

    <el-row :gutter="20">
      <el-col :span="6" v-for="merchant in merchants" :key="merchant.id">
        <el-card class="merchant-card" :body-style="{ padding: '0px' }">
          <img :src="merchant.image" class="merchant-image">
          <div class="merchant-info">
            <h3>{{ merchant.name }}</h3>
            <p class="description">{{ merchant.description }}</p>
            <div class="rating">
              <el-rate
                v-model="merchant.rating"
                disabled
                show-score
                text-color="#ff9900"
              />
            </div>
            <div class="price-range">
              <span>起送价：¥{{ merchant.minPrice }}</span>
              <span>配送费：¥{{ merchant.deliveryFee }}</span>
            </div>
            <el-button type="primary" @click="viewDishes(merchant)">查看菜品</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-pagination
      v-model:current-page="page"
      v-model:page-size="size"
      :page-sizes="[8, 16, 24, 32]"
      :total="total"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />

    <el-dialog
      v-model="dialogVisible"
      :title="currentMerchant.name + ' - 菜品列表'"
      width="70%"
    >
      <div class="dishes-container">
        <el-row :gutter="20">
          <el-col :span="8" v-for="dish in dishes" :key="dish.id">
            <el-card class="dish-card" :body-style="{ padding: '0px' }">
              <img :src="dish.image" class="dish-image">
              <div class="dish-info">
                <h4>{{ dish.name }}</h4>
                <p class="description">{{ dish.description }}</p>
                <div class="price-action">
                  <span class="price">¥{{ dish.price }}</span>
                  <el-input-number
                    v-model="dish.quantity"
                    :min="0"
                    :max="99"
                    size="small"
                    @change="handleQuantityChange(dish)"
                  />
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>
      <div class="cart-summary" v-if="totalQuantity > 0">
        <div class="total-info">
          <span>已选 {{ totalQuantity }} 件商品</span>
          <span class="total-price">合计：¥{{ totalPrice }}</span>
        </div>
        <el-button type="primary" @click="submitOrder">提交订单</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getMerchantList, getDishList } from '@/api/user'
import { createOrder } from '@/api/order'
import { ElMessage } from 'element-plus'

const router = useRouter()
const searchKeyword = ref('')
const merchants = ref([])
const page = ref(1)
const size = ref(8)
const total = ref(0)
const dialogVisible = ref(false)
const currentMerchant = ref({})
const dishes = ref([])
const cart = ref({})

const totalQuantity = computed(() => {
  return Object.values(cart.value).reduce((sum, quantity) => sum + quantity, 0)
})

const totalPrice = computed(() => {
  return dishes.value.reduce((sum, dish) => {
    return sum + (dish.price * (cart.value[dish.id] || 0))
  }, 0).toFixed(2)
})

const fetchMerchants = async () => {
  try {
    const res = await getMerchantList({
      page: page.value,
      size: size.value,
      keyword: searchKeyword.value
    })
    merchants.value = res.data.records
    total.value = res.data.total
  } catch (error) {
    console.error('获取商家列表失败:', error)
  }
}

const handleSearch = () => {
  page.value = 1
  fetchMerchants()
}

const handleSizeChange = (val) => {
  size.value = val
  fetchMerchants()
}

const handleCurrentChange = (val) => {
  page.value = val
  fetchMerchants()
}

const viewDishes = async (merchant) => {
  currentMerchant.value = merchant
  dialogVisible.value = true
  cart.value = {}
  try {
    const res = await getDishList({
      merchantId: merchant.id
    })
    dishes.value = res.data.map(dish => ({
      ...dish,
      quantity: 0
    }))
  } catch (error) {
    console.error('获取菜品列表失败:', error)
  }
}

const handleQuantityChange = (dish) => {
  if (dish.quantity > 0) {
    cart.value[dish.id] = dish.quantity
  } else {
    delete cart.value[dish.id]
  }
}

const submitOrder = async () => {
  if (totalQuantity.value === 0) {
    ElMessage.warning('请选择菜品')
    return
  }

  const orderItems = Object.entries(cart.value).map(([dishId, quantity]) => {
    const dish = dishes.value.find(d => d.id === parseInt(dishId))
    return {
      dishId: parseInt(dishId),
      quantity,
      price: dish.price
    }
  })

  try {
    await createOrder({
      merchantId: currentMerchant.value.id,
      items: orderItems
    })
    ElMessage.success('订单提交成功')
    dialogVisible.value = false
    router.push('/user/orders')
  } catch (error) {
    console.error('提交订单失败:', error)
  }
}

onMounted(() => {
  fetchMerchants()
})
</script>

<style scoped>
.user-home {
  padding: 20px;
}
.search-bar {
  margin-bottom: 20px;
}
.search-input {
  width: 500px;
}
.merchant-card {
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s;
}
.merchant-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 2px 12px 0 rgba(0,0,0,.1);
}
.merchant-image {
  width: 100%;
  height: 200px;
  object-fit: cover;
}
.merchant-info {
  padding: 14px;
}
.merchant-info h3 {
  margin: 0;
  font-size: 18px;
}
.description {
  color: #666;
  font-size: 14px;
  margin: 10px 0;
}
.rating {
  margin: 10px 0;
}
.price-range {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 14px;
  margin: 10px 0;
}
.el-pagination {
  margin-top: 20px;
  text-align: right;
}
.dishes-container {
  max-height: 60vh;
  overflow-y: auto;
}
.dish-card {
  margin-bottom: 20px;
}
.dish-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}
.dish-info {
  padding: 14px;
}
.dish-info h4 {
  margin: 0;
  font-size: 16px;
}
.price-action {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 10px;
}
.price {
  color: #f56c6c;
  font-size: 18px;
  font-weight: bold;
}
.cart-summary {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background-color: #fff;
  padding: 15px 20px;
  box-shadow: 0 -2px 10px rgba(0,0,0,.1);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.total-info {
  display: flex;
  align-items: center;
}
.total-price {
  margin-left: 20px;
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}
</style> 