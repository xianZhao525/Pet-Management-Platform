import { fileURLToPath, URL } from 'node:url'
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    port: 5173,
    // 关键配置：所有路由都回退到 index.html
    historyApiFallback: true,
    proxy: {
      // 只代理 API 请求，不代理页面路由
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/user': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/pet': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/admin': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      // ❌ 删除 '/adoption' 代理，让前端路由处理
      // '/adoption': {
      //   target: 'http://localhost:8080',
      //   changeOrigin: true
      // },
      '/donation': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/foster': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
      '/test': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})