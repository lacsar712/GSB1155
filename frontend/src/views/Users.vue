<template>
  <div class="users-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="openCreateDialog" v-if="userStore.isAdmin">
            <el-icon><Plus /></el-icon>
            新建用户
          </el-button>
        </div>
      </template>
      
      <el-table 
        :data="userList" 
        v-loading="loading"
        stripe
        border
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="orgName" label="所属机构" min-width="150" show-overflow-tooltip />
        <el-table-column label="角色" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getRoleType(row.roleType)">{{ row.roleName }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="editUser(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="deleteUser(row)" v-if="userStore.isAdmin">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新建/编辑用户对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑用户' : '新建用户'"
      width="500px"
      destroy-on-close
    >
      <el-form 
        ref="formRef"
        :model="userForm" 
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="用户名" prop="username" v-if="!isEdit">
          <el-input v-model="userForm.username" placeholder="请输入用户名" />
        </el-form-item>
        
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="userForm.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        
        <el-form-item label="所属机构" prop="orgId">
          <el-select v-model="userForm.orgId" placeholder="请选择所属机构" style="width: 100%">
            <el-option
              v-for="item in organizations"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="角色" prop="roleType">
          <el-select v-model="userForm.roleType" placeholder="请选择角色" style="width: 100%">
            <el-option label="系统管理员" :value="1" v-if="userStore.isAdmin" />
            <el-option label="单位管理员" :value="2" />
            <el-option label="巡检员" :value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="状态">
          <el-switch v-model="userForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { useUserStore } from '@/stores/user'
import { userApi } from '@/api/user'
import { organizationApi } from '@/api/organization'
import type { User, Organization } from '@/types'
import { sha256Hex } from '@/utils/password'

const userStore = useUserStore()

const loading = ref(false)
const submitLoading = ref(false)
const userList = ref<User[]>([])
const organizations = ref<Organization[]>([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)

const formRef = ref<FormInstance>()
const userForm = reactive({
  username: '',
  password: '',
  realName: '',
  phone: '',
  email: '',
  orgId: undefined as number | undefined,
  roleType: 3,
  status: 1
})

const formRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  orgId: [{ required: true, message: '请选择所属机构', trigger: 'change' }],
  roleType: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await userApi.getAll()
    if (res.data) userList.value = res.data
  } finally {
    loading.value = false
  }
}

const fetchOrganizations = async () => {
  const res = await organizationApi.getAll()
  if (res.data) organizations.value = res.data
}

const getRoleType = (roleType: number) => {
  const types: Record<number, string> = { 1: 'danger', 2: 'warning', 3: 'success' }
  return types[roleType] || 'info'
}

const openCreateDialog = () => {
  isEdit.value = false
  editingId.value = null
  Object.assign(userForm, {
    username: '',
    password: '',
    realName: '',
    phone: '',
    email: '',
    orgId: undefined,
    roleType: 3,
    status: 1
  })
  dialogVisible.value = true
}

const editUser = (user: User) => {
  isEdit.value = true
  editingId.value = user.id
  Object.assign(userForm, {
    username: user.username,
    password: '',
    realName: user.realName,
    phone: user.phone,
    email: user.email,
    orgId: user.orgId,
    roleType: user.roleType,
    status: user.status
  })
  dialogVisible.value = true
}

const deleteUser = async (user: User) => {
  try {
    await ElMessageBox.confirm('确定删除该用户吗？', '警告', { type: 'warning' })
    await userApi.delete(user.id)
    ElMessage.success('删除成功')
    fetchUsers()
  } catch {
    // 取消
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      if (isEdit.value && editingId.value) {
        const { password, username, ...updateData } = userForm
        await userApi.update(editingId.value, updateData)
        ElMessage.success('更新成功')
      } else {
        await userApi.create({
          username: userForm.username,
          passwordHash: await sha256Hex(userForm.password),
          realName: userForm.realName,
          phone: userForm.phone,
          email: userForm.email,
          orgId: userForm.orgId!,
          roleType: userForm.roleType,
          status: userForm.status
        })
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      fetchUsers()
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(() => {
  fetchUsers()
  fetchOrganizations()
})
</script>

<style lang="scss" scoped>
.users-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
