<template>
  <div class="merchant-home">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>今日订单</span>
          </div>
          <div class="card-content">
            <div class="number">{{ statistics.todayOrders }}</div>
            <div class="label">订单数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>今日营业额</span>
          </div>
          <div class="card-content">
            <div class="number">¥{{ statistics.todayRevenue }}</div>
            <div class="label">金额</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>菜品总数</span>
          </div>
          <div class="card-content">
            <div class="number">{{ statistics.totalDishes }}</div>
            <div class="label">个</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="box-card">
          <div slot="header" class="clearfix">
            <span>待处理订单</span>
          </div>
          <div class="card-content">
            <div class="number">{{ statistics.pendingOrders }}</div>
            <div class="label">个</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card class="chart-card">
      <div slot="header" class="clearfix">
        <span>最近7天营业额统计</span>
      </div>
      <div class="chart-container">
        <!-- 这里可以添加图表组件 -->
      </div>
    </el-card>

    <el-card class="recent-orders">
      <div slot="header" class="clearfix">
        <span>最近订单</span>
      </div>
      <el-table :data="recentOrders" style="width: 100%">
        <el-table-column prop="orderId" label="订单号" width="180"></el-table-column>
        <el-table-column prop="createTime" label="下单时间" width="180"></el-table-column>
        <el-table-column prop="amount" label="金额" width="100"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getStatistics, getOrderList } from '@/api/merchant'

export default {
  name: 'MerchantHome',
  props: ['shopId'],
  data() {
    return {
      statistics: {
        todayOrders: 0,
        todayRevenue: 0,
        totalDishes: 0,
        pendingOrders: 0
      },
      recentOrders: [],
      merchantInfo: {}
    }
  },
  // 添加生命周期钩子
    mounted() {
      console.log('当前店铺ID:', this.shopId)
    },
  created() {
    this.fetchStatistics()
    this.fetchRecentOrders()
  },
  methods: {
    async fetchMerchantInfo(shopId) {
        try {
          const res = await getMerchantInfo(shopId); //使用 shopId 参数
          this.merchantInfo = res.data;
        } catch (error) {
          console.error('获取商家信息失败:', error);
        }
      },
    async fetchStatistics() {
      try {
        const res = await getStatistics(this.shopId)
        this.statistics = res.data
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    },
    async fetchRecentOrders() {
      try {
        const res = await getOrderList(this.shopId,{ page: 1, size: 5 })
        this.recentOrders = res.data.records
      } catch (error) {
        console.error('获取最近订单失败:', error)
      }
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
    }
  }
}
</script>

<style scoped>
.merchant-home {
  padding: 20px;
}
.box-card {
  margin-bottom: 20px;
}
.card-content {
  text-align: center;
  padding: 20px 0;
}
.number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}
.label {
  margin-top: 10px;
  color: #666;
}
.chart-card {
  margin-bottom: 20px;
}
.chart-container {
  height: 300px;
}
.recent-orders {
  margin-bottom: 20px;
}
</style> 