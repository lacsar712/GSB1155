import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api/auth'
import type { User, LoginRequest } from '@/types'

function readCachedUser(): User | null {
  const cachedUser = localStorage.getItem('user')
  if (!cachedUser) {
    return null
  }

  try {
    return JSON.parse(cachedUser) as User
  } catch {
    localStorage.removeItem('user')
    return null
  }
}

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem('token') || '')
  const user = ref<User | null>(readCachedUser())

  const isAuthenticated = computed(() => !!token.value)
  
  const isAdmin = computed(() => user.value?.roleType === 1)
  const isManager = computed(() => user.value?.roleType === 2)
  const isInspector = computed(() => user.value?.roleType === 3)

  async function login(credentials: LoginRequest) {
    const response = await authApi.login(credentials)
    if (response.code === 200 && response.data) {
      token.value = response.data.token
      user.value = response.data.user
      localStorage.setItem('token', response.data.token)
      localStorage.setItem('user', JSON.stringify(response.data.user))
      return true
    }
    throw new Error(response.message || '登录失败')
  }

  async function fetchCurrentUser() {
    if (!token.value) return
    try {
      const response = await authApi.getCurrentUser()
      if (response.code === 200 && response.data) {
        user.value = response.data
        localStorage.setItem('user', JSON.stringify(response.data))
      }
    } catch (error) {
      logout()
    }
  }

  function logout() {
    token.value = ''
    user.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  }

  // 初始化时获取用户信息
  if (token.value && !user.value) {
    fetchCurrentUser()
  }

  return {
    token,
    user,
    isAuthenticated,
    isAdmin,
    isManager,
    isInspector,
    login,
    logout,
    fetchCurrentUser
  }
})
