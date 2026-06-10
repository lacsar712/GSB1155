<template>
  <el-container class="main-layout">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside">
      <div class="logo">
        <el-icon class="logo-icon"><Aim /></el-icon>
        <span v-show="!isCollapse" class="logo-text">蚁心安巡</span>
      </div>
      
      <el-menu
        :default-active="currentRoute"
        class="side-menu"
        :collapse="isCollapse"
        router
        background-color="#001529"
        text-color="#ffffffb3"
        active-text-color="#fff"
      >
        <el-menu-item index="/dashboard">
          <el-icon><HomeFilled /></el-icon>
          <template #title>工作台</template>
        </el-menu-item>
        
        <el-menu-item index="/tasks">
          <el-icon><List /></el-icon>
          <template #title>任务管理</template>
        </el-menu-item>
        
        <el-menu-item index="/templates" v-if="userStore.isAdmin || userStore.isManager">
          <el-icon><Document /></el-icon>
          <template #title>模板管理</template>
        </el-menu-item>
        
        <el-menu-item index="/organizations" v-if="userStore.isAdmin">
          <el-icon><OfficeBuilding /></el-icon>
          <template #title>组织管理</template>
        </el-menu-item>
        
        <el-menu-item index="/users" v-if="userStore.isAdmin || userStore.isManager">
          <el-icon><User /></el-icon>
          <template #title>用户管理</template>
        </el-menu-item>
      </el-menu>
    </el-aside>
    
    <el-container class="main-container">
      <!-- 头部 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon 
            class="collapse-btn" 
            @click="isCollapse = !isCollapse"
          >
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentTitle">{{ currentTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" class="avatar">
                {{ userStore.user?.realName?.charAt(0) || 'U' }}
              </el-avatar>
              <span class="user-name">{{ userStore.user?.realName || '用户' }}</span>
              <el-icon class="arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  <el-icon><User /></el-icon>
                  {{ userStore.user?.roleName }}
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon>
                  退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>
      
      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const currentRoute = computed(() => route.path)
const currentTitle = computed(() => {
  const meta = route.meta as { title?: string }
  return meta.title || ''
})

const handleCommand = async (command: string) => {
  if (command === 'logout') {
    try {
      await ElMessageBox.confirm('确定要退出登录吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      })
      userStore.logout()
      router.push('/login')
    } catch {
      // 取消操作
    }
  }
}
</script>

<style lang="scss" scoped>
.main-layout {
  height: 100vh;
  
  .aside {
    background: #001529;
    transition: width 0.3s;
    overflow: hidden;
    
    .logo {
      height: 60px;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      
      .logo-icon {
        font-size: 28px;
        color: #409eff;
      }
      
      .logo-text {
        font-size: 18px;
        font-weight: bold;
        margin-left: 10px;
        white-space: nowrap;
      }
    }
    
    .side-menu {
      border-right: none;
      
      :deep(.el-menu-item) {
        &:hover {
          background-color: #000c17 !important;
        }
        
        &.is-active {
          background: linear-gradient(90deg, #1890ff 0%, #096dd9 100%);
        }
      }
    }
  }
  
  .main-container {
    background: #f0f2f5;
    
    .header {
      background: #fff;
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 20px;
      box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
      
      .header-left {
        display: flex;
        align-items: center;
        gap: 16px;
        
        .collapse-btn {
          font-size: 20px;
          cursor: pointer;
          transition: color 0.3s;
          
          &:hover {
            color: #409eff;
          }
        }
      }
      
      .header-right {
        .user-info {
          display: flex;
          align-items: center;
          gap: 8px;
          cursor: pointer;
          padding: 4px 8px;
          border-radius: 4px;
          transition: background 0.3s;
          
          &:hover {
            background: #f5f5f5;
          }
          
          .avatar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
          }
          
          .user-name {
            color: #333;
            font-size: 14px;
          }
          
          .arrow {
            color: #999;
            font-size: 12px;
          }
        }
      }
    }
    
    .main-content {
      padding: 20px;
      overflow-y: auto;
    }
  }
}

@media (max-width: 768px) {
  .main-layout {
    .aside {
      position: fixed;
      z-index: 100;
      height: 100%;
    }
    
    .main-container {
      margin-left: 0;
    }
  }
}
</style>
