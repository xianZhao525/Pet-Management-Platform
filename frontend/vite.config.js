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
    // 关键配置：所有路由都回退到 index.html，解决 history 模式刷新 404 问题
    historyApiFallback: true,
    proxy: {
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
      '/adoption': {
        target: 'http://localhost:8080',
        changeOrigin: true
      },
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