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
        <el-card class="merchant-card" :body-style="{ padding: '0px' }" @click="goToMerchant(merchant.shopId)">
            <div class="image-container">
              <img :src="getAvatarUrl(merchant.avatar)"
                    class="merchant-image"
                    @error="handleImageError"
              >
            </div>
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
              <span>起送价：¥{{ merchant.minprice }}</span>
              <span>配送费：¥{{ merchant.devfee }}</span>
            </div>
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search } from '@element-plus/icons-vue'
import { getMerchantList } from '@/api/user'

const router = useRouter()
const searchKeyword = ref('')
const merchants = ref([])
const page = ref(1)
const size = ref(8)
const total = ref(0)

const fetchMerchants = async () => {
  try {
    const res = await getMerchantList({
      page: page.value,
      size: size.value,
      keyword: searchKeyword.value
    })
    console.log('Merchants:', res.data.records) // 打印商家数据
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

const goToMerchant = (shopId) => {
  console.log('Shop ID:', shopId) // 调试信息
  router.push({ path: `/merchantdetail/${shopId}` })
}
// 添加获取头像URL的方法
const getAvatarUrl = (avatar) => {
  if (!avatar) return '/images/default-merchant.png';
  // 根据实际后端配置调整URL
  return `/images/${avatar}`;
};

// 添加图片错误处理
const handleImageError = (e) => {
  console.error('头像加载失败:', e);
  e.target.src = '/images/default-merchant.png';
};
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
.image-container {
  position: relative;
  width: 100%;
  padding-top: 75%; /* 4:3 宽高比 */
  overflow: hidden;
}
.merchant-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}
.merchant-card:hover .merchant-image {
  transform: scale(1.1);
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