<template>
  <div class="merchant-detail">
    <div class="merchant-info">
      <img :src="merchant.avatar ? '/images/' + merchant.avatar : '/images/merchant/default-merchant.png'"
           class="merchant-avatar"
           alt="商家头像"
      >
      <div class="info">
        <h2>{{ merchant.name }}</h2>
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
          <span>起送价：¥{{ merchant.minprice }}</span>
          <span>配送费：¥{{ merchant.devfee }}</span>
        </div>
      </div>
    </div>

    <div class="dishes-container">
      <h3>菜品列表</h3>
      <el-row :gutter="20">
        <el-col :span="8" v-for="dish in dishes" :key="dish.id">
          <el-card class="dish-card" :body-style="{ padding: '0px' }">
            <img :src="dish.imageUrl" class="dish-image">
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
        <div class="cart-icon" @click.stop="drawerVisible = true">
            <el-badge :value="totalQuantity" :max="99" class="item">
              <el-icon :size="24"><ShoppingCart /></el-icon>
            </el-badge>
        </div>
        <div class="total-info">
          <span>已选 {{ totalQuantity }} 件商品</span>
          <span class="total-price">合计：¥{{ totalPrice }}</span>
          <span v-if="!meetsMinPrice" class="min-price-warning">(菜品总价未满足起送价¥{{ merchant.minprice }})</span>
        </div>
        <el-button
            type="primary"
            @click.stop="submitOrder"
            :disabled="!meetsMinPrice"
        >
          提交订单
        </el-button>
      </div>
    <!-- 购物车抽屉 -->
    <el-drawer
      v-model="drawerVisible"
      title="购物车"
      direction="btt"
      size="60%"
      :modal="true"
      :show-close="true"
      :wrapperClosable="true"
      custom-class="cart-drawer"
    >
      <div class="cart-content">
        <el-table :data="cartItems" style="width: 100%">
          <el-table-column prop="name" label="菜品名称" />
          <el-table-column label="数量" width="120" align="center">
            <template #default="{row}">
              <el-input-number
                v-model="row.quantity"
                :min="1"
                :max="99"
                size="small"
                @change="updateCartItem(row)"
              />
            </template>
          </el-table-column>
          <el-table-column label="小计" width="120" align="right">
            <template #default="{row}">
              ¥{{ (row.price * row.quantity).toFixed(2) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="80">
            <template #default="{row}">
              <el-button type="danger" size="small" @click="removeFromCart(row)" circle>
                <el-icon><Delete /></el-icon>
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="price-summary">
          <div class="price-row">
            <span>商品总价：</span>
            <span>¥{{ cartSubtotal.toFixed(2) }}</span>
          </div>
          <div class="price-row">
            <span>配送费：</span>
            <span>¥{{ merchant.devfee || 0 }}</span>
          </div>
          <div class="price-row total">
            <span>合计：</span>
            <span>¥{{ (cartSubtotal + Number(merchant.devfee || 0)).toFixed(2) }}</span>
          </div>
        </div>

        <div class="checkout-btn">
          <el-button
            type="primary"
            size="large"
            @click="submitOrder"
            :disabled="!meetsMinPrice"
          >
            提交订单
          </el-button>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getMerchantDetail, getDishList } from '@/api/user'
import { createOrder } from '@/api/order'
import { ElMessage } from 'element-plus'
import { ShoppingCart, Delete } from '@element-plus/icons-vue'
import { getAvailableDishes } from '@/api/dish'
const route = useRoute()
const merchant = ref({})
const dishes = ref([])
const cart = ref({})
const drawerVisible = ref(false)
const router = useRouter()
const totalQuantity = computed(() => {
  const quantity = Object.values(cart.value).reduce((sum, quantity) => sum + quantity, 0);
  console.log('计算总数量:', quantity); // 打印计算的总数量
  return quantity;
});

const totalPrice = computed(() => {
  const dishesTotal = dishes.value.reduce((sum, dish) => {
    return sum + (Number(dish.price) * (cart.value[dish.dishId] || 0));
  }, 0);

  // 加上配送费
  const deliveryFee = Number(merchant.value.devfee) || 0;
  const total = dishesTotal + deliveryFee;

  console.log('计算总价:', total.toFixed(2));
  return total.toFixed(2);
});

// 检查是否满足起送价
const meetsMinPrice = computed(() => {
  const dishesTotal = dishes.value.reduce((sum, dish) => {
    return sum + (Number(dish.price) * (cart.value[dish.dishId] || 0));
  }, 0);
  return dishesTotal >= Number(merchant.value.minprice);
});

const fetchMerchantDetail = async () => {
  try {
    const res = await getMerchantDetail(route.params.shopId)
    merchant.value = res.data
  } catch (error) {
    console.error('获取商家详情失败:', error)
  }
}

const fetchDishes = async () => {
  try {
    const res = await getAvailableDishes(route.params.shopId, 1, 100);// 调用新接口，默认第1页，每页10条
    dishes.value = res.data.records.map(dish => ({
      ...dish,
      quantity: 0
    }));
  } catch (error) {
    console.error('获取上架菜品失败:', error);
  }
};

const handleQuantityChange = (dish) => {
  if (dish.quantity > 0) {
    cart.value[dish.dishId] = dish.quantity;
  } else {
    delete cart.value[dish.dishId];
    dish.quantity = 0; // 确保同步设置为0
  }
}
const submitOrder = async () => {
  if (totalQuantity.value === 0) {
    ElMessage.warning('请选择菜品');
    return;
  }
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
  const userId = userInfo.userId
  const orderItems = Object.entries(cart.value).map(([dishId, quantity]) => {
    const dish = dishes.value.find(d => d.dishId === parseInt(dishId));
    return dish ? {
      dishId: parseInt(dishId),
      name: dish.name,
      quantity,
      price: dish.price
    } : null;
  }).filter(Boolean);

  // 准备结算页数据
  const checkoutData = {
    merchantName: merchant.value.name,
    merchantId: merchant.value.id,
    items: orderItems,
    userId: parseInt(userId),
    shopId: route.params.shopId,
    subtotal: orderItems.reduce((sum, item) => sum + (item.price * item.quantity), 0),
    deliveryFee: Number(merchant.value.devfee) || 0,
    total: 0
  };
  checkoutData.total = checkoutData.subtotal + checkoutData.deliveryFee;

  // 跳转到结算页
  router.push({
    name: 'Checkout',
    state: { checkoutData }
  });

  // 重置购物车
  cart.value = {};
  dishes.value.forEach(dish => dish.quantity = 0);
};

const cartItems = computed(() => {
  return dishes.value
    .filter(dish => cart.value[dish.dishId] > 0)
    .map(dish => ({
      ...dish,
      quantity: cart.value[dish.dishId] || 0
    }));
});

const cartSubtotal = computed(() => {
  return cartItems.value.reduce((sum, item) => sum + (item.price * item.quantity), 0)
})

const updateCartItem = (dish) => {
  if (dish.quantity > 0) {
    cart.value[dish.dishId] = dish.quantity;
  } else {
    delete cart.value[dish.dishId];
  }
  // 同步更新菜品列表中的数量
  const targetDish = dishes.value.find(d => d.dishId === dish.dishId);
  if (targetDish) {
    targetDish.quantity = dish.quantity;
  }
}

const removeFromCart = (dish) => {
  delete cart.value[dish.dishId];
  const targetDish = dishes.value.find(d => d.dishId === dish.dishId);
  if (targetDish) {
    targetDish.quantity = 0;
  }
}

onMounted(() => {
  fetchMerchantDetail()
  fetchDishes()
})
</script>

<style scoped>
.merchant-detail {
  padding: 20px;
}

.merchant-info {
  display: flex;
  margin-bottom: 30px;
}

.merchant-avatar {
  width: 120px;
  height: 120px;
  object-fit: cover;
  margin-right: 20px;
}

.info {
  flex: 1;
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

.dishes-container {
  margin-bottom: 100px;
  padding-bottom: 80px;
}

.dish-card {
  width: 200px;
  margin-bottom: 20px;
  position: relative; /* 确保内部元素定位正确 */
  z-index: 1;
}

.dish-image {
  width: 100%;
  height: 150px;
  object-fit: cover;
}

.dish-info {
  padding: 14px;
  width: 100%;
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
  box-shadow: 0 -10px 15px rgba(0, 0, 0, .1);
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 1000;
}

.total-info {
    display: flex;
    align-items: center;
    flex-grow: 1;
}

.total-price {
  margin-left: 20px;
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}
/* 添加新的样式 */
.cart-icon {
  margin-right: 20px;
  cursor: pointer;
}

.cart-content {
   height: 100%;
   display: flex;
   flex-direction: column;
 }
.price-summary {
  margin-top: 20px;
  padding: 0 20px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  margin: 10px 0;
}
.price-label {
  min-width: 80px;
}

.price-value {
  font-weight: bold;
}

.price-row.total .price-value {
  color: #f56c6c;
  font-size: 18px;
}

.checkout-btn {
  margin-top: 20px;
  text-align: center;
}
:deep(.cart-drawer) {
  border-top-left-radius: 12px;
  border-top-right-radius: 12px;
  box-shadow: 0 -5px 15px rgba(0, 0, 0, 0.1);
}

:deep(.cart-drawer .el-drawer__header) {
  margin-bottom: 0;
  padding: 15px 20px;
  border-bottom: 1px solid #eee;
}

:deep(.cart-drawer .el-drawer__body) {
  padding: 0;
}

</style>