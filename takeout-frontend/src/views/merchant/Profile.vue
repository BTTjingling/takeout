<template>
  <div class="merchant-profile">
    <el-card>
      <div slot="header">
        <span>商铺信息</span>
      </div>
      <div class="avatar-section">
                    <el-upload
                      class="avatar-uploader"
                      action=""
                      :show-file-list="false"
                      :before-upload="beforeAvatarUpload"
                      :http-request="handleAvatarUpload"
                    >
                      <img v-if="form.avatarUrl" :src="form.avatarUrl" class="avatar">
                      <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                    <p class="avatar-tip">点击上传头像</p>
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
import { getMerchantInfo, updateMerchantInfo, changePassword ,updateMerchantStatus,uploadAvatar, getAvatarUrl } from '@/api/merchant'
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
        devfee: 0.0,
        avatarUrl: '/images/default-merchant.png',
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
    this.fetchAvatar()
  },
  methods: {
    async fetchMerchantInfo() {
          try {
            const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
            if (!userInfo?.shopId) {
              throw new Error('未获取到商家ID')
            }
            const res = await getMerchantInfo(userInfo.shopId)
            this.form = res.data
          } catch (error) {
            console.error('获取商家信息失败:', error)
            this.$message.error('获取商家信息失败')
          }
        },
    async fetchAvatar() {
      try {
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        if (!userInfo?.shopId) {
          throw new Error('未获取到商家ID')
        }
        const res = await getAvatarUrl(userInfo.shopId)
        if (res && res.data) {
          const avatarPath = typeof res.data === 'string' ? res.data : Object.values(res.data).join('')
          // 添加时间戳参数避免缓存
          this.form.avatarUrl = `${avatarPath}?t=${new Date().getTime()}`
        }
      } catch (error) {
        console.error('获取头像失败:', error)
        this.form.avatarUrl = '/images/default-merchant.png'
      }
    },


    beforeAvatarUpload(file) {
        const isImage = file.type.includes('image/')
        const isLt2M = file.size / 1024 / 1024 < 2

        if (!isImage) {
          this.$message.error('只能上传图片文件!')
          return false
        }
        if (!isLt2M) {
          this.$message.error('头像图片大小不能超过 2MB!')
          return false
        }
        return true
    },

     async handleAvatarUpload({ file }) {
       try {
         const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
         if (!userInfo?.shopId) {
           throw new Error('未获取到商家ID')
         }

         const formData = new FormData()
         formData.append('file', file)
         formData.append('shopId', userInfo.shopId)

         await uploadAvatar(formData)
         this.$message.success('头像上传成功!')
         this.fetchAvatar() // 重新获取头像URL
       } catch (error) {
         console.error('头像上传失败:', error)
         this.$message.error('头像上传失败: ' + (error.response?.data?.message || error.message))
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
.avatar-section {
  margin: 20px 0;
  text-align: center;
}

.avatar-uploader {
  display: inline-block;
}

.avatar {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px dashed #d9d9d9;
  cursor: pointer;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  line-height: 120px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 50%;
}

.avatar-tip {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}
</style>