import { createApp } from 'vue'
import { createPinia } from 'pinia'  // 导入 Pinia
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()  // 创建 Pinia 实例

// 注册 Element Plus
app.use(ElementPlus)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}

app.use(pinia)   // 使用 Pinia
app.use(router)  // 使用路由
app.mount('#app')