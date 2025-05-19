<template>
  <div class="user-home">
    <div class="main-container">
      <div class="content-left">
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
          <el-col :span="20" v-for="merchant in merchants" :key="merchant.id">
            <el-card class="merchant-card" :body-style="{ padding: '0px' }" @click="goToMerchant(merchant.shopId)">
              <div class="card-content">
                <img :src="getAvatarUrl(merchant.avatar)"
                      class="merchant-image"
                      @error="handleImageError"
                >
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

      <div class="chat-container">
        <div class="chat-header">
          <h3>AI点餐助手</h3>
        </div>
        <div class="chat-messages">
          <!-- 这里放置聊天消息 -->
        </div>
        <div class="chat-input">
          <el-input
            v-model="message"
            placeholder="输入您的问题..."
            @keyup.enter="sendMessage"
          >
            <template #append>
              <el-button @click="sendMessage">
                <el-icon><promotion /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Promotion  } from '@element-plus/icons-vue'
import { getMerchantList } from '@/api/user'

const router = useRouter()
const searchKeyword = ref('')
const merchants = ref([])
const page = ref(1)
const size = ref(8)
const total = ref(0)
const message = ref('')
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
const sendMessage = () => {
  if (message.value.trim()) {
    console.log('发送消息:', message.value)
    // 这里添加发送消息的逻辑
    message.value = ''
  }
}

onMounted(() => {
  fetchMerchants()
})
</script>

<style scoped>
.user-home {
  padding: 20px;
  height: calc(100vh - 60px);
}
.main-container {
  display: flex;
  height: 100%;
}
.content-left {
  flex: 1;
  padding-right: 20px;
  overflow-y: auto;
}
.chat-container {
  width: 400px;
  border-left: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
  height: 100%;
}
.chat-header {
  padding: 15px;
  border-bottom: 1px solid #e6e6e6;
  text-align: center;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 15px;
}

.chat-input {
  padding: 15px;
  border-top: 1px solid #e6e6e6;
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
.card-content {
  display: flex;
  align-items: center;
}

.avatar-container {
  width: 150px; /* 正方形头像区域 */
  height: 150px;
  overflow: hidden;
  flex-shrink: 0;
}

.merchant-avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-container {
  position: relative;
  width: 100%;
  padding-top: 75%; /* 4:3 宽高比 */
  overflow: hidden;
}

.merchant-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  margin-right: 20px;
  border-radius: 4px;
}

/* 悬停效果 */
.merchant-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px 0 rgba(0,0,0,.1);
}
.merchant-info {
  flex: 1;
}

.merchant-info h3 {
  margin: 0;
  font-size: 18px;
  margin-bottom: 8px;
}
.description {
  color: #666;
  font-size: 14px;
  margin-bottom: 8px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.rating {
  margin: 8px 0;
}
.price-range {
  display: flex;
  justify-content: space-between;
  color: #666;
  font-size: 14px;
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