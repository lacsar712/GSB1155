const { getBaseUrl } = require('../config/index')

function request(options) {
  const token = wx.getStorageSync('token')
  const { url, method = 'GET', data, auth = true } = options

  return new Promise((resolve, reject) => {
    const baseUrl = getBaseUrl()

    wx.request({
      url: `${baseUrl}${url}`,
      method,
      data,
      timeout: 30000,
      header: {
        'content-type': 'application/json',
        ...(auth && token ? { Authorization: `Bearer ${token}` } : {})
      },
      success(res) {
        const { statusCode, data: body } = res
        if (statusCode === 401 || (body && body.code === 401)) {
          wx.removeStorageSync('token')
          wx.removeStorageSync('user')
          wx.reLaunch({ url: '/pages/login/index' })
          reject(new Error((body && body.message) || '登录已失效'))
          return
        }

        if (statusCode >= 200 && statusCode < 300 && body && body.code === 200) {
          resolve(body)
          return
        }

        reject(new Error((body && body.message) || '请求失败'))
      },
      fail(error) {
        reject(error)
      }
    })
  })
}

function get(url, options = {}) {
  return request({ ...options, url, method: 'GET' })
}

function post(url, data, options = {}) {
  return request({ ...options, url, data, method: 'POST' })
}

function patch(url, data, options = {}) {
  return request({ ...options, url, data, method: 'PATCH' })
}

module.exports = {
  request,
  get,
  post,
  patch
}
