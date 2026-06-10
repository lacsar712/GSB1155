<template>
  <div class="login-container">
    <div class="login-bg">
      <div class="bg-shape shape-1"></div>
      <div class="bg-shape shape-2"></div>
      <div class="bg-shape shape-3"></div>
    </div>
    
    <div class="login-card">
      <div class="login-header">
        <el-icon class="logo-icon"><Aim /></el-icon>
        <h1 class="title">蚁心安巡</h1>
        <p class="subtitle">学校安全检查管理平台</p>
      </div>
      
      <el-form 
        ref="formRef"
        :model="loginForm" 
        :rules="rules"
        class="login-form"
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            :prefix-icon="UserIcon"
          />
        </el-form-item>
        
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            :prefix-icon="Lock"
            show-password
            @keyup.enter="handleLogin"
          />
        </el-form-item>
        
        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '登 录' }}
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User as UserIcon, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { sha256Hex } from '@/utils/password'

const router = useRouter()
const userStore = useUserStore()

const formRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: '',
  password: ''
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const fillAccount = (username: string) => {
  loginForm.username = username
}

const handleLogin = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    
    loading.value = true
    try {
      await userStore.login({
        username: loginForm.username,
        passwordHash: await sha256Hex(loginForm.password)
      })
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } catch (error: any) {
      ElMessage.error(error.message || '登录失败')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
  
  .login-bg {
    position: absolute;
    inset: 0;
    
    .bg-shape {
      position: absolute;
      border-radius: 50%;
      background: rgba(255, 255, 255, 0.1);
      animation: float 20s infinite ease-in-out;
    }
    
    .shape-1 {
      width: 400px;
      height: 400px;
      top: -100px;
      left: -100px;
    }
    
    .shape-2 {
      width: 300px;
      height: 300px;
      bottom: -50px;
      right: -50px;
      animation-delay: -5s;
    }
    
    .shape-3 {
      width: 200px;
      height: 200px;
      top: 50%;
      left: 50%;
      animation-delay: -10s;
    }
  }
  
  .login-card {
    width: 420px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
    position: relative;
    z-index: 1;
    backdrop-filter: blur(10px);
    
    .login-header {
      text-align: center;
      margin-bottom: 36px;
      
      .logo-icon {
        font-size: 48px;
        color: #667eea;
        margin-bottom: 16px;
      }
      
      .title {
        font-size: 28px;
        font-weight: bold;
        color: #333;
        margin: 0 0 8px 0;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
      
      .subtitle {
        font-size: 14px;
        color: #999;
        margin: 0;
      }
    }
    
    .login-form {
      .el-input {
        :deep(.el-input__wrapper) {
          border-radius: 8px;
          box-shadow: 0 0 0 1px #dcdfe6 inset;
          
          &:hover, &:focus {
            box-shadow: 0 0 0 1px #667eea inset;
          }
        }
      }
      
      .login-btn {
        width: 100%;
        height: 44px;
        border-radius: 8px;
        font-size: 16px;
        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        border: none;
        
        &:hover {
          opacity: 0.9;
        }
      }
    }
    
    .login-footer {
      margin-top: 20px;
      
      .el-divider {
        :deep(.el-divider__text) {
          color: #999;
          font-size: 12px;
        }
      }
      
      .test-accounts {
        display: flex;
        flex-direction: column;
        gap: 8px;
        
        .el-tag {
          cursor: pointer;
          justify-content: center;
          transition: transform 0.2s;
          
          &:hover {
            transform: scale(1.02);
          }
        }
      }

      .account-tip {
        margin: 12px 0 0;
        font-size: 12px;
        line-height: 1.6;
        color: #909399;
        text-align: center;
      }
    }
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@media (max-width: 480px) {
  .login-container {
    padding: 20px;
    
    .login-card {
      width: 100%;
      padding: 24px;
    }
  }
}
</style>
