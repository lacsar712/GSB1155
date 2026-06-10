<template>
  <div class="organizations-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>组织机构管理</span>
          <el-button type="primary" @click="openCreateDialog">
            <el-icon><Plus /></el-icon>
            新建机构
          </el-button>
        </div>
      </template>
      
      <el-table 
        :data="orgList" 
        v-loading="loading"
        stripe
        border
        row-key="id"
        default-expand-all
      >
        <el-table-column prop="name" label="机构名称" min-width="200" />
        <el-table-column prop="code" label="机构编码" width="120" />
        <el-table-column label="机构类型" width="120" align="center">
          <template #default="{ row }">
            <el-tag :type="getOrgTypeTag(row.orgType)">
              {{ getOrgTypeName(row.orgType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="editOrg(row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="deleteOrg(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新建/编辑机构对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑机构' : '新建机构'"
      width="500px"
      destroy-on-close
    >
      <el-form 
        ref="formRef"
        :model="orgForm" 
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="机构名称" prop="name">
          <el-input v-model="orgForm.name" placeholder="请输入机构名称" />
        </el-form-item>
        
        <el-form-item label="机构编码" prop="code" v-if="!isEdit">
          <el-input v-model="orgForm.code" placeholder="请输入机构编码" />
        </el-form-item>
        
        <el-form-item label="上级机构" prop="parentId">
          <el-select v-model="orgForm.parentId" placeholder="请选择上级机构" style="width: 100%">
            <el-option label="无 (顶级机构)" :value="0" />
            <el-option
              v-for="item in parentOrgOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        
        <el-form-item label="机构类型" prop="orgType">
          <el-select v-model="orgForm.orgType" placeholder="请选择类型" style="width: 100%">
            <el-option label="教育局" :value="1" />
            <el-option label="学校" :value="2" />
            <el-option label="部门" :value="3" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="orgForm.sortOrder" :min="0" :max="999" />
        </el-form-item>
        
        <el-form-item label="状态">
          <el-switch v-model="orgForm.status" :active-value="1" :inactive-value="0" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { organizationApi } from '@/api/organization'
import type { Organization } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const orgList = ref<Organization[]>([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)

const formRef = ref<FormInstance>()
const orgForm = reactive({
  name: '',
  code: '',
  parentId: 0,
  orgType: 2,
  sortOrder: 0,
  status: 1
})

const formRules: FormRules = {
  name: [{ required: true, message: '请输入机构名称', trigger: 'blur' }],
  code: [{ required: true, message: '请输入机构编码', trigger: 'blur' }],
  orgType: [{ required: true, message: '请选择机构类型', trigger: 'change' }]
}

const parentOrgOptions = computed(() => {
  return orgList.value.filter(org => org.orgType < 3)
})

const fetchOrganizations = async () => {
  loading.value = true
  try {
    const res = await organizationApi.getAll()
    if (res.data) orgList.value = res.data
  } finally {
    loading.value = false
  }
}

const getOrgTypeName = (type: number) => {
  const names: Record<number, string> = { 1: '教育局', 2: '学校', 3: '部门' }
  return names[type] || '未知'
}

const getOrgTypeTag = (type: number) => {
  const tags: Record<number, string> = { 1: 'danger', 2: 'primary', 3: 'success' }
  return tags[type] || 'info'
}

const openCreateDialog = () => {
  isEdit.value = false
  editingId.value = null
  Object.assign(orgForm, {
    name: '',
    code: '',
    parentId: 0,
    orgType: 2,
    sortOrder: 0,
    status: 1
  })
  dialogVisible.value = true
}

const editOrg = (org: Organization) => {
  isEdit.value = true
  editingId.value = org.id
  Object.assign(orgForm, {
    name: org.name,
    code: org.code,
    parentId: org.parentId,
    orgType: org.orgType,
    sortOrder: org.sortOrder,
    status: org.status
  })
  dialogVisible.value = true
}

const deleteOrg = async (org: Organization) => {
  try {
    await ElMessageBox.confirm('确定删除该机构吗？', '警告', { type: 'warning' })
    await organizationApi.delete(org.id)
    ElMessage.success('删除成功')
    fetchOrganizations()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    submitLoading.value = true
    try {
      if (isEdit.value && editingId.value) {
        await organizationApi.update(editingId.value, orgForm)
        ElMessage.success('更新成功')
      } else {
        await organizationApi.create(orgForm)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      fetchOrganizations()
    } finally {
      submitLoading.value = false
    }
  })
}

onMounted(() => {
  fetchOrganizations()
})
</script>

<style lang="scss" scoped>
.organizations-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
}
</style>
