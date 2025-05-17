<template>
  <div class="merchant-detail">
    <div class="merchant-info">
      <img :src="merchant.image" class="merchant-image">
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getMerchantDetail, getDishList } from '@/api/user'
import { createOrder } from '@/api/order'
import { ElMessage } from 'element-plus'
import { getAvailableDishes } from '@/api/dish'
const route = useRoute()
const merchant = ref({})
const dishes = ref([])
const cart = ref({})

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
    const res = await getAvailableDishes(1, 10); // 调用新接口，默认第1页，每页10条
    dishes.value = res.data.records.map(dish => ({
      ...dish,
      quantity: 0
    }));
  } catch (error) {
    console.error('获取上架菜品失败:', error);
  }
};

const handleQuantityChange = (dish) => {
  console.log('当前菜品:', dish); // 打印当前菜品信息
  console.log('更新前购物车:', cart.value); // 打印更新前的购物车状态

  if (dish.quantity > 0) {
    cart.value[dish.dishId] = dish.quantity; // 使用 dish.dishId 作为键
  } else {
    delete cart.value[dish.dishId];
  }

  console.log('更新后购物车:', cart.value); // 打印更新后的购物车状态
  console.log('总数量:', totalQuantity.value); // 打印总数量
  console.log('总价:', totalPrice.value); // 打印总价
};

const submitOrder = async () => {
  if (totalQuantity.value === 0) {
    ElMessage.warning('请选择菜品');
    return;
  }

  console.log('开始提交订单，购物车内容:', cart.value);

  const orderItems = Object.entries(cart.value).map(([dishId, quantity]) => {
    console.log('正在处理菜品ID:', dishId, '数量:', quantity);
    const dish = dishes.value.find(d => d.dishId === parseInt(dishId));
    if (!dish) {
      console.error('未找到菜品:', dishId);
      return null;
    }
    return {
      dishId: parseInt(dishId),
      quantity,
      price: dish.price
    };
  }).filter(item => item !== null);

  console.log('生成的订单项:', orderItems);

  if (orderItems.length === 0) {
    ElMessage.error('未找到有效的菜品，请重新选择');
    return;
  }

  try {
    for (const item of orderItems) {
      const orderData = {
        userId: 1, // 这里需要替换为实际的用户ID
        shopId:  parseInt(route.params.shopId),
        dishId: item.dishId,
        quantity: item.quantity,
        totalAmount: item.price * item.quantity,
        orderTime: new Date()
      };

      console.log('提交的订单数据:', orderData);
      await createOrder(orderData); // 为每个商品单独调用一次 createOrder
    }
    // 重置购物车
        cart.value = {};
        // 重置菜品数量
        dishes.value.forEach(dish => dish.quantity = 0);
    ElMessage.success('订单提交成功');
    //router.push('/user/orders');
  } catch (error) {
    console.error('提交订单失败:', error);
  }
};


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

.merchant-image {
  width: 300px;
  height: 200px;
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
