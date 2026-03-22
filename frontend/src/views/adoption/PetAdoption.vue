<template>
  <div class="pet-list-page">
    <!-- Hero区域 -->
    <div class="pet-hero">
      <div class="container">
        <h1 class="display-4 fade-in-up">
          <i class="fas fa-paw"></i> 领养宠物
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
          <router-link :to="`/pet/list?type=${category.type}`" class="view-all-btn">
            查看全部{{ category.name }} →
          </router-link>
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

    // 加载宠物列表
    const loadPets = async () => {
    try {
        const response = await getPetList({
        keyword: searchKeyword.value || undefined,
        type: activeCategory.value !== 'all' ? activeCategory.value.toUpperCase() : undefined
        })
        
        if (response.code === 200) {
        pets.value = response.data.list || []
        stats.value = {
            availableCount: response.data.stats?.availableCount || 0,
            adoptedCount: response.data.stats?.adoptedCount || 0,
            fosteredCount: response.data.stats?.fosteredCount || 0
        }
        }
    } catch (error) {
        console.error('加载宠物列表失败', error)
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
    padding: 100px 0;
    text-align: center;
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

    .category-icon {
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

    /* 响应式 */
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