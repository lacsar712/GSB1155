const { get, patch } = require('../../utils/request')

const STATUS_LABELS = {
  0: '待执行',
  1: '执行中',
  2: '待审核',
  3: '已完成',
  4: '已驳回'
}

const STATUS_FILTERS = [
  { value: 'all', label: '全部' },
  { value: 0, label: '待执行' },
  { value: 1, label: '执行中' },
  { value: 2, label: '待审核' },
  { value: 4, label: '已驳回' }
]

Page({
  data: {
    loading: false,
    tasks: [],
    filteredTasks: [],
    activeFilter: 'all',
    filters: STATUS_FILTERS,
    user: {}
  },

  onShow() {
    const token = wx.getStorageSync('token')
    const user = wx.getStorageSync('user')

    if (!token) {
      wx.reLaunch({ url: '/pages/login/index' })
      return
    }

    this.setData({ user })
    this.fetchTasks()
  },

  onPullDownRefresh() {
    this.fetchTasks().finally(() => {
      wx.stopPullDownRefresh()
    })
  },

  applyFilter(tasks = this.data.tasks, activeFilter = this.data.activeFilter) {
    const filteredTasks =
      activeFilter === 'all'
        ? tasks
        : tasks.filter((task) => Number(task.status) === Number(activeFilter))

    this.setData({ filteredTasks })
  },

  async fetchTasks() {
    this.setData({ loading: true })
    try {
      const response = await get('/tasks/my')
      const tasks = (response.data || []).map((task) => ({
        ...task,
        statusText: STATUS_LABELS[task.status] || task.statusName || '未知状态'
      }))

      this.setData({ tasks })
      this.applyFilter(tasks)
    } catch (error) {
      wx.showToast({
        title: error.message || '获取任务失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  changeFilter(event) {
    const activeFilter = event.currentTarget.dataset.filter
    this.setData({ activeFilter })
    this.applyFilter(this.data.tasks, activeFilter)
  },

  async startTask(event) {
    const { id } = event.currentTarget.dataset
    try {
      await patch(`/tasks/${id}/status`, { status: 1 })
      wx.showToast({
        title: '任务已开始',
        icon: 'success'
      })
      this.fetchTasks()
    } catch (error) {
      wx.showToast({
        title: error.message || '操作失败',
        icon: 'none'
      })
    }
  },

  goReport(event) {
    const { id, title } = event.currentTarget.dataset
    wx.navigateTo({
      url: `/pages/report/index?taskId=${id}&title=${encodeURIComponent(title)}`
    })
  },

  logout() {
    wx.removeStorageSync('token')
    wx.removeStorageSync('user')
    getApp().globalData.user = null
    wx.reLaunch({ url: '/pages/login/index' })
  }
})
