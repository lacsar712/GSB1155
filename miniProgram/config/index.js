const ENV_BASE_URLS = {
  develop: 'http://127.0.0.1:8000/api',
  trial: 'https://your-domain.example.com/api',
  release: 'https://your-domain.example.com/api'
}

function getEnvVersion() {
  try {
    const accountInfo = wx.getAccountInfoSync()
    return (accountInfo && accountInfo.miniProgram && accountInfo.miniProgram.envVersion) || 'develop'
  } catch (error) {
    return 'develop'
  }
}

function getBaseUrl() {
  const customBaseUrl = wx.getStorageSync('baseUrlOverride')
  if (customBaseUrl) {
    return customBaseUrl
  }

  const envVersion = getEnvVersion()
  return ENV_BASE_URLS[envVersion] || ENV_BASE_URLS.develop
}

module.exports = {
  ENV_BASE_URLS,
  getBaseUrl,
  getEnvVersion
}
