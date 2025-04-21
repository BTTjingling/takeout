<template>
  <div class="user-profile">
    <el-card>
      <template #header>
        <span>个人信息</span>
      </template>
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="手机号码" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" />
        </el-form-item>
        <el-form-item label="收货地址" prop="address">
          <el-input type="textarea" v-model="form.address" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitForm">保存修改</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="password-card">
      <template #header>
        <span>修改密码</span>
      </template>
      <el-form :model="passwordForm" :rules="passwordRules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input type="password" v-model="passwordForm.oldPassword" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitPasswordForm">修改密码</el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getUserInfo, updateUserInfo, changePassword } from '@/api/user'
import { ElMessage } from 'element-plus'

const formRef = ref(null)
const passwordFormRef = ref(null)

const form = ref({
  username: '',
  phone: '',
  email: '',
  address: ''
})

const passwordForm = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入密码'))
  } else {
    if (passwordForm.value.confirmPassword !== '') {
      passwordFormRef.value?.validateField('confirmPassword')
    }
    callback()
  }
}

const validatePass2 = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== passwordForm.value.newPassword) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
  ]
}

const passwordRules = {
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

const fetchUserInfo = async () => {
  try {
    const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
    const userId = userInfo.userId;
    if (userId) {
      const res = await getUserInfo(userId);
      form.value = res.data;
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await updateUserInfo(form.value);
        ElMessage.success('保存成功');
      } catch (error) {
        console.error('更新用户信息失败:', error);
      }
    }
  })
}

const submitPasswordForm = async () => {
  if (!passwordFormRef.value) return
  await passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        await changePassword({
          oldPassword: passwordForm.value.oldPassword,
          newPassword: passwordForm.value.newPassword
        });
        ElMessage.success('密码修改成功');
        passwordForm.value = {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        };
      } catch (error) {
        console.error('修改密码失败:', error);
      }
    }
  })
}

onMounted(() => {
  fetchUserInfo();
})
</script>

<style scoped>
.user-profile {
  padding: 20px;
}
.password-card {
  margin-top: 20px;
}
</style>