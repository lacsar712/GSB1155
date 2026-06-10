const { post } = require('../../utils/request')
const { sha256 } = require('../../utils/password')

Page({
  data: {
    username: '',
    password: '',
    loading: false,
    accountOptions: [
      { label: '管理员', username: 'admin' },
      { label: '单位管理员', username: 'school1_admin' },
      { label: '巡检员', username: 'inspector1' }
    ]
  },

  onShow() {
    if (wx.getStorageSync('token')) {
      wx.reLaunch({ url: '/pages/tasks/index' })
    }
  },

  handleInput(event) {
    const { field } = event.currentTarget.dataset
    this.setData({
      [field]: event.detail.value
    })
  },

  fillUsername(event) {
    this.setData({
      username: event.currentTarget.dataset.username
    })
  },

  async submitLogin() {
    const { username, password, loading } = this.data
    if (loading) return

    if (!username.trim() || !password) {
      wx.showToast({
        title: '请输入用户名和密码',
        icon: 'none'
      })
      return
    }

    this.setData({ loading: true })

    try {
      const response = await post(
        '/auth/login',
        {
          username: username.trim(),
          passwordHash: sha256(password)
        },
        { auth: false }
      )

      wx.setStorageSync('token', response.data.token)
      wx.setStorageSync('user', response.data.user)
      getApp().globalData.user = response.data.user

      wx.showToast({
        title: '登录成功',
        icon: 'success'
      })

      wx.reLaunch({ url: '/pages/tasks/index' })
    } catch (error) {
      wx.showToast({
        title: error.message || '登录失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  }
})
