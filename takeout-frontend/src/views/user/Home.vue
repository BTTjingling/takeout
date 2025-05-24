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
              clearable
          >
            <template #append>
              <el-button @click="handleSearch">
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>

        <el-row :gutter="20">
          <el-col :span="24" v-for="merchant in merchants" :key="merchant.id">
            <el-card
                class="merchant-card"
                :body-style="{ padding: '0px' }"
                @click="goToMerchant(merchant.shopId)"
            >
              <div class="card-content">
                <img
                    :src="getAvatarUrl(merchant.avatar)"
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

      <div class="chat-container" ref="chatContainer" v-show="!isChatMinimized">
        <div class="chat-header">
          <h3>AI点餐助手</h3>
          <span class="minimize-btn" @click="isChatMinimized = true">×</span>
        </div>

        <div class="chat-messages" ref="chatMessages">
          <div
              class="message"
              v-for="(msg, index) in chatMessagesArr"
              :key="index"
          >
            <!-- AI消息 -->
            <div v-if="msg.from === 'ai'" class="message-left">
              <div class="message-avatar">
                <div class="circular-avatar ai-avatar">
                  <span class="avatar-text">AI</span>
                </div>
              </div>
              <div class="message-bubble">
                <div class="message-content" v-if="typeof msg.content === 'string'">{{ msg.content }}</div>
                <div class="recommended-dishes" v-else>
                  <h4>推荐菜品</h4>
                  <ul>
                    <li v-for="(dish, dishIndex) in msg.content" :key="dishIndex">
                      {{ dish.name }} - ¥{{ dish.price }} - {{ dish.reason }}
                    </li>
                  </ul>
                </div>
              </div>
            </div>

            <!-- 用户消息 -->
            <div v-if="msg.from === 'me'" class="message-right">
              <div class="message-bubble">
                <div class="message-content">{{ msg.content }}</div>
              </div>
              <div class="message-avatar">
                <div class="circular-avatar me-avatar">
                  <span class="avatar-text">ME</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="chat-input">
          <div class="input-container">
            <el-input
                v-model="message"
                type="textarea"
                :rows="3"
                placeholder="输入您的问题..."
                @keyup.enter.ctrl="sendMessage"
                resize="none"
            ></el-input>
            <el-button
                class="send-btn"
                type="primary"
                @click="sendMessage"
            >
              <el-icon class="icon"><Promotion /></el-icon>
            </el-button>
          </div>
        </div>
      </div>
    </div>

    <!-- 最小化按钮 -->
    <div
        class="minimized-chat"
        v-show="isChatMinimized"
        @click="restoreChat"
    >
      <div class="minimized-avatar">
        {{ minimizedTitle }}
      </div>
      <span class="minimized-title">点餐助手</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { Search, Promotion } from '@element-plus/icons-vue'
import { getMerchantList, searchMerchantsByDishName, getDishRecommendations } from '@/api/user'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 商家列表相关
const searchKeyword = ref('')
const merchants = ref([])
const page = ref(1)
const size = ref(8)
const total = ref(0)

// 聊天相关
const message = ref('')
const isChatMinimized = ref(false)
const chatContainer = ref(null)
const chatMessages = ref(null)
const chatMessagesArr = ref([])
const minimizedTitle = ref('AI')

// 获取商家列表
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

// 搜索功能
const handleSearch = async () => {
  page.value = 1
  const keyword = searchKeyword.value.trim()

  if (!keyword) {
    await fetchMerchants()
    return
  }

  try {
    const merchantRes = await getMerchantList({ page: page.value, size: size.value, keyword })
    if (merchantRes.data.records.length > 0) {
      merchants.value = merchantRes.data.records
      total.value = merchantRes.data.total
      return
    }

    const dishRes = await searchMerchantsByDishName({ page: page.value, size: size.value, keyword })
    if (dishRes.data.length > 0) {
      merchants.value = dishRes.data
      total.value = dishRes.data.length
      return
    }

    ElMessage.warning('没有搜索到结果')
    searchKeyword.value = ''
    await fetchMerchants()
  } catch (error) {
    console.error('搜索失败:', error)
    ElMessage.error('搜索失败，请重试')
  }
}

// 分页处理
const handleSizeChange = (val) => {
  size.value = val
  fetchMerchants()
}

const handleCurrentChange = (val) => {
  page.value = val
  fetchMerchants()
}

// 跳转商家详情
const goToMerchant = (shopId) => {
  router.push({ path: `/merchantdetail/${shopId}` })
}

// 图片处理
const getAvatarUrl = (avatar) => {
  return avatar ? `/images/${avatar}` : '/images/default-merchant.png'
}

const handleImageError = (e) => {
  e.target.src = '/images/default-merchant.png'
}

// 聊天功能
const sendMessage = async () => {
  const content = message.value.trim()
  if (!content) return

  // 添加用户消息
  chatMessagesArr.value.push({
    content,
    from: 'me'
  })
  message.value = ''

  // 滚动到底部
  nextTick(() => {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight
  })

  // 获取AI回复
  try {
    const userId = 1 // 假设用户ID为1
    const res = await getDishRecommendations(userId, content)

    if (res.data && res.data.length > 0) {
      // 添加AI推荐结果
      chatMessagesArr.value.push({
        content: res.data,
        from: 'ai'
      })
    } else {
      // 添加AI无推荐结果消息
      chatMessagesArr.value.push({
        content: '没有找到符合条件的推荐菜品',
        from: 'ai'
      })
    }
  } catch (error) {
    console.error('获取推荐菜品失败:', error)
    // 添加AI错误消息
    chatMessagesArr.value.push({
      content: '获取推荐失败，请重试',
      from: 'ai'
    })
    ElMessage.error('获取推荐菜品失败，请重试')
  }

  // 滚动到底部
  nextTick(() => {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight
  })
}

// 恢复聊天窗口
const restoreChat = () => {
  isChatMinimized.value = false
  nextTick(() => {
    chatMessages.value.scrollTop = chatMessages.value.scrollHeight
  })
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
  gap: 20px;
}

.content-left {
  flex: 1;
  overflow-y: auto;
}

/* 聊天容器样式 */
.chat-container {
  width: 400px;
  border-left: 1px solid #e6e6e6;
  display: flex;
  flex-direction: column;
  height: 100%;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
  background: white;
}

.chat-header {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60px;
  border-bottom: 1px solid #f0f0f0;
  padding: 0 20px;
}

.chat-header h3 {
  margin: 0;
  flex: 1;
  text-align: center;
}

.minimize-btn {
  cursor: pointer;
  font-size: 20px;
}

/* 消息区样式 */
.chat-messages {
  flex: 1;
  padding: 15px;
  overflow-y: auto;
  background: #f8f9fa;
}

.message {
  margin-bottom: 15px;
}

.message-left {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.message-right {
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  gap: 10px;
}

.circular-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 14px;
  flex-shrink: 0;
}

.ai-avatar {
  background-color: #409eff;
}

.me-avatar {
  background-color: #67c23a;
}

.message-bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 18px;
  line-height: 1.5;
}

.message-left .message-bubble {
  background: white;
  border: 1px solid #e5e7eb;
}

.message-right .message-bubble {
  background: white;
  color: black;
}

.recommended-dishes {
  background-color: #f0f0f0;
  padding: 10px;
  border-radius: 8px;
}

.recommended-dishes h4 {
  margin-top: 0;
  margin-bottom: 8px;
  font-size: 14px;
}

.recommended-dishes ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
}

.recommended-dishes li {
  margin-bottom: 5px;
  font-size: 14px;
}

/* 输入区样式 */
.chat-input {
  border-top: 1px solid #e5e7eb;
  padding: 16px;
}

.input-container {
  display: flex;
  gap: 12px;
  align-items: flex-end;
}

.send-btn {
  height: 72px;
  width: 50px;
  padding: 0;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.send-btn .icon {
  font-size: 20px;
}

/* 最小化样式 */
.minimized-chat {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 60px;
  height: 60px;
  background: #409eff; /* 蓝色背景 */
  border-radius: 50%;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: transform 0.2s;
}

.minimized-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  background-color: #409eff; /* 蓝色背景 */
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 5px;
}

.minimized-title {
  color: white;
  font-size: 12px;
  text-align: center;
}

/* 商家卡片样式 */
.merchant-card {
  margin-bottom: 20px;
  transition: transform 0.2s, box-shadow 0.2s;
  cursor: pointer;
}

.merchant-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.card-content {
  display: flex;
  padding: 16px;
  gap: 20px;
}

.merchant-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 6px;
  flex-shrink: 0;
}

.merchant-info {
  flex: 1;
}

.price-range {
  display: flex;
  gap: 15px;
  margin-top: 8px;
  color: #666;
}

@media (max-width: 768px) {
  .main-container {
    flex-direction: column;
  }

  .chat-container {
    width: 100%;
    height: 60vh;
    margin-top: 20px;
  }

  .send-btn {
    height: 66px;
    width: 45px;
  }
}
</style>