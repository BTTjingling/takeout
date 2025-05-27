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
        <v-chart :option="chartOptions" autoresize style="height: 100%;" />
      </div>
    </el-card>
  </div>
</template>

<script>
import { defineComponent } from 'vue'
import {
  getTodayOrderCount,
  getTotalDishes,
  getTodayRevenue,
  getPendingOrders,
  getRevenueLast7Days
} from '@/api/merchant'
import 'echarts'
import VChart from 'vue-echarts'

export default defineComponent({
  name: 'MerchantHome',
  components: {
    'v-chart': VChart
  },
  props: ['shopId'],
  data() {
    return {
      statistics: {
        todayOrders: 0,
        todayRevenue: 0,
        totalDishes: 0,
        pendingOrders: 0
      },
      chartOptions: {
        tooltip: {
          trigger: 'axis'
        },
        xAxis: {
          type: 'category',
          data: []
        },
        yAxis: {
          type: 'value'
        },
        series: [
          {
            name: '营业额',
            type: 'line',
            data: [],
            smooth: true,
            areaStyle: {
              color: '#cce5ff'
            },
            lineStyle: {
              color: '#409EFF'
            },
            itemStyle: {
              color: '#409EFF'
            }
          }
        ]
      }
    }
  },
  mounted() {
    console.log('当前店铺ID:', this.shopId)
  },
  created() {
    this.fetchStatistics()
    this.fetchChartData()
  },
  methods: {
    async fetchStatistics() {
      try {
        const [todayOrdersRes, totalDishesRes, todayRevenueRes, pendingOrdersRes] = await Promise.all([
          getTodayOrderCount(this.shopId),
          getTotalDishes(this.shopId),
          getTodayRevenue(this.shopId),
          getPendingOrders(this.shopId)
        ])

        this.statistics = {
          todayOrders: todayOrdersRes.data,
          todayRevenue: todayRevenueRes.data,
          totalDishes: totalDishesRes.data,
          pendingOrders: pendingOrdersRes.data
        }
      } catch (error) {
        console.error('获取统计数据失败:', error)
      }
    },
    async fetchChartData() {
      try {
        const res = await getRevenueLast7Days(this.shopId)
        const entries = Object.entries(res.data)

        const days = entries.map(([date]) => date)
        const revenues = entries.map(([, revenue]) => revenue)

        this.chartOptions.xAxis.data = days
        this.chartOptions.series[0].data = revenues
      } catch (error) {
        console.error('获取最近7天营业额失败:', error)
      }
    }
  }
})
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
</style>
