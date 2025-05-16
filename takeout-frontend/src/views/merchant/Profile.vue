<template>
  <div class="merchant-profile">
    <el-card>
      <div slot="header">
        <span>商铺信息</span>
      </div>
      <el-form :model="form" :rules="rules" ref="form" label-width="100px">
        <el-form-item label="商家名称" prop="name">
          <el-input v-model="form.name"></el-input>
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone"></el-input>
        </el-form-item>
        <el-form-item label="商家地址" prop="address">
          <el-input v-model="form.address"></el-input>
        </el-form-item>
        <el-form-item label="商家简介" prop="description">
          <el-input type="textarea" v-model="form.description"></el-input>
        </el-form-item>
        <el-form-item label="起送价" prop="minprice">
          <el-input-number v-model="form.minprice" :min="0" :precision="2" placeholder="请输入起送价"></el-input-number>
        </el-form-item>
        <el-form-item label="配送费" prop="devfee">
          <el-input-number v-model="form.devfee" :min="0" :precision="2" placeholder="请输入配送费"></el-input-number>
        </el-form-item>
        <a-form-item label="商铺状态">
          <a-switch
            v-model:checked="form.status"
            :checked-value="1"
            :un-checked-value="0"
            checked-children="营业"
            un-checked-children="歇业"
            @change="handleStatusChange"
          />
        </a-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="password-card">
      <div slot="header">
        <span>修改密码</span>
      </div>
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordForm" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input type="password" v-model="passwordForm.oldPassword"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword"></el-input>
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword"></el-input>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitPasswordForm">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script>
import { getMerchantInfo, updateMerchantInfo, changePassword ,updateMerchantStatus} from '@/api/merchant'
import { FormItem as AFormItem, Switch as ASwitch } from 'ant-design-vue';
export default {
  name: 'MerchantProfile',
  components: { // 注册组件
      AFormItem,
      ASwitch
    },
  data() {
    const validatePass = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请输入密码'))
      } else {
        if (this.passwordForm.confirmPassword !== '') {
          this.$refs.passwordForm.validateField('confirmPassword')
        }
        callback()
      }
    }
    const validatePass2 = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('请再次输入密码'))
      } else if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致!'))
      } else {
        callback()
      }
    }
    return {
      form: {
        name: '',
        phone: '',
        address: '',
        description: '',
        Status: 0,
        minprice: 0.0,
        devfee: 0.0
      },
      rules: {
        name: [
          { required: true, message: '请输入商家名称', trigger: 'blur' }
        ],
        phone: [
          { required: true, message: '请输入联系电话', trigger: 'blur' },
          { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
        ],
        address: [
          { required: true, message: '请输入商家地址', trigger: 'blur' }
        ],
        minprice: [
          { required: true, message: '请输入起送价', trigger: 'blur' },
          { type: 'number', min: 0, message: '起送价不能为负数', trigger: 'blur' }
        ],
        devfee: [
          { required: true, message: '请输入配送费', trigger: 'blur' },
          { type: 'number', min: 0, message: '配送费不能为负数', trigger: 'blur' }
        ],
        shopStatus: [
            { required: true, message: '请选择商铺状态' }
          ]
      },
      passwordForm: {
        oldPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        oldPassword: [
          { required: true, message: '请输入原密码', trigger: 'blur' }
        ],
        newPassword: [
          { required: true, validator: validatePass, trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, validator: validatePass2, trigger: 'blur' }
        ]
      }
    }
  },
  created() {
    this.fetchMerchantInfo()
  },
  methods: {
    async fetchMerchantInfo() {
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo'))
        if (!userInfo?.shopId) {
                throw new Error('未获取到商家ID')
        }
        const res = await getMerchantInfo(userInfo.shopId)
        this.form = res.data
      } catch (error) {
        console.error('获取商家信息失败:', error)
      }
    },
    async handleStatusChange(newStatus) {
        try {
          const userInfo = JSON.parse(localStorage.getItem('userInfo'))
          if (!userInfo?.shopId) {
            throw new Error('未获取到商家ID')
          }

          // 调用API更新状态
          await updateMerchantStatus(userInfo.shopId, newStatus)

          // 更新成功提示
          this.$message.success('状态更新成功')

          // 更新本地状态
          this.form.Status = newStatus
        } catch (error) {
          console.error('状态更新失败:', error)

          // 更新失败提示
          this.$message.error('状态更新失败: ' + (error.response?.data?.message || error.message))

          // 恢复之前的状态
          this.form.Status = this.form.Status === 1 ? 0 : 1
        }
      },
    submitForm() {
      this.$refs.form.validate(async (valid) => {
        if (valid) {
          try {
          const userInfo = JSON.parse(localStorage.getItem('userInfo'))
                    if (!userInfo?.shopId) {
                      throw new Error('未获取到商家ID')
                    }

            await updateMerchantInfo(userInfo.shopId,this.form)
            this.$message.success('保存成功')
          } catch (error) {
            console.error('更新商家信息失败:', error)
          }
        }
      })
    },
    submitPasswordForm() {
      this.$refs.passwordForm.validate(async (valid) => {
        if (valid) {
          try {
          const userInfo = JSON.parse(localStorage.getItem('userInfo'))
                              if (!userInfo?.shopId) {
                                throw new Error('未获取到商家ID')
                              }
            await changePassword({
              shopId: userInfo.shopId,
              oldPassword: this.passwordForm.oldPassword,
              newPassword: this.passwordForm.newPassword
            })
            this.$message.success('密码修改成功')
            this.passwordForm = {
              oldPassword: '',
              newPassword: '',
              confirmPassword: ''
            }
          } catch (error) {
            console.error('修改密码失败:', error)
          }
        }
      })
    }
  }
}
</script>

<style scoped>
.merchant-profile {
  padding: 20px;
}
.password-card {
  margin-top: 20px;
}
</style>