<template>
  <div class="pet-detail-container">
    <div class="container">
      <div class="row">
        <!-- 左侧图片区域 -->
        <div class="col-md-6">
          <img :src="currentImage" class="pet-image-large" :alt="pet.name">
          <div class="row mt-3">
            <div 
              v-for="(img, index) in petImages" 
              :key="index"
              class="col-4 text-center"
            >
              <img 
                :src="img" 
                class="img-fluid rounded thumbnail-img" 
                :alt="pet.name"
                @click="currentImage = img"
              >
            </div>
          </div>
        </div>

        <!-- 右侧信息区域 -->
        <div class="col-md-6">
          <div class="pet-info-card">
            <h1 class="pet-name-header">{{ pet.name }}</h1>
            
            <div class="mb-4">
              <span class="status-badge" :class="statusClass">
                {{ pet.status === 'AVAILABLE' ? '可领养' : '已被领养' }}
              </span>
            </div>
            
            <div class="pet-info-item">
              <span class="pet-info-label"><i class="fas fa-paw"></i> 品种：</span>
              <span class="pet-info-value">{{ pet.breed }}</span>
            </div>
            
            <div class="pet-info-item">
              <span class="pet-info-label"><i class="fas fa-birthday-cake"></i> 年龄：</span>
              <span class="pet-info-value">{{ pet.age }}岁</span>
            </div>
            
            <div class="pet-info-item">
              <span class="pet-info-label">
                <i :class="genderIcon"></i> 性别：
              </span>
              <span class="pet-info-value">{{ pet.gender === 'MALE' ? '公' : '母' }}</span>
            </div>
            
            <!-- 宠物介绍 -->
            <div class="section-title">宠物介绍</div>
            <div class="pet-description-box">
              {{ pet.description }}
            </div>
            
            <!-- 健康信息 -->
            <div class="section-title">健康信息</div>
            <div class="health-info">
              <div class="pet-info-item">
                <span class="pet-info-label"><i class="fas fa-heartbeat"></i> 健康状况：</span>
                <span class="pet-info-value">{{ pet.healthStatus || '健康' }}</span>
              </div>
              <div class="pet-info-item">
                <span class="pet-info-label"><i class="fas fa-syringe"></i> 疫苗接种：</span>
                <span class="pet-info-value">{{ pet.vaccinated ? '已完成所有疫苗接种' : '未接种疫苗' }}</span>
              </div>
              <div class="pet-info-item">
                <span class="pet-info-label"><i class="fas fa-stethoscope"></i> 驱虫情况：</span>
                <span class="pet-info-value">{{ pet.dewormed ? '已完成定期驱虫' : '未驱虫' }}</span>
              </div>
            </div>
            
            <!-- 照顾要点 -->
            <div class="section-title">照顾要点</div>
            <div class="care-tips">
              <p v-for="tip in careTips" :key="tip">
                <i class="fas fa-check-circle text-success"></i> {{ tip }}
              </p>
            </div>
            
            <!-- 操作按钮区域 -->
            <div class="mt-4">
              <!-- 可领养状态显示申请按钮 -->
              <button 
                v-if="pet.status === 'AVAILABLE' && !isLoggedIn"
                class="btn btn-success btn-lg me-2"
                @click="goToLogin"
              >
                <i class="fas fa-heart"></i> 申请领养
              </button>
              
              <button 
                v-else-if="pet.status === 'AVAILABLE' && isLoggedIn && !hasApplied"
                class="btn btn-success btn-lg me-2"
                :disabled="loading"
                @click="handleApply"
              >
                <i class="fas fa-heart"></i> {{ loading ? '提交中...' : '申请领养' }}
              </button>
              
              <button 
                v-else-if="pet.status === 'AVAILABLE' && hasApplied"
                class="btn btn-secondary btn-lg me-2"
                disabled
              >
                <i class="fas fa-check"></i> 已申请
              </button>
              
              <router-link to="/pet/list" class="btn btn-outline-primary back-button">
                <i class="fas fa-arrow-left"></i> 返回宠物列表
              </router-link>
            </div>
            
            <!-- 未登录提示 -->
            <div v-if="!isLoggedIn && pet.status === 'AVAILABLE'" class="alert alert-warning mt-3">
              <i class="fas fa-info-circle"></i> 请先 
              <router-link :to="`/login?redirect=/pet/detail/${petId}`">登录</router-link> 
              后再申请领养
            </div>
            
            <!-- 已申请提示 -->
            <div v-if="hasApplied" class="alert alert-info mt-3">
              <i class="fas fa-info-circle"></i> 
              您已经申请过领养这只宠物，可以在 
              <router-link to="/adoption/my">我的申请</router-link> 
              中查看状态
            </div>
          </div>
        </div>
      </div>
      
      <!-- 相关宠物推荐 -->
      <div class="row mt-5">
        <div class="col-12">
          <h3 class="section-title">您可能还喜欢</h3>
          <div class="row">
            <div 
              v-for="relatedPet in relatedPets" 
              :key="relatedPet.id"
              class="col-md-3 col-sm-6 mb-4"
            >
              <div class="card h-100" @click="goToPetDetail(relatedPet.id)">
                <img :src="relatedPet.image" class="card-img-top" :alt="relatedPet.name">
                <div class="card-body">
                  <h5 class="card-title">{{ relatedPet.name }}</h5>
                  <p class="card-text">{{ relatedPet.shortDesc }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
    import { ref, computed, onMounted } from 'vue'
    import { useRoute, useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { useUserStore } from '@/stores/user'
    import { getPetDetail, applyAdoption, getRelatedPets } from '@/api/petApi'

    const route = useRoute()
    const router = useRouter()
    const userStore = useUserStore()

    const petId = computed(() => route.params.id)
    const loading = ref(false)
    const hasApplied = ref(false)

    // 宠物数据
    const pet = ref({
    id: null,
    name: '',
    breed: '',
    age: 0,
    gender: 'MALE',
    status: 'AVAILABLE',
    description: '',
    healthStatus: '健康',
    vaccinated: true,
    dewormed: true,
    image: '/images/pets/default.jpg'
    })

    // 图片列表
    const petImages = ref([])
    const currentImage = ref('')

    // 相关宠物
    const relatedPets = ref([])

    // 计算属性
    const isLoggedIn = computed(() => userStore.isLoggedIn)
    const statusClass = computed(() => {
    return pet.value.status === 'AVAILABLE' ? 'status-available' : 'status-adopted'
    })

    const genderIcon = computed(() => {
    return pet.value.gender === 'MALE' ? 'fas fa-mars' : 'fas fa-venus'
    })

    const careTips = computed(() => {
    const breed = pet.value.breed
    if (breed.includes('狗')) {
        return [
        '每天需要至少1-2小时运动',
        '需要定期梳毛和洗澡',
        '需要基本服从训练'
        ]
    } else if (breed.includes('猫')) {
        return [
        '需要猫砂盆和定期清理',
        '需要猫抓板防止抓家具',
        '定期梳毛和指甲修剪'
        ]
    } else if (breed.includes('兔')) {
        return [
        '需要干草和新鲜蔬菜',
        '定期修剪指甲',
        '需要足够的活动空间'
        ]
    } else {
        return [
        '需要专门的饲养环境',
        '需要定期清洁笼舍',
        '需要专业宠物食品'
        ]
    }
    })

    // 申请领养
    const handleApply = async () => {
    loading.value = true
    try {
        const response = await applyAdoption(petId.value)
        if (response.code === 200) {
        hasApplied.value = true
        ElMessage.success('申请成功，请耐心等待审核')
        } else {
        ElMessage.error(response.message || '申请失败')
        }
    } catch (error) {
        ElMessage.error('申请失败，请稍后重试')
    } finally {
        loading.value = false
    }
    }

    // 跳转登录
    const goToLogin = () => {
    router.push(`/login?redirect=/pet/detail/${petId.value}`)
    }

    // 跳转宠物详情
    const goToPetDetail = (id) => {
    router.push(`/pet/detail/${id}`)
    }

    // 生成图片列表
    const generateImageList = () => {
    const baseImage = pet.value.image
    petImages.value = [
        baseImage,
        baseImage.replace('.jpg', '_2.jpg'),
        baseImage.replace('.jpg', '_3.jpg')
    ].filter(img => img !== baseImage)
    petImages.value.unshift(baseImage)
    }

    // 加载宠物详情
    const loadPetDetail = async () => {
    try {
        const response = await getPetDetail(petId.value)
        if (response.code === 200) {
        pet.value = response.data
        currentImage.value = pet.value.image
        generateImageList()
        hasApplied.value = response.data.hasApplied || false
        } else {
        ElMessage.error('获取宠物信息失败')
        }
    } catch (error) {
        ElMessage.error('加载失败')
    }
    }

    // 加载相关宠物
    const loadRelatedPets = async () => {
    try {
        const response = await getRelatedPets(petId.value)
        if (response.code === 200) {
        relatedPets.value = response.data
        }
    } catch (error) {
        console.error('加载相关宠物失败', error)
    }
    }

    onMounted(() => {
    loadPetDetail()
    loadRelatedPets()
    })
</script>

<style scoped>
    .pet-detail-container {
    margin-top: 30px;
    margin-bottom: 50px;
    }

    .pet-image-large {
    border-radius: 15px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    width: 100%;
    height: 400px;
    object-fit: cover;
    }

    .thumbnail-img {
    height: 80px;
    object-fit: cover;
    cursor: pointer;
    transition: all 0.3s ease;
    opacity: 0.7;
    }

    .thumbnail-img:hover {
    opacity: 1;
    transform: scale(1.05);
    }

    .pet-info-card {
    background: white;
    border-radius: 15px;
    padding: 30px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    height: 100%;
    }

    .pet-name-header {
    color: #333;
    font-weight: 700;
    margin-bottom: 20px;
    border-bottom: 3px solid #4361ee;
    padding-bottom: 10px;
    }

    .pet-info-item {
    margin-bottom: 15px;
    display: flex;
    align-items: center;
    }

    .pet-info-label {
    font-weight: 600;
    color: #555;
    min-width: 80px;
    }

    .pet-info-value {
    color: #333;
    margin-left: 10px;
    }

    .pet-description-box {
    background: #f8f9fa;
    border-radius: 10px;
    padding: 20px;
    margin-top: 20px;
    line-height: 1.8;
    }

    .status-badge {
    display: inline-block;
    padding: 6px 15px;
    border-radius: 20px;
    font-size: 0.9rem;
    font-weight: 600;
    }

    .status-available {
    background: #d4edda;
    color: #155724;
    }

    .status-adopted {
    background: #f8d7da;
    color: #721c24;
    }

    .back-button {
    margin-top: 20px;
    }

    .section-title {
    font-size: 1.2rem;
    font-weight: 600;
    color: #4361ee;
    margin-bottom: 15px;
    margin-top: 20px;
    padding-bottom: 8px;
    border-bottom: 2px solid #f0f0f0;
    }

    .health-info {
    background: #e7f5ff;
    border-radius: 10px;
    padding: 15px;
    margin-top: 20px;
    }

    .care-tips {
    background: #fff3cd;
    border-radius: 10px;
    padding: 15px;
    margin-top: 20px;
    }

    .care-tips p {
    margin-bottom: 8px;
    }

    .care-tips p:last-child {
    margin-bottom: 0;
    }

    .btn-success {
    background: linear-gradient(135deg, #28a745, #20c997);
    border: none;
    padding: 12px 30px;
    transition: all 0.3s ease;
    }

    .btn-success:hover:not(:disabled) {
    transform: translateY(-3px);
    box-shadow: 0 10px 20px rgba(0, 0, 0, 0.2);
    }

    .btn-success:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    }

    .btn-outline-primary {
    border: 2px solid #4361ee;
    color: #4361ee;
    padding: 12px 30px;
    transition: all 0.3s ease;
    }

    .btn-outline-primary:hover {
    background: #4361ee;
    color: white;
    transform: translateY(-2px);
    }

    .card {
    cursor: pointer;
    transition: all 0.3s ease;
    overflow: hidden;
    border-radius: 10px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
    }

    .card:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
    }

    .card-img-top {
    height: 200px;
    object-fit: cover;
    transition: transform 0.3s ease;
    }

    .card:hover .card-img-top {
    transform: scale(1.05);
    }

    .card-title {
    font-size: 1.1rem;
    font-weight: 600;
    margin-bottom: 8px;
    }

    .card-text {
    font-size: 0.85rem;
    color: #666;
    }

    .alert {
    border-radius: 10px;
    }

    .alert a {
    color: inherit;
    text-decoration: underline;
    }

    @media (max-width: 768px) {
    .pet-image-large {
        height: 250px;
    }
    
    .pet-info-card {
        margin-top: 20px;
        padding: 20px;
    }
    
    .pet-name-header {
        font-size: 1.5rem;
    }
    
    .btn-success,
    .btn-outline-primary {
        width: 100%;
        margin-bottom: 10px;
    }
    
    .me-2 {
        margin-right: 0 !important;
    }
    }
</style>