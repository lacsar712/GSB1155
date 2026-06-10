import axios from 'axios'
import type { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types'

let isHandlingAuthFailure = false

function handleAuthFailure(message?: string) {
  if (isHandlingAuthFailure) {
    return
  }

  isHandlingAuthFailure = true
  localStorage.removeItem('token')
  localStorage.removeItem('user')

  ElMessage.error(message || '登录状态已失效，请重新登录')

  if (window.location.pathname !== '/login') {
    window.location.replace('/login')
  }

  window.setTimeout(() => {
    isHandlingAuthFailure = false
  }, 1000)
}

// 创建axios实例
const service: AxiosInstance = axios.create({
  baseURL: '/api',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  (response: AxiosResponse<ApiResponse<any>>) => {
    const res = response.data
    
    // 如果code不是200，说明有错误
    if (res.code !== 200) {
      // 401: 未授权
      if (res.code === 401 || res.code === 403) {
        handleAuthFailure(res.message)
      } else {
        ElMessage.error(res.message || '请求失败')
      }
      
      return Promise.reject(new Error(res.message || '请求失败'))
    }
    
    return response.data
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络错误'

    if (error.response?.status === 401 || error.response?.status === 403) {
      handleAuthFailure(message)
    } else {
      ElMessage.error(message)
    }
    
    return Promise.reject(error)
  }
)

// 封装请求方法
export function get<T>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.get(url, config)
}

export function post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.post(url, data, config)
}

export function put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.put(url, data, config)
}

export function del<T>(url: string, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.delete(url, config)
}

export function patch<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<ApiResponse<T>> {
  return service.patch(url, data, config)
}

export default service
