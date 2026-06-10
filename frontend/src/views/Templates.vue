<template>
  <div class="templates-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>检查模板管理</span>
          <el-button type="primary" @click="openCreateDialog">
            <el-icon><Plus /></el-icon>
            新建模板
          </el-button>
        </div>
      </template>
      
      <!-- 模板列表 -->
      <el-table 
        :data="templateList" 
        v-loading="loading"
        stripe
        border
      >
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="name" label="模板名称" min-width="180" />
        <el-table-column prop="category" label="检查类别" width="120" />
        <el-table-column prop="description" label="描述" min-width="200" show-overflow-tooltip />
        <el-table-column label="检查项数" width="100" align="center">
          <template #default="{ row }">
            <el-tag type="info">{{ getCheckItemsCount(row.checkItems) }} 项</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="160">
          <template #default="{ row }">
            {{ formatDate(row.createTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link size="small" @click="viewTemplate(row)">
              查看
            </el-button>
            <el-button type="warning" link size="small" @click="editTemplate(row)">
              编辑
            </el-button>
            <el-button type="danger" link size="small" @click="deleteTemplate(row)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新建/编辑模板对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑模板' : '新建模板'"
      width="700px"
      destroy-on-close
    >
      <el-form 
        ref="formRef"
        :model="templateForm" 
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="模板名称" prop="name">
          <el-input v-model="templateForm.name" placeholder="请输入模板名称" />
        </el-form-item>
        
        <el-form-item label="检查类别" prop="category">
          <el-select v-model="templateForm.category" placeholder="请选择或输入类别" filterable allow-create style="width: 100%">
            <el-option label="日常检查" value="日常检查" />
            <el-option label="综合检查" value="综合检查" />
            <el-option label="专项检查" value="专项检查" />
          </el-select>
        </el-form-item>
        
        <el-form-item label="描述" prop="description">
          <el-input 
            v-model="templateForm.description" 
            type="textarea" 
            :rows="2"
            placeholder="请输入模板描述"
          />
        </el-form-item>
        
        <el-form-item label="检查项目">
          <div class="check-items-section">
            <div 
              class="check-item" 
              v-for="(item, index) in checkItems" 
              :key="index"
            >
              <el-input v-model="item.name" placeholder="检查项名称" style="width: 180px" />
              <el-input v-model="item.description" placeholder="描述" style="width: 200px" />
              <el-checkbox v-model="item.required">必填</el-checkbox>
              <el-button type="danger" link @click="removeCheckItem(index)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
            <el-button type="primary" link @click="addCheckItem">
              <el-icon><Plus /></el-icon>
              添加检查项
            </el-button>
          </div>
        </el-form-item>
        
        <el-form-item label="状态">
          <el-switch v-model="templateForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 模板详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      title="模板详情"
      width="600px"
    >
      <el-descriptions :column="2" border v-if="currentTemplate">
        <el-descriptions-item label="模板名称" :span="2">{{ currentTemplate.name }}</el-descriptions-item>
        <el-descriptions-item label="检查类别">{{ currentTemplate.category }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="currentTemplate.status === 1 ? 'success' : 'danger'">
            {{ currentTemplate.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ currentTemplate.description || '-' }}</el-descriptions-item>
      </el-descriptions>
      
      <div class="check-items-detail" v-if="currentTemplate">
        <h4>检查项目</h4>
        <el-table :data="parseCheckItems(currentTemplate.checkItems)" border size="small">
          <el-table-column prop="name" label="检查项" />
          <el-table-column prop="description" label="描述" />
          <el-table-column label="必填" width="80" align="center">
            <template #default="{ row }">
              <el-tag :type="row.required ? 'danger' : 'info'" size="small">
                {{ row.required ? '是' : '否' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>
      
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox, type FormInstance, type FormRules } from 'element-plus'
import { templateApi } from '@/api/template'
import type { TaskTemplate, CheckItem } from '@/types'

const loading = ref(false)
const submitLoading = ref(false)
const templateList = ref<TaskTemplate[]>([])

const dialogVisible = ref(false)
const detailVisible = ref(false)
const isEdit = ref(false)
const currentTemplate = ref<TaskTemplate | null>(null)
const editingId = ref<number | null>(null)

const formRef = ref<FormInstance>()
const templateForm = reactive({
  name: '',
  category: '',
  description: '',
  status: 1
})

const checkItems = ref<CheckItem[]>([])

const formRules: FormRules = {
  name: [{ required: true, message: '请输入模板名称', trigger: 'blur' }],
  category: [{ required: true, message: '请选择检查类别', trigger: 'change' }]
}

const fetchTemplates = async () => {
  loading.value = true
  try {
    const res = await templateApi.getAll()
    if (res.data) templateList.value = res.data
  } finally {
    loading.value = false
  }
}

const getCheckItemsCount = (checkItemsJson: string) => {
  try {
    const items = JSON.parse(checkItemsJson || '[]')
    return items.length
  } catch {
    return 0
  }
}

const parseCheckItems = (checkItemsJson: string): CheckItem[] => {
  try {
    return JSON.parse(checkItemsJson || '[]')
  } catch {
    return []
  }
}

const openCreateDialog = () => {
  isEdit.value = false
  editingId.value = null
  Object.assign(templateForm, {
    name: '',
    category: '',
    description: '',
    status: 1
  })
  checkItems.value = [{ id: 1, name: '', description: '', required: true }]
  dialogVisible.value = true
}

const viewTemplate = (template: TaskTemplate) => {
  currentTemplate.value = template
  detailVisible.value = true
}

const editTemplate = (template: TaskTemplate) => {
  isEdit.value = true
  editingId.value = template.id
  Object.assign(templateForm, {
    name: template.name,
    category: template.category,
    description: template.description,
    status: template.status
  })
  checkItems.value = parseCheckItems(template.checkItems)
  if (checkItems.value.length === 0) {
    checkItems.value = [{ id: 1, name: '', description: '', required: true }]
  }
  dialogVisible.value = true
}

const deleteTemplate = async (template: TaskTemplate) => {
  try {
    await ElMessageBox.confirm('确定删除该模板吗？', '警告', { type: 'warning' })
    await templateApi.delete(template.id)
    ElMessage.success('删除成功')
    fetchTemplates()
  } catch {
    // 取消
  }
}

const addCheckItem = () => {
  const maxId = checkItems.value.reduce((max, item) => Math.max(max, item.id), 0)
  checkItems.value.push({ id: maxId + 1, name: '', description: '', required: false })
}

const removeCheckItem = (index: number) => {
  checkItems.value.splice(index, 1)
}

const submitForm = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    // 过滤空的检查项
    const validItems = checkItems.value.filter(item => item.name.trim())
    
    submitLoading.value = true
    try {
      const data = {
        ...templateForm,
        checkItems: JSON.stringify(validItems)
      }
      
      if (isEdit.value && editingId.value) {
        await templateApi.update(editingId.value, data)
        ElMessage.success('更新成功')
      } else {
        await templateApi.create(data)
        ElMessage.success('创建成功')
      }
      dialogVisible.value = false
      fetchTemplates()
    } finally {
      submitLoading.value = false
    }
  })
}

const formatDate = (dateStr: string) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

onMounted(() => {
  fetchTemplates()
})
</script>

<style lang="scss" scoped>
.templates-page {
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  
  .check-items-section {
    width: 100%;
    
    .check-item {
      display: flex;
      align-items: center;
      gap: 12px;
      margin-bottom: 12px;
    }
  }
  
  .check-items-detail {
    margin-top: 20px;
    
    h4 {
      margin-bottom: 12px;
      color: #303133;
    }
  }
}
</style>
