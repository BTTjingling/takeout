<template>
  <div class="user-profile">
    <!-- 导引栏 -->
    <el-menu :default-active="activeTab" class="guide-menu" @select="handleSelect">
      <el-menu-item index="personal">个人资料</el-menu-item>
      <el-menu-item index="change-password">修改密码</el-menu-item>
      <el-menu-item index="addresses">收货地址</el-menu-item> <!-- 新增收货地址选项 -->
    </el-menu>

    <!-- 内容区域 -->
    <div class="content">
      <!-- 个人资料表单 -->
      <el-card v-if="activeTab === 'personal'">
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

      <!-- 修改密码表单 -->
      <el-card v-if="activeTab === 'change-password'" class="password-card">
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

      <!-- 收货地址列表 -->
      <el-card v-if="activeTab === 'addresses'">
        <template #header>
          <span>收货地址</span>
        </template>
        <el-button type="primary" @click="handleAdd">+ 新增地址</el-button>

        <!-- 地址列表 -->
        <el-table :data="addresses" style="width: 100%" border>
          <el-table-column label="收货人" prop="recipientName" :width="100"></el-table-column>
          <el-table-column label="联系电话" prop="recipientPhone" :width="150"></el-table-column>
          <el-table-column label="详细地址" prop="fullAddress"></el-table-column>
          <el-table-column label="地址类别" prop="addressType" :width="100"></el-table-column>
          <el-table-column label="操作">
            <template #default="scope">
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="danger" size="small" @click="handleDelete(scope.row)">删除</el-button>
              <el-button
                      v-if="!scope.row.isDefault"
                      type="success"
                      size="small"
                      @click="setDefaultAddress(scope.row)">
                设置默认
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 新增地址表单弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增地址" width="50%" :close-on-click-modal="false" :center="true" :append-to-body="true">
      <el-form :model="addressForm" label-width="100px">
        <el-form-item label="收货人" prop="recipientName">
          <el-input v-model="addressForm.recipientName" />
        </el-form-item>
        <el-form-item label="联系电话" prop="recipientPhone">
          <el-input v-model="addressForm.recipientPhone" />
        </el-form-item>
        <el-form-item label="详细地址" prop="fullAddress">
          <el-input v-model="addressForm.fullAddress" />
        </el-form-item>
        <el-form-item label="地址类别" prop="addressType">
          <el-input v-model="addressForm.addressType" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSaveAddress">保存</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

    <!-- 编辑地址表单弹窗 -->
    <el-dialog v-model="editDialogVisible" title="编辑地址" width="50%" :close-on-click-modal="false" :append-to-body="true">
      <el-form :model="addressForm" label-width="100px">
        <el-form-item label="收货人" prop="recipientName">
          <el-input v-model="addressForm.recipientName" />
        </el-form-item>
        <el-form-item label="联系电话" prop="recipientPhone">
          <el-input v-model="addressForm.recipientPhone" />
        </el-form-item>
        <el-form-item label="详细地址" prop="fullAddress">
          <el-input v-model="addressForm.fullAddress" />
        </el-form-item>
        <el-form-item label="地址类别" prop="addressType">
          <el-input v-model="addressForm.addressType" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleUpdateAddress">更新</el-button>
          <el-button @click="editDialogVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script>
  import { ElMessage } from 'element-plus'
  import { ref, onMounted } from 'vue'
  import { getUserInfo, updateUserInfo, changePassword, getUserAddresses, addUserAddress, updateUserAddress, deleteUserAddress,setDefaultAddress } from '@/api/user'

  export default {
    data() {
      return {
        activeTab: 'personal', // 默认选中个人资料
        form: {
          username: '',
          phone: '',
          email: '',
          address: ''
        },
        passwordForm: {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        },
        addresses: [],  // 存储地址列表
        dialogVisible: false,  // 控制新增地址弹窗的显示
        editDialogVisible: false,  // 控制编辑地址弹窗的显示
        addressForm: {
          recipientName: '',
          recipientPhone: '',
          fullAddress: '',
          addressType: ''
        },
        rules: {
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
        },
        passwordRules: {
          oldPassword: [
            { required: true, message: '请输入原密码', trigger: 'blur' }
          ],
          newPassword: [
            { required: true, message: '请输入新密码', trigger: 'blur' }
          ],
          confirmPassword: [
            { required: true, message: '请确认密码', trigger: 'blur' }
          ]
        },
        userId: JSON.parse(localStorage.getItem('userInfo') || '{}').userId  // 从 localStorage 获取 userId
      }
    },
    methods: {
      async fetchUserInfo() {
        try {
          const res = await getUserInfo(this.userId);

          this.form = res.data;
        } catch (error) {
          console.error('获取用户信息失败:', error);
        }
      },
      async fetchUserAddresses() {
        try {
          const response = await getUserAddresses(this.userId);
          this.addresses = response.data;
        } catch (error) {
          console.error('获取用户地址列表失败:', error);
        }
      },
      handleDelete(row) {
        try {
          deleteUserAddress(row.addressId);
          ElMessage.success('删除成功');
          this.fetchUserAddresses();
        } catch (error) {
          console.error('删除地址失败:', error);
        }
      },
      handleAdd() {
        this.addressForm = { recipientName: '', recipientPhone: '', fullAddress: '', addressType: '' };
        this.dialogVisible = true;
      },
      handleEdit(row) {
        this.addressForm = { ...row };
        console.log(this.addressForm.addressId);
        this.editDialogVisible = true;
      },
      async handleSaveAddress() {
        try {
          await addUserAddress(this.userId, this.addressForm);
          ElMessage.success('新增地址成功');
          this.dialogVisible = false;
          this.fetchUserAddresses();
        } catch (error) {
          console.error('新增地址失败:', error);
        }
      },
      async handleUpdateAddress() {
        try {
          await updateUserAddress(this.addressForm.addressId,this.addressForm);
          ElMessage.success('更新地址成功');
          this.editDialogVisible = false;
          this.fetchUserAddresses();
        } catch (error) {
          console.error('更新地址失败:', error);
        }
      },
      // 设置默认地址
      async setDefaultAddress(row) {
        try {
          await setDefaultAddress(this.userId,row.addressId);  // 调用 API 设置默认地址
          ElMessage.success('设置默认地址成功');
          this.fetchUserAddresses();
        } catch (error) {
          console.error('设置默认地址失败:', error);
        }
      },
      async submitForm() {
        if (!this.$refs.formRef) return;
        this.$refs.formRef.validate(async (valid) => {
          if (valid) {
            try {
              await updateUserInfo(this.form);
              ElMessage.success('保存成功');
            } catch (error) {
              console.error('更新用户信息失败:', error);
            }
          }
        });
      },
      async submitPasswordForm() {
        if (!this.$refs.passwordFormRef) return;
        this.$refs.passwordFormRef.validate(async (valid) => {
          if (valid) {
            try {
              await changePassword({
                oldPassword: this.passwordForm.oldPassword,
                newPassword: this.passwordForm.newPassword
              });
              ElMessage.success('密码修改成功');
              this.passwordForm = {
                oldPassword: '',
                newPassword: '',
                confirmPassword: ''
              };
            } catch (error) {
              console.error('修改密码失败:', error);
            }
          }
        });
      },
      handleSelect(key) {
        this.activeTab = key;
      }
    },
    mounted() {
      this.fetchUserInfo();
      this.fetchUserAddresses();
    }
  };
</script>

<style scoped>
  .user-profile {
    display: flex;
    padding: 20px;
  }

  .guide-menu {
    width: 200px;
    margin-right: 20px;
  }

  .content {
    flex: 1;
  }

  .password-card {
    margin-top: 20px;
  }

  .el-table {
    margin-top: 20px;
  }

  .el-dialog {
  z-index: 9999 !important;
  }
</style>
