<template>
  <footer class="footer">
    <div class="container">
      <div class="row">
        <div class="col-md-4">
          <h5>
            <i class="fas fa-paw"></i> 
            宠物领养平台
          </h5>
          <p class="text-muted">
            我们致力于为流浪宠物寻找温暖的家，通过领养代替购买，减少流浪动物的数量。
          </p>
        </div>
        
        <div class="col-md-4">
          <h5>快速链接</h5>
          <ul class="list-unstyled">
            <li v-for="link in quickLinks" :key="link.path">
              <a 
                :href="link.path" 
                class="text-muted"
                @click.prevent="navigateTo(link.path)"
              >
                {{ link.name }}
              </a>
            </li>
          </ul>
        </div>
        
        <div class="col-md-4">
          <h5>联系我们</h5>
          <div class="contact-info">
            <p class="text-muted">
              <i class="fas fa-heart me-2" style="color: #ff6b6b;"></i> 
              每一个毛孩子都想要一个可以遮风挡雨的家
            </p>
            <p class="text-muted">
              <i class="fas fa-envelope me-2"></i> 
              contact@petadoption.com
            </p>
            <p class="text-muted">
              <i class="fas fa-map-marker-alt me-2"></i> 
              用领养代替购买，用爱心代替抛弃
            </p>
          </div>
        </div>
      </div>
      
      <hr />
      
      <div class="text-center text-muted">
        <p>
          &copy; {{ currentYear }} 宠物领养平台. 版权所有.
          <span class="mx-2">|</span>
          <a href="#" class="text-muted" @click.prevent="goToPrivacy">隐私政策</a>
          <span class="mx-2">|</span>
          <a href="#" class="text-muted" @click.prevent="goToTerms">使用条款</a>
        </p>
        <p class="small">
          <i class="fas fa-shield-alt me-1"></i> 
          我们承诺：每一只宠物都经过健康检查，确保它们能健康快乐地进入新家庭
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRouter } from 'vue-router'

    const router = useRouter()

    // 当前年份（自动更新）
    const currentYear = ref(new Date().getFullYear())

    // 快速链接配置
    const quickLinks = ref([
    { name: '首页', path: '/' },
    { name: '领养宠物', path: '/pet/list' },
    { name: '关于我们', path: '/about' },
    { name: '联系我们', path: '/contact' }
    ])

    // 导航方法
    const navigateTo = (path) => {
    router.push(path)
    }

    const goToPrivacy = () => {
    router.push('/privacy')
    }

    const goToTerms = () => {
    router.push('/terms')
    }

    // 可选：添加滚动显示效果（原JSP中的滚动效果已移到AppHeader中）
    // 如果你想保留footer的显示效果，可以添加
    onMounted(() => {
    // 页脚淡入效果
    const footer = document.querySelector('.footer')
    if (footer) {
        footer.style.opacity = '0'
        footer.style.transform = 'translateY(20px)'
        setTimeout(() => {
        footer.style.transition = 'all 0.5s ease'
        footer.style.opacity = '1'
        footer.style.transform = 'translateY(0)'
        }, 100)
    }
    })
</script>

<style scoped>
    .footer {
      background: #2c3e50;
      color: #ecf0f1;
      padding: 60px 0 20px;
      margin-top: 60px;
    }

    /* 新增：让三栏容器水平居中且均匀分布 */
    .footer .row {
      display: flex;
      flex-wrap: wrap;
      justify-content: space-between;
      max-width: 1200px;
      margin: 0 auto;
    }

    .footer .col-md-4 {
      flex: 1;
      min-width: 250px;
      padding: 0 15px;
      text-align: center;  /* 内容居中，可根据喜好改为 left */
    }

    /* 响应式 */
    @media (max-width: 768px) {
      .footer .row {
        flex-direction: column;
        align-items: center;
      }
      .footer .col-md-4 {
        text-align: center;
        margin-bottom: 30px;
        width: 100%;
      }
    .footer ul li a:hover {
        transform: translateX(0);
    }
    
    .contact-info i {
        width: auto;
        margin-right: 8px;
        }
    }

    /* 添加一个可爱的小爪印动画效果 */
    .footer .fas.fa-paw {
    animation: pawWave 2s ease-in-out infinite;
    display: inline-block;
    }

    @keyframes pawWave {
        0%, 100% {
            transform: rotate(0deg);
        }
        25% {
            transform: rotate(10deg);
        }
        75% {
            transform: rotate(-10deg);
        }
    }
</style>