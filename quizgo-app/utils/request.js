const BASE_URL = 'http://localhost:8080'

export function request(options) {
  return new Promise((resolve, reject) => {
    uni.request({
      url: BASE_URL + options.url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        Authorization: 'Bearer ' + (uni.getStorageSync('token') || ''),
        'Content-Type': 'application/json'
      },
      success: (res) => {
        const body = res.data || {}
        if (body.code === 200) {
          resolve(body.data)
        } else {
          uni.showToast({
            title: body.msg || '请求失败',
            icon: 'none'
          })
          reject(body)
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络错误',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

export default request
