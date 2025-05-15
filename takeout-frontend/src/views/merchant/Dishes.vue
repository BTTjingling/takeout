<template>
  <div class="dishes-manage">
    <div class="operation-bar">
      <el-button type="primary" @click="handleAdd">添加菜品</el-button>
    </div>

    <el-table :data="dishes" style="width: 100%">
      <el-table-column prop="name" label="菜品名称"></el-table-column>
      <el-table-column prop="price" label="价格">
        <template v-slot:default="scope">
          ¥{{ scope.row.price }}
        </template>
      </el-table-column>
      <el-table-column prop="description" label="描述"></el-table-column>
      <el-table-column prop="status" label="状态">
        <template v-slot:default="scope">
          <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
            {{ scope.row.status === 1 ? '上架' : '下架' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="inventory" label="库存"></el-table-column> <!-- 添加库存列 -->
      <el-table-column label="操作" width="200">
        <template v-slot:default="scope">
          <el-button size="mini" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="mini" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="page"
        :page-sizes="[10, 20, 50, 100]"
        :page-size="size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total">
    </el-pagination>

    <!-- 添加/编辑菜品对话框 -->
    <a-modal
        :title="dialogTitle"
        :open="dialogVisible"
        @ok="submitForm"
        @cancel="dialogVisible = false"
    >
      <a-form :model="form" :rules="rules" ref="form">
        <a-form-item label="菜品名称" name="name">
          <a-input v-model:value="form.name" />
        </a-form-item>
        <a-form-item label="价格" name="price">
          <a-input-number v-model:value="form.price" :precision="2" :step="0.1" :min="0" />
        </a-form-item>
        <a-form-item label="描述" name="description">
          <a-textarea v-model:value="form.description" />
        </a-form-item>
        <a-form-item label="库存" name="inventory"> <!-- 添加库存输入项 -->
          <a-input-number v-model:value="form.inventory" :min="0" />
        </a-form-item>
        <a-form-item label="状态" name="status">
          <a-switch
              v-model:checked="form.status"
              :checked-value="1"
              :un-checked-value="0"
              checked-children="上架"
              un-checked-children="下架"
          />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script>
import { getDishList, addDish, updateDish, deleteDish } from '@/api/merchant'
import { Modal as AModal, Form as AForm, Input as AInput, InputNumber as AInputNumber, Switch as ASwitch, Textarea as ATextarea } from 'ant-design-vue';
export default {
  props: ['shopId'],
  name: 'DishesManage',
  components: {
    AModal,
    AForm,
    AFormItem: AForm.Item,
    AInput,
    AInputNumber,
    ASwitch,
    ATextarea,
  },
  data() {
    return {
      dishes: [],
      page: 1,
      size: 10,
      total: 0,
      dialogVisible: false,
      dialogTitle: '',
      form: {
        shopId: this.shopId,
        id: null,
        name: '',
        price: 0,
        description: '',
        status: 1,
        inventory: 0
      },
      rules: {
        name: [
          { required: true, message: '请输入菜品名称', trigger: 'blur' }
        ],
        price: [
          { required: true, message: '请输入价格', trigger: 'blur' }
        ],
        inventory: [ // 添加库存验证规则
          { required: true, message: '请输入库存数量', trigger: 'blur' },
          { type: 'number', min: 0, message: '库存不能为负数', trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchDishes()
  },
  methods: {
    async fetchDishes() {
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        const shopId = this.shopId || userInfo?.shopId
        if (!shopId) {
          this.$message.error('无法获取商家ID')
          return
        }
        const res = await getDishList(shopId,{
          page: this.page,
          size: this.size
        })
        this.dishes = res.data.records
        this.total = res.data.total
      } catch (error) {
        console.error('获取菜品列表失败:', error)
      }
    },
    handleSizeChange(val) {
      this.size = val
      this.fetchDishes()
    },
    handleCurrentChange(val) {
      this.page = val
      this.fetchDishes()
    },
    handleAdd() {
      console.log('添加菜品按钮被点击')
      this.dialogTitle = '添加菜品'
      this.form = {
        shopId: this.shopId,
        id: null,
        name: '',
        price: 0,
        description: '',
        status: 1,
        inventory: 0 // 初始化库存为0
      }
      this.dialogVisible = true
      console.log('dialogVisible:', this.dialogVisible)
    },
    handleEdit(row) {
      this.dialogTitle = '编辑菜品'
      this.form = { ...row , shopId: this.shopId }
      this.dialogVisible = true
    },
    async handleDelete(row) {
      try {
        await this.$confirm('确认删除该菜品?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        await deleteDish(this.shopId, row.id)
        this.$message.success('删除成功')
        this.fetchDishes()
      } catch (error) {
        if (error !== 'cancel') {
          console.error('删除菜品失败:', error)
        }
      }
    },
    submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          try {
            if (this.form.id) {
              await updateDish(this.form)
            } else {
              await addDish(this.form)
            }
            this.$message.success('保存成功')
            this.dialogVisible = false
            this.fetchDishes()
          } catch (error) {
            console.error('保存菜品失败:', error)
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.dishes-manage {
  padding: 20px;
}
.operation-bar {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
<style>
.dish-dialog {
  z-index: 2000 !important; /* 确保对话框在最上层 */
}
</style>