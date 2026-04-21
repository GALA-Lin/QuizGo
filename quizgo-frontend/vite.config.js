import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path' // 导入 path 模块

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      // 设置 @ 符号指向 src 目录
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    port: 5173,
    host: '0.0.0.0'
  }
})