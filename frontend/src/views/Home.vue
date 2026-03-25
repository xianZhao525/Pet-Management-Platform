<template>
  <div class="app">
    <!-- 页面特有样式 -->
    <section class="hero fade-in-up">
      <div class="container">
        <h1>给流浪宠物一个温暖的家</h1>
        <p>我们连接爱心人士与需要帮助的宠物，让领养变得简单、可靠。每一只宠物都值得被爱，每一次领养都是拯救一个生命。</p>
        <div style="margin-top: 40px;">
          <el-button type="primary" size="large" @click="navigateTo('/adoption/petAdoption')" class="hero-btn">
            浏览待领养宠物
          </el-button>
          <el-button size="large" @click="navigateTo('/donation/charityDonation')" class="hero-btn-secondary">
            爱心捐赠
          </el-button>
        </div>
      </div>
    </section>

    <section class="services-section">
      <div class="container">
        <h2 class="section-title">我们的服务</h2>
        <div class="services-grid">
          <div 
            v-for="(service, index) in services" 
            :key="service.title"
            class="service-card fade-in-up"
            :style="{ animationDelay: `${0.1 + index * 0.1}s` }"
          >
            <div class="service-icon">
              <i :class="service.icon"></i>
            </div>
            <h3>{{ service.title }}</h3>
            <p>{{ service.description }}</p>
            <a href="#" class="view-details" @click.prevent="navigateTo(service.link)">了解更多</a>
          </div>
        </div>
      </div>
    </section>

    <section class="pets-section">
      <div class="container">
        <h2 class="section-title">宠物品种介绍</h2>
        <p class="section-subtitle">
          了解不同宠物品种的特点，选择最适合您家庭的新成员
        </p>
        
        <div class="pets-grid" ref="petsGrid">
          <div 
            v-for="pet in pets" 
            :key="pet.name"
            class="pet-card fade-in-up"
            @mouseenter="handleCardHover($event, true)"
            @mouseleave="handleCardHover($event, false)"
          >
            <div class="pet-image-container">
              <img :src="pet.image" :alt="pet.name" class="pet-image" @error="handleImageError">
            </div>
            <div class="pet-info">
              <h3 class="pet-name">{{ pet.name }}</h3>
              <div class="pet-meta">
                <span class="pet-type" :class="pet.typeClass">{{ pet.type }}</span>
                <span class="pet-age"><i class="fas fa-ruler-combined"></i> {{ pet.size }}</span>
              </div>
              <div class="breed-info">
                <div 
                  v-for="feature in pet.features" 
                  :key="feature"
                  class="breed-feature"
                >
                  <i class="fas fa-heart"></i>
                  <span>{{ feature }}</span>
                </div>
              </div>
              <p class="pet-description">{{ pet.description }}</p>
            </div>
          </div>
        </div>
        
        <div class="view-more">
          <el-button type="primary" @click="navigateTo('/adoption/petAdoption')">
            查看待领养宠物
          </el-button>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
  import { onMounted, onUnmounted } from 'vue'
  import { useRouter } from 'vue-router'
  //import { Search, HeartFilled, Paw } from '@element-plus/icons-vue'

  const router = useRouter()

  // 服务数据
  const services = [
    {
      title: '宠物领养',
      description: '为流浪动物寻找永久的家，提供完整的领养流程支持。',
      icon: 'fas fa-home',
      link: '/adoption/petAdoption'
    },
    {
      title: '临时寄养',
      description: '为宠物主人提供可靠的临时寄养服务。',
      icon: 'fas fa-hands-helping',
      link: '/foster/temporaryFosterCare'
    },
    {
      title: '爱心捐赠',
      description: '支持我们的救助工作，帮助更多小生命。',
      icon: 'fas fa-donate',
      link: '/donation/charityDonation'
    }
  ]

  // 宠物数据
  const pets = [
    {
      name: '金毛寻回犬',
      type: '狗狗',
      typeClass: 'dog',
      size: '中大型犬',
      image: '/images/pets/golden_retriever.jpg',
      features: ['性格特点：友善温顺，聪明忠诚', '适合家庭：有孩子的家庭', '运动需求：每天1-2小时'],
      description: '被称为"暖男"的金毛，是极受欢迎的家庭伴侣犬，对孩子非常友好。'
    },
    {
      name: '哈士奇',
      type: '狗狗',
      typeClass: 'dog',
      size: '中大型犬',
      image: '/images/pets/husky.jpg',
      features: ['性格特点：活泼好动，独立有个性', '适合家庭：有院子的家庭', '运动需求：每天2小时以上'],
      description: '精力充沛的"二哈"，需要大量运动和训练，不适合新手饲养。'
    },
    {
      name: '萨摩耶',
      type: '狗狗',
      typeClass: 'dog',
      size: '中大型犬',
      image: '/images/pets/samoyed.jpg',
      features: ['性格特点：温和友善，微笑天使', '适合家庭：有时间的家庭', '特殊需求：需要定期美容'],
      description: '拥有招牌"萨摩耶微笑"的美丽犬种，性格温和但需要精心打理毛发。'
    },
    {
      name: '垂耳兔',
      type: '兔子',
      typeClass: 'rabbit',
      size: '小型宠物',
      image: '/images/pets/lop_rabbit.jpg',
      features: ['性格特点：温顺安静，胆小敏感', '饲养环境：室内笼养', '饮食需求：干草+蔬菜+兔粮'],
      description: '可爱的垂耳兔是理想的室内宠物，需要精心照料和定期的健康检查。'
    },
    {
      name: '布偶猫',
      type: '猫咪',
      typeClass: 'cat',
      size: '中大型猫',
      image: '/images/pets/ragdoll_cat.jpg',
      features: ['性格特点：温顺粘人，像布偶一样', '适合家庭：所有家庭', '特殊需求：需要梳理长毛'],
      description: '被称为"小狗猫"的布偶，性格极其温顺，喜欢跟随主人。'
    },
    {
      name: '橘猫',
      type: '猫咪',
      typeClass: 'cat',
      size: '中型猫',
      image: '/images/pets/orange_cat.jpg',
      features: ['性格特点：活泼亲人，贪吃爱睡', '适合环境：适应力强', '饮食注意：控制食量防肥胖'],
      description: '十只橘猫九只胖，还有一只特别胖。性格通常很友好，容易相处。'
    }
  ]

  const navigateTo = (path) => {
    router.push(path)
  }

  const handleImageError = (event) => {
    event.target.src = '/images/pets/default.jpg'
  }

  const handleCardHover = (event, isEnter) => {
    const card = event.currentTarget
    if (isEnter) {
      card.style.transform = 'translateY(-10px)'
      card.style.boxShadow = '0 15px 30px rgba(0, 0, 0, 0.15)'
    } else {
      card.style.transform = 'translateY(0)'
      card.style.boxShadow = '0 5px 15px rgba(0, 0, 0, 0.08)'
    }
  }

  // 滚动淡入效果
  const handleScrollFadeIn = () => {
    const fadeElements = document.querySelectorAll('.fade-in-up')
    const windowHeight = window.innerHeight
    const elementVisible = 150
    
    fadeElements.forEach(element => {
      const elementTop = element.getBoundingClientRect().top
      if (elementTop < windowHeight - elementVisible) {
        element.classList.add('active')
      }
    })
  }

  onMounted(() => {
    // 初始调用
    handleScrollFadeIn()
    // 添加滚动监听
    window.addEventListener('scroll', handleScrollFadeIn)
  })

  onUnmounted(() => {
    window.removeEventListener('scroll', handleScrollFadeIn)
  })
</script>

<style scoped>
  /* 动画 */
  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(30px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }

  .fade-in-up {
    opacity: 0;
    transform: translateY(30px);
    transition: opacity 0.6s ease, transform 0.6s ease;
  }

  .fade-in-up.active {
    opacity: 1;
    transform: translateY(0);
  }

  /* Hero 区域 */
  .hero {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    text-align: center;
    padding: 100px 20px;
    margin-top: -20px;
  }

  .hero h1 {
    font-size: 3rem;
    margin-bottom: 20px;
    font-weight: 700;
  }

  .hero p {
    font-size: 1.2rem;
    max-width: 800px;
    margin: 0 auto 30px;
    opacity: 0.9;
  }

  .hero-btn {
    background-color: white;
    color: #667eea;
    border: none;
    padding: 12px 35px;
    font-size: 1.1rem;
  }

  .hero-btn-secondary {
    background-color: transparent;
    color: white;
    border: 2px solid white;
    padding: 12px 35px;
    font-size: 1.1rem;
    margin-left: 15px;
  }

  .hero-btn-secondary:hover {
    background-color: white;
    color: #667eea;
  }

  /* 服务区域整体背景 */
  .services-section {
    padding: 80px 0;
    background: linear-gradient(145deg, #fafbff 0%, #f5f3ff 100%);
    position: relative;
    overflow: hidden;
  }

  /* 装饰性背景光晕 */
  .services-section::before {
    content: '';
    position: absolute;
    width: 300px;
    height: 300px;
    background: radial-gradient(circle, rgba(102, 126, 234, 0.1) 0%, transparent 70%);
    top: -100px;
    right: -100px;
    border-radius: 50%;
    pointer-events: none;
  }

  .services-section::after {
    content: '';
    position: absolute;
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, rgba(118, 75, 162, 0.08) 0%, transparent 70%);
    bottom: -150px;
    left: -150px;
    border-radius: 50%;
    pointer-events: none;
  }

  /* 标题样式升级 */
  .section-title {
    text-align: center;
    font-size: 2.8rem;
    margin-bottom: 20px;
    color: #1e1e2f;
    position: relative;
    font-weight: 700;
  }

  .section-title::after {
    content: '';
    position: absolute;
    bottom: -15px;
    left: 50%;
    transform: translateX(-50%);
    width: 80px;
    height: 4px;
    background: linear-gradient(90deg, #667eea, #764ba2);
    border-radius: 4px;
  }

  /* 服务网格 */
  .services-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 40px;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
    position: relative;
    z-index: 2;
  }

  /* 卡片玻璃态设计 */
  .service-card {
    background: rgba(255, 255, 255, 0.7);
    backdrop-filter: blur(10px);
    border-radius: 28px;
    padding: 40px 30px;
    text-align: center;
    transition: all 0.4s cubic-bezier(0.2, 0.9, 0.4, 1.1);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(255, 255, 255, 0.8);
    position: relative;
    overflow: hidden;
  }

  /* 卡片悬停效果 */
  .service-card:hover {
    transform: translateY(-12px);
    box-shadow: 0 25px 40px rgba(102, 126, 234, 0.2);
    background: rgba(255, 255, 255, 0.9);
    border-color: rgba(102, 126, 234, 0.3);
  }

  /* 卡片装饰光晕（悬停时出现） */
  .service-card::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: linear-gradient(120deg, transparent, rgba(102, 126, 234, 0.05), transparent);
    transform: translateX(-100%);
    transition: transform 0.6s;
    pointer-events: none;
  }

  .service-card:hover::before {
    transform: translateX(100%);
  }

  /* 图标区域 */
  .service-icon {
    width: 90px;
    height: 90px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 0 auto 25px;
    transition: all 0.3s ease;
    box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
  }

  .service-card:hover .service-icon {
    transform: scale(1.05);
    box-shadow: 0 15px 30px rgba(102, 126, 234, 0.4);
  }

  .service-icon i {
    font-size: 40px;
    color: white;
    transition: transform 0.3s;
  }

  .service-card:hover .service-icon i {
    transform: scale(1.1);
  }

  /* 卡片文字 */
  .service-card h3 {
    font-size: 1.6rem;
    margin-bottom: 15px;
    color: #1e1e2f;
    font-weight: 600;
  }

  .service-card p {
    color: #5a5a6e;
    line-height: 1.6;
    margin-bottom: 25px;
    font-size: 0.95rem;
  }

  /* 了解更多链接 */
  .view-details {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: #667eea;
    text-decoration: none;
    font-weight: 600;
    font-size: 0.95rem;
    padding: 8px 20px;
    border-radius: 40px;
    background: rgba(102, 126, 234, 0.1);
    transition: all 0.3s ease;
  }

  .view-details:hover {
    background: #667eea;
    color: white;
    gap: 12px;
    box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
  }

  .view-details i {
    transition: transform 0.2s;
  }

  .view-details:hover i {
    transform: translateX(4px);
  }

  /* 响应式适配 */
  @media (max-width: 992px) {
    .services-grid {
      gap: 30px;
    }
    .service-card {
      padding: 30px 20px;
    }
    .service-icon {
      width: 80px;
      height: 80px;
    }
    .service-icon i {
      font-size: 34px;
    }
  }

  @media (max-width: 768px) {
    .section-title {
      font-size: 2rem;
    }
    .service-card {
      padding: 25px 20px;
    }
    .service-icon {
      width: 70px;
      height: 70px;
    }
    .service-icon i {
      font-size: 30px;
    }
  }

  .view-details {
    color: #667eea;
    text-decoration: none;
    font-weight: 500;
    transition: color 0.3s;
  }

  .view-details:hover {
    color: #764ba2;
  }

  /* 宠物区域 */
  .pets-section {
    padding: 80px 0;
    background-color: white;
  }

  .section-subtitle {
    text-align: center;
    color: #666;
    margin-bottom: 40px;
    max-width: 800px;
    margin-left: auto;
    margin-right: auto;
  }

  .pets-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
    gap: 30px;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .pet-card {
    background: white;
    border-radius: 12px;
    overflow: hidden;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
    transition: all 0.3s ease;
  }

  .pet-image-container {
    height: 250px;
    overflow: hidden;
  }

  .pet-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    transition: transform 0.3s ease;
  }

  .pet-card:hover .pet-image {
    transform: scale(1.05);
  }

  .pet-info {
    padding: 20px;
  }

  .pet-name {
    font-size: 1.3rem;
    margin-bottom: 10px;
    color: #333;
  }

  .pet-meta {
    display: flex;
    gap: 10px;
    margin-bottom: 15px;
  }

  .pet-type {
    padding: 4px 12px;
    border-radius: 20px;
    font-size: 0.85rem;
    font-weight: 500;
  }

  .pet-type.dog {
    background-color: #e3f2fd;
    color: #2196f3;
  }

  .pet-type.cat {
    background-color: #f3e5f5;
    color: #9c27b0;
  }

  .pet-type.rabbit {
    background-color: #e8f5e9;
    color: #4caf50;
  }

  .pet-age {
    color: #666;
    font-size: 0.85rem;
    display: flex;
    align-items: center;
    gap: 5px;
  }

  .breed-info {
    margin: 15px 0;
  }

  .breed-feature {
    display: flex;
    align-items: center;
    margin: 8px 0;
    font-size: 0.9rem;
    color: #555;
  }

  .breed-feature i {
    width: 20px;
    margin-right: 8px;
    color: #667eea;
  }

  .pet-description {
    color: #666;
    line-height: 1.5;
    margin-top: 10px;
  }

  .view-more {
    text-align: center;
    margin-top: 50px;
  }

  /* 响应式 */
  @media (max-width: 768px) {
    .hero h1 {
      font-size: 2rem;
    }
    
    .hero p {
      font-size: 1rem;
    }
    
    .hero-btn, .hero-btn-secondary {
      padding: 8px 20px;
      font-size: 0.9rem;
    }
    
    .section-title {
      font-size: 1.8rem;
    }
    
    .pets-grid {
      grid-template-columns: 1fr;
    }
  }
</style>