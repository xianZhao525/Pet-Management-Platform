<template>
  <div class="pet-list-page">
    <!-- Hero区域 -->
    <div class="pet-hero">
      <div class="container">
        <h1 class="display-4 fade-in-up">
         🐶领养宠物
        </h1>
        <p class="lead">给流浪的毛孩子一个温暖的家，每一次领养都是拯救一个生命</p>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="container">
      <div class="pet-stats">
        <div class="stats-grid">
          <div class="stat-item">
            <span class="stat-number">{{ stats.availableCount }}</span>
            <div>待领养宠物</div>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ stats.adoptedCount }}</span>
            <div>已被领养</div>
          </div>
          <div class="stat-item">
            <span class="stat-number">{{ stats.fosteredCount }}</span>
            <div>寄养中</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索筛选区域 -->
    <div class="container">
      <div class="search-filter-section">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchKeyword"
            placeholder="搜索宠物名称、品种或描述..."
            @keyup.enter="handleSearch"
          >
          <button @click="handleSearch">
            <i class="fas fa-search"></i>
          </button>
        </div>

        <!-- 分类导航 -->
        <div class="category-nav">
          <button 
            v-for="category in categories" 
            :key="category.value"
            class="category-tab"
            :class="{ active: activeCategory === category.value }"
            @click="switchCategory(category.value)"
          >
            <i :class="category.icon" class="category-icon"></i> {{ category.name }}
          </button>
        </div>
      </div>
    </div>

    <!-- 宠物分类展示区 -->
    <div class="container mb-5">
      <div 
        v-for="category in displayedCategories" 
        :key="category.type"
        class="category-section"
      >
        <div class="category-header">
          <h2 class="category-title">
            <i :class="category.icon" class="category-icon"></i> {{ category.name }}
          </h2>
          <!-- 修改为按钮，点击后切换分类 -->
          <a href="#" class="view-all-btn" @click.prevent="switchCategory(category.value)">
            查看全部{{ category.name }} →
          </a>
        </div>

        <div class="pets-grid">
          <PetCard 
            v-for="(pet, index) in getCategoryPets(category.type)"
            :key="pet.id"
            :pet="pet"
            :style="{ animationDelay: `${0.1 + index * 0.1}s` }"
          />
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredPets.length === 0" class="empty-state">
        <i class="fas fa-paw"></i>
        <h3>暂无宠物</h3>
        <p>暂时没有找到符合条件的宠物，请稍后再来查看</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import PetCard from '@/components/pet/PetCard.vue'
import { getPetList } from '@/api/petApi'

const route = useRoute()
const router = useRouter()

// 数据
const searchKeyword = ref('')
const activeCategory = ref('all')
const pets = ref([])

// 统计数据
const stats = ref({
  availableCount: 0,
  adoptedCount: 0,
  fosteredCount: 0
})

// 分类配置
const categories = [
  { value: 'all', name: '全部宠物', icon: 'fas fa-paw', type: 'ALL' },
  { value: 'dog', name: '狗狗', icon: 'fas fa-dog', type: 'DOG' },
  { value: 'cat', name: '猫咪', icon: 'fas fa-cat', type: 'CAT' },
  { value: 'rabbit', name: '兔子', icon: 'fas fa-paw', type: 'RABBIT' },
  { value: 'other', name: '其他', icon: 'fas fa-dove', type: 'OTHER' }
]

// 显示的分类（非全部时只显示当前分类）
const displayedCategories = computed(() => {
  if (activeCategory.value === 'all') {
    return categories.filter(c => c.value !== 'all')
  }
  return categories.filter(c => c.value === activeCategory.value)
})

// 筛选后的宠物
const filteredPets = computed(() => {
  let result = pets.value
  
  // 按分类筛选
  if (activeCategory.value !== 'all') {
    const categoryType = categories.find(c => c.value === activeCategory.value)?.type
    if (categoryType) {
      result = result.filter(pet => pet.type === categoryType)
    }
  }
  
  // 按关键词搜索
  if (searchKeyword.value.trim()) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(pet => 
      pet.name.toLowerCase().includes(keyword) ||
      pet.breed?.toLowerCase().includes(keyword) ||
      pet.description?.toLowerCase().includes(keyword)
    )
  }
  
  return result
})

// 获取分类宠物（最多5只）
const getCategoryPets = (categoryType) => {
  return filteredPets.value
    .filter(pet => pet.type === categoryType)
    .slice(0, 5)
}

// 切换分类
const switchCategory = (category) => {
  activeCategory.value = category
  
  // 滚动到搜索区域
  const searchSection = document.querySelector('.search-filter-section')
  if (searchSection) {
    searchSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }
  
  // 更新URL参数
  if (category === 'all') {
    router.replace({ query: {} })
  } else {
    router.replace({ query: { type: category } })
  }
}

// 搜索
const handleSearch = () => {
  // 更新URL参数
  const query = {}
  if (searchKeyword.value) query.keyword = searchKeyword.value
  if (activeCategory.value !== 'all') query.type = activeCategory.value
  router.replace({ query })
}

// ========== 静态宠物数据（从原 JSP 提取）==========
const staticPets = [
  // 狗狗
  { id: 1, name: '旺财', type: 'DOG', breed: '金毛寻回犬', age: 2, gender: 'MALE', description: '性格温和友善，特别喜欢小孩，已经学会了基本指令如"坐下"、"握手"，喜欢玩接球游戏。', image: '/images/pets/dog1.jpg', healthStatus: '健康', vaccinated: true },
  { id: 2, name: '小黑', type: 'DOG', breed: '边境牧羊犬', age: 3, gender: 'FEMALE', description: '非常聪明活泼，精力充沛，喜欢追逐和接飞盘，适合有院子或有时间带它户外活动的家庭。', image: '/images/pets/dog2.jpg', healthStatus: '健康', vaccinated: true },
  { id: 3, name: '豆豆', type: 'DOG', breed: '柯基犬', age: 1.5, gender: 'MALE', description: '短腿柯基，性格活泼可爱，特别喜欢撒娇，会跟着主人走来走去，是个小跟屁虫。', image: '/images/pets/dog3.jpg', healthStatus: '健康', vaccinated: true },
  { id: 4, name: '乐乐', type: 'DOG', breed: '哈士奇', age: 2.5, gender: 'FEMALE', description: '蓝眼睛的美丽哈士奇，性格独立但有颗温柔的心，需要定期梳毛，不适合公寓饲养。', image: '/images/pets/dog4.jpg', healthStatus: '健康', vaccinated: true },
  { id: 5, name: '多多', type: 'DOG', breed: '柴犬', age: 4, gender: 'MALE', description: '表情包常客柴犬，性格忠诚机警，对主人非常忠心，适合有养狗经验的家庭。', image: '/images/pets/dog5.jpg', healthStatus: '健康', vaccinated: true },
  // 猫咪
  { id: 6, name: '咪咪', type: 'CAT', breed: '英国短毛猫', age: 3, gender: 'FEMALE', description: '蓝灰色英短，性格温和安静，喜欢趴在窗边看风景，对陌生人有些害羞。', image: '/images/pets/cat1.jpg', healthStatus: '健康', vaccinated: true },
  { id: 7, name: '小橘', type: 'CAT', breed: '橘猫', age: 2, gender: 'MALE', description: '名副其实的小吃货，性格亲人，特别喜欢被抚摸下巴，会发出响亮的呼噜声。', image: '/images/pets/cat2.jpg', healthStatus: '健康', vaccinated: true },
  { id: 8, name: '小白', type: 'CAT', breed: '波斯猫', age: 4, gender: 'FEMALE', description: '纯白长毛波斯猫，性格优雅高傲，需要每天梳毛，喜欢安静的环境。', image: '/images/pets/cat3.jpg', healthStatus: '健康', vaccinated: true },
  { id: 9, name: '奶牛', type: 'CAT', breed: '奶牛猫', age: 1.5, gender: 'MALE', description: '黑白花纹奶牛猫，性格活泼好动，喜欢探索家里的每个角落，好奇心强。', image: '/images/pets/cat4.jpg', healthStatus: '健康', vaccinated: true },
  { id: 10, name: '小黑猫', type: 'CAT', breed: '黑猫', age: 2.5, gender: 'FEMALE', description: '乌黑发亮的毛发，性格独立但亲人，晚上眼睛像两颗宝石，非常漂亮。', image: '/images/pets/cat5.jpg', healthStatus: '健康', vaccinated: true },
  // 兔子
  { id: 11, name: '雪球', type: 'RABBIT', breed: '垂耳兔', age: 1, gender: 'FEMALE', description: '纯白垂耳兔，性格温顺胆小，喜欢吃提摩西草和胡萝卜，需要定期修剪指甲。', image: '/images/pets/rabbit1.jpg', healthStatus: '健康', vaccinated: true },
  { id: 12, name: '咖啡', type: 'RABBIT', breed: '侏儒兔', age: 2, gender: 'MALE', description: '咖啡色侏儒兔，体型小巧可爱，性格活泼好动，喜欢在笼子里跳来跳去。', image: '/images/pets/rabbit2.jpg', healthStatus: '健康', vaccinated: true },
  { id: 13, name: '花花', type: 'RABBIT', breed: '道奇兔', age: 1.5, gender: 'FEMALE', description: '黑白花纹道奇兔，性格亲人，喜欢被轻轻抚摸头部，食量很大但很健康。', image: '/images/pets/rabbit3.jpg', healthStatus: '健康', vaccinated: true },
  { id: 14, name: '灰灰', type: 'RABBIT', breed: '安哥拉兔', age: 3, gender: 'MALE', description: '灰色长毛安哥拉兔，毛发需要每天梳理，性格温顺安静，适合细心有耐心的主人。', image: '/images/pets/rabbit4.jpg', healthStatus: '健康', vaccinated: true },
  { id: 15, name: '巧克力', type: 'RABBIT', breed: '荷兰垂耳兔', age: 2, gender: 'FEMALE', description: '深棕色垂耳兔，特别喜欢吃甜菜根和苹果，会用便盆，非常聪明。', image: '/images/pets/rabbit5.jpg', healthStatus: '健康', vaccinated: true },
  // 其他
  { id: 16, name: '小胖', type: 'OTHER', breed: '仓鼠', age: 0.5, gender: 'MALE', description: '黄金仓鼠，圆滚滚的很可爱，晚上比较活跃，喜欢在滚轮上跑步。', image: '/images/pets/other1.jpg', healthStatus: '健康', vaccinated: true },
  { id: 17, name: '小绿', type: 'OTHER', breed: '鹦鹉', age: 3, gender: 'FEMALE', description: '虎皮鹦鹉，羽毛颜色鲜艳漂亮，会模仿简单的声音，喜欢和人互动。', image: '/images/pets/other2.jpg', healthStatus: '健康', vaccinated: true },
  { id: 18, name: '慢慢', type: 'OTHER', breed: '乌龟', age: 8, gender: 'MALE', description: '巴西龟，性格安静，寿命长，容易照顾，需要定期晒背和换水。', image: '/images/pets/other3.jpg', healthStatus: '健康', vaccinated: true },
  { id: 19, name: '嘟嘟', type: 'OTHER', breed: '荷兰猪', age: 2, gender: 'FEMALE', description: '三色荷兰猪，喜欢发出"咕咕"的声音，需要足够的活动空间和新鲜蔬菜。', image: '/images/pets/other4.jpg', healthStatus: '健康', vaccinated: true },
  { id: 20, name: '毛毛', type: 'OTHER', breed: '龙猫', age: 4, gender: 'MALE', description: '灰色龙猫，毛茸茸的很可爱，需要定期梳毛和沙浴，性格温和亲人。', image: '/images/pets/other5.jpg', healthStatus: '健康', vaccinated: true }
]

// 加载宠物列表
const loadPets = async () => {
  try {
    const response = await getPetList({
      keyword: searchKeyword.value || undefined,
      type: activeCategory.value !== 'all' ? activeCategory.value.toUpperCase() : undefined
    })
    
    if (response.code === 200 && response.data.list?.length) {
      pets.value = response.data.list
      stats.value = {
        availableCount: response.data.stats?.availableCount || 0,
        adoptedCount: response.data.stats?.adoptedCount || 0,
        fosteredCount: response.data.stats?.fosteredCount || 0
      }
    } else {
      // API 无数据或失败时，使用静态数据
      console.warn('使用静态数据')
      pets.value = staticPets
      stats.value = {
        availableCount: staticPets.length,
        adoptedCount: 0,
        fosteredCount: 0
      }
    }
  } catch (error) {
    console.error('加载宠物列表失败，使用静态数据', error)
    pets.value = staticPets
    stats.value = {
      availableCount: staticPets.length,
      adoptedCount: 0,
      fosteredCount: 0
    }
  }
}

// 监听路由参数变化
onMounted(() => {
  // 从URL获取参数
  const typeParam = route.query.type
  const keywordParam = route.query.keyword
  
  if (typeParam) {
    const category = categories.find(c => c.value === typeParam.toLowerCase())
    if (category) activeCategory.value = category.value
  }
  if (keywordParam) searchKeyword.value = keywordParam
  
  loadPets()
})
</script>

<style scoped>
  /* Hero区域 */
  .pet-hero {
    background: linear-gradient(135deg, #ec965dff 0%, #fd4b4bff 100%);
    color: white;
    padding: 80px 0;
    text-align: center;
  }

  .pet-hero h1 {
    font-size: 2.5rem;
    margin-bottom: 20px;
    font-weight: 700;
  }

  .pet-hero .lead {
    font-size: 1.2rem;
    opacity: 0.9;
  }

  .fade-in-up {
    animation: fadeInUp 0.8s ease-out;
  }

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

  /* 统计信息卡片 */
  .pet-stats {
    background: white;
    border-radius: 15px;
    padding: 30px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    margin: -50px auto 50px;
    max-width: 900px;
    position: relative;
    z-index: 10;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 30px;
  }

  .stat-item {
    text-align: center;
    padding: 20px;
    border-right: 1px solid #eee;
  }

  .stat-item:last-child {
    border-right: none;
  }

  .stat-number {
    font-size: 2.5rem;
    font-weight: bold;
    color: var(--primary-color, #4e97fd);
    display: block;
  }

  /* 搜索筛选区域 */
  .search-filter-section {
    background: white;
    padding: 25px;
    border-radius: 15px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
    margin-bottom: 30px;
  }

  .search-box {
    position: relative;
    max-width: 500px;
    margin: 0 auto;
  }

  .search-box input {
    width: 100%;
    padding: 15px 50px 15px 20px;
    border: 2px solid #e0e0e0;
    border-radius: 30px;
    font-size: 1rem;
    transition: all 0.3s ease;
  }

  .search-box input:focus {
    outline: none;
    border-color: var(--primary-color, #4e97fd);
    box-shadow: 0 0 0 3px rgba(78, 151, 253, 0.1);
  }

  .search-box button {
    position: absolute;
    right: 5px;
    top: 50%;
    transform: translateY(-50%);
    background: var(--primary-color, #4e97fd);
    color: white;
    border: none;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    cursor: pointer;
    transition: all 0.3s ease;
  }

  .search-box button:hover {
    transform: translateY(-50%) scale(1.05);
  }

  /* 分类标签导航 */
  .category-nav {
    display: flex;
    justify-content: center;
    gap: 5px;
    margin: 30px 0 0;
    flex-wrap: wrap;
  }

  .category-tab {
    background: none;
    border: none;
    padding: 10px 25px;
    font-size: 1.1rem;
    font-weight: 600;
    color: #666;
    border-bottom: 3px solid transparent;
    transition: all 0.3s ease;
    cursor: pointer;
    margin: 0 5px;
  }

  .category-tab:hover {
    color: var(--primary-color, #4e97fd);
  }

  .category-tab.active {
    color: var(--primary-color, #4e97fd);
    border-bottom-color: var(--primary-color, #4e97fd);
    background: rgba(78, 151, 253, 0.1);
    border-radius: 30px 30px 0 0;
  }

  .category-icon {
    margin-right: 8px;
  }

  /* 分类区域 */
  .category-section {
    margin-bottom: 50px;
  }

  .category-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 25px;
    padding-bottom: 10px;
    border-bottom: 2px solid #f0f0f0;
  }

  .category-title {
    font-size: 1.8rem;
    font-weight: 700;
    color: var(--dark-color, #343a40);
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .category-title .category-icon {
    font-size: 1.5rem;
  }

  .view-all-btn {
    color: var(--primary-color, #4e97fd);
    text-decoration: none;
    font-weight: 500;
    transition: all 0.3s ease;
  }

  .view-all-btn:hover {
    color: var(--primary-dark-color, #3a7bd5);
    text-decoration: underline;
  }

  /* 宠物网格 */
  .pets-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 30px;
  }

  /* 空状态 */
  .empty-state {
    text-align: center;
    padding: 80px 20px;
    color: #666;
  }

  .empty-state i {
    font-size: 4rem;
    color: #ddd;
    margin-bottom: 20px;
  }

  .empty-state h3 {
    margin-bottom: 10px;
    color: #333;
  }

  /* 响应式调整 */
  @media (max-width: 768px) {
    .pet-hero {
      padding: 60px 0;
    }
    .pet-hero h1 {
      font-size: 1.8rem;
    }
    .stats-grid {
      gap: 15px;
    }
    .stat-number {
      font-size: 1.5rem;
    }
    .pets-grid {
      grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
      gap: 20px;
    }
    .category-nav {
      flex-direction: column;
      align-items: center;
      gap: 10px;
    }
    .category-tab {
      width: 200px;
      text-align: center;
    }
    .category-header {
      flex-direction: column;
      gap: 15px;
      text-align: center;
    }
  }
</style>