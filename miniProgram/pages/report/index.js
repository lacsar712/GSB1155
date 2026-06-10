const { post } = require('../../utils/request')

Page({
  data: {
    taskId: '',
    title: '',
    location: '',
    reportContent: '',
    checkResultText: '',
    imageText: '',
    loading: false
  },

  onLoad(query) {
    if (!query.taskId) {
      wx.showToast({
        title: '任务参数缺失',
        icon: 'none'
      })
      wx.navigateBack()
      return
    }

    this.setData({
      taskId: query.taskId,
      title: decodeURIComponent(query.title || '')
    })
  },

  handleInput(event) {
    const { field } = event.currentTarget.dataset
    this.setData({
      [field]: event.detail.value
    })
  },

  normalizeImages() {
    const { imageText } = this.data
    const list = imageText
      .split(/\n|,/)
      .map((item) => item.trim())
      .filter(Boolean)

    return list.length ? JSON.stringify(list) : ''
  },

  async submitReport() {
    const { taskId, location, reportContent, checkResultText, loading } = this.data
    if (loading) return

    if (!location.trim() || !reportContent.trim() || !checkResultText.trim()) {
      wx.showToast({
        title: '请填写完整上报信息',
        icon: 'none'
      })
      return
    }

    this.setData({ loading: true })

    try {
      await post('/task-reports', {
        taskId: Number(taskId),
        location: location.trim(),
        reportContent: reportContent.trim(),
        images: this.normalizeImages(),
        checkResult: JSON.stringify([
          {
            itemName: '巡检摘要',
            remark: checkResultText.trim()
          }
        ])
      })

      wx.showToast({
        title: '上报成功',
        icon: 'success'
      })

      setTimeout(() => {
        wx.navigateBack()
      }, 600)
    } catch (error) {
      wx.showToast({
        title: error.message || '上报失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  }
})
