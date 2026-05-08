// 基础配置：后端运行在 8081 端口
// 注意：在微信小程序模拟器中可以使用 localhost，真机调试需更换为电脑 IP
const BASE_URL = 'https://quizgo.gala-lin.top'; 

/**
 * 通用请求封装
 * @param {Object} options - 请求配置
 */
export const request = (options) => {
  return new Promise((resolve, reject) => {
    // 1. 拼接 URL
    const url = options.url.startsWith('http') ? options.url : BASE_URL + options.url;

    // 2. 从本地缓存获取登录时保存的 Token
    const token = uni.getStorageSync('token') || '';

    uni.request({
      url: url,
      method: options.method || 'GET',
      data: options.data || {},
      header: {
        'Content-Type': 'application/json',
        // 对应文档中要求的 Authorization header
        'Authorization': token, 
        ...options.header
      },
      success: (res) => {
        // 微信小程序中 res.statusCode 为 HTTP 状态码
        if (res.statusCode === 200) {
          // 根据文档，业务逻辑成功 code 通常为 200
          if (res.data.code === 200 || !res.data.code) {
            // 直接返回 data 部分，简化页面调用
            resolve(res.data.data || res.data);
          } else {
            // 处理业务错误（如：Token过期、权限不足）
            uni.showToast({
              title: res.data.message || '业务逻辑错误',
              icon: 'none'
            });
            reject(res.data);
          }
        } else if (res.statusCode === 401) {
          // 未授权，跳转登录
          uni.showToast({ title: '请先登录', icon: 'none' });
          setTimeout(() => {
            uni.navigateTo({ url: '/pages/login/login' });
          }, 1000);
          reject(res);
        } else {
          uni.showToast({
            title: `服务器错误(${res.statusCode})`,
            icon: 'none'
          });
          reject(res);
        }
      },
      fail: (err) => {
        uni.showToast({
          title: '网络连接失败，请检查后端服务是否启动',
          icon: 'none'
        });
        reject(err);
      }
    });
  });
};