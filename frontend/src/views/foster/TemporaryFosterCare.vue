<template>
  <div class="foster-page">
    <!-- Hero区域 -->
    <div class="foster-hero">
      <div class="container">
        <h1 class="display-4">🐾 临时寄养服务</h1>
        <p class="lead">当您外出时，我们为您提供安全可靠的宠物临时寄养服务</p>
      </div>
    </div>

    <!-- 服务卡片区域 -->
    <div class="container my-5">
      <div class="row">
        <div v-for="service in services" :key="service.title" class="col-md-4 mb-4">
          <div class="foster-card" @click="goToServiceDetail(service.id)">
            <img :src="service.image" :alt="service.title">
            <div class="card-body">
              <h5>{{ service.icon }} {{ service.title }}</h5>
              <p>{{ service.description }}</p>
              <button class="btn btn-outline-primary btn-sm mt-2">
                了解详情 <i class="fas fa-arrow-right"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 寄养流程 -->
    <div class="process-section">
      <div class="container">
        <h2 class="section-title">寄养流程</h2>
        <div class="process-steps">
          <div v-for="(step, index) in processSteps" :key="index" class="step-item">
            <div class="step-number">{{ index + 1 }}</div>
            <div class="step-icon">
              <i :class="step.icon"></i>
            </div>
            <h4>{{ step.title }}</h4>
            <p>{{ step.description }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 寄养套餐 -->
    <div class="packages-section">
      <div class="container">
        <h2 class="section-title">寄养套餐</h2>
        <div class="row">
          <div v-for="pkg in packages" :key="pkg.name" class="col-md-4 mb-4">
            <div class="package-card" :class="{ popular: pkg.popular }">
              <div v-if="pkg.popular" class="popular-badge">推荐</div>
              <h3>{{ pkg.name }}</h3>
              <div class="price">
                <span class="price-number">¥{{ pkg.price }}</span>
                <span class="price-unit">/天</span>
              </div>
              <ul class="package-features">
                <li v-for="feature in pkg.features" :key="feature">
                  <i class="fas fa-check-circle text-success"></i> {{ feature }}
                </li>
              </ul>
              <button class="btn btn-primary" @click="openBookingModal(pkg)">
                立即预约
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 常见问题 -->
    <div class="faq-section">
      <div class="container">
        <h2 class="section-title">常见问题</h2>
        <div class="faq-list">
          <div v-for="(faq, index) in faqs" :key="index" class="faq-item">
            <div class="faq-question" @click="toggleFaq(index)">
              <i class="fas fa-question-circle"></i>
              <span>{{ faq.question }}</span>
              <i :class="faq.open ? 'fas fa-chevron-up' : 'fas fa-chevron-down'"></i>
            </div>
            <div v-show="faq.open" class="faq-answer">
              {{ faq.answer }}
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 预约弹窗 -->
    <el-dialog 
      v-model="bookingModalVisible" 
      title="预约寄养服务"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="bookingForm" :rules="bookingRules" ref="bookingFormRef" label-width="100px">
        <el-form-item label="寄养套餐" prop="packageName">
          <el-input :value="selectedPackage?.name" disabled />
        </el-form-item>

        <el-form-item label="宠物名称" prop="petName">
          <el-input v-model="bookingForm.petName" placeholder="请输入宠物名称" />
        </el-form-item>

        <el-form-item label="宠物类型" prop="petType">
          <el-select v-model="bookingForm.petType" placeholder="请选择宠物类型">
            <el-option label="狗狗" value="dog" />
            <el-option label="猫咪" value="cat" />
            <el-option label="兔子" value="rabbit" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item label="寄养日期" prop="dateRange">
          <el-date-picker
            v-model="bookingForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            :disabled-date="disabledDate"
            style="width: 100%"
          />
        </el-form-item>

        <el-form-item label="联系人" prop="contactName">
          <el-input v-model="bookingForm.contactName" placeholder="请输入联系人姓名" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="bookingForm.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="特殊要求" prop="specialRequests">
          <el-input 
            v-model="bookingForm.specialRequests" 
            type="textarea" 
            :rows="3"
            placeholder="如有特殊要求，请在此说明"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="bookingModalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitBooking" :loading="submitting">
            {{ submitting ? '提交中...' : '确认预约' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 预约成功弹窗 -->
    <el-dialog v-model="successModalVisible" title="预约成功" width="400px" center>
      <div class="success-content">
        <i class="fas fa-check-circle success-icon"></i>
        <h3>预约成功！</h3>
        <p>我们已收到您的寄养预约</p>
        <p class="small-text">工作人员会在24小时内与您联系确认</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="successModalVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
    import { ref, reactive } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage } from 'element-plus'
    import { createFoster } from '@/api/fosterApi'

    const router = useRouter()

    // 服务数据
    const services = ref([
    {
        id: 1,
        title: '家庭式寄养',
        description: '温馨的家庭环境，让您的宠物感受家的温暖',
        icon: '🏠',
        image: 'https://images.unsplash.com/photo-1581888227599-779811939961?w=400'
    },
    {
        id: 2,
        title: '专业护理',
        description: '经验丰富的护理人员，24小时贴心照顾',
        icon: '👩‍⚕️',
        image: 'https://images.unsplash.com/photo-1548199973-03cce0bbc87b?w=400'
    },
    {
        id: 3,
        title: '实时监控',
        description: '提供实时视频，随时查看宠物状态',
        icon: '📱',
        image: 'https://images.unsplash.com/photo-1537151625747-768eb6cf92b2?w=400'
    }
    ])

    // 寄养流程
    const processSteps = ref([
    {
        title: '在线预约',
        description: '选择寄养套餐和日期，填写宠物信息',
        icon: 'fas fa-calendar-alt'
    },
    {
        title: '健康检查',
        description: '寄养前进行宠物健康检查',
        icon: 'fas fa-stethoscope'
    },
    {
        title: '签订协议',
        description: '签订寄养服务协议，明确双方责任',
        icon: 'fas fa-file-signature'
    },
    {
        title: '安心寄养',
        description: '宠物入住，享受专业照顾',
        icon: 'fas fa-heart'
    }
    ])

    // 寄养套餐
    const packages = ref([
    {
        name: '基础套餐',
        price: 68,
        features: ['基础喂养', '日常清洁', '每日散步2次', '基础玩具'],
        popular: false
    },
    {
        name: '标准套餐',
        price: 128,
        features: ['优质粮喂养', '每日清洁消毒', '每日散步3次', '专业玩具', '每日照片反馈'],
        popular: true
    },
    {
        name: '尊享套餐',
        price: 228,
        features: ['进口粮喂养', '独立单间', '每日散步4次', '豪华玩具', '每日视频反馈', '宠物美容1次'],
        popular: false
    }
    ])

    // 常见问题
    const faqs = ref([
    {
        question: '寄养需要提前多久预约？',
        answer: '建议提前3-7天预约，节假日期间请提前2周预约，以确保有合适的寄养位置。',
        open: false
    },
    {
        question: '寄养需要提供哪些证件？',
        answer: '需要提供宠物的疫苗接种证明、健康证明，以及主人的身份证明。',
        open: false
    },
    {
        question: '宠物寄养期间生病怎么办？',
        answer: '我们有合作的宠物医院，会第一时间联系宠物主人并送往医院治疗。',
        open: false
    },
    {
        question: '可以上门接宠物吗？',
        answer: '是的，我们提供付费上门接送服务，详情请咨询客服。',
        open: false
    }
    ])

    // 弹窗状态
    const bookingModalVisible = ref(false)
    const successModalVisible = ref(false)
    const selectedPackage = ref(null)
    const submitting = ref(false)
    const bookingFormRef = ref(null)

    // 预约表单
    const bookingForm = reactive({
    packageName: '',
    packageId: null,
    petName: '',
    petType: '',
    dateRange: [],
    contactName: '',
    phone: '',
    specialRequests: ''
    })

    // 表单验证规则
    const bookingRules = {
    petName: [
        { required: true, message: '请输入宠物名称', trigger: 'blur' },
        { min: 1, max: 20, message: '宠物名称长度在1-20个字符之间', trigger: 'blur' }
    ],
    petType: [
        { required: true, message: '请选择宠物类型', trigger: 'change' }
    ],
    dateRange: [
        { required: true, message: '请选择寄养日期', trigger: 'change' }
    ],
    contactName: [
        { required: true, message: '请输入联系人姓名', trigger: 'blur' },
        { min: 2, max: 20, message: '姓名长度在2-20个字符之间', trigger: 'blur' }
    ],
    phone: [
        { required: true, message: '请输入联系电话', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
    ]
    }

    // 禁用过去的日期
    const disabledDate = (time) => {
    return time.getTime() < Date.now() - 86400000
    }

    // 跳转服务详情
    const goToServiceDetail = (id) => {
    router.push(`/foster/service/${id}`)
    }

    // 切换FAQ
    const toggleFaq = (index) => {
    faqs.value[index].open = !faqs.value[index].open
    }

    // 打开预约弹窗
    const openBookingModal = (pkg) => {
    selectedPackage.value = pkg
    bookingForm.packageName = pkg.name
    bookingForm.packageId = pkg.id
    bookingForm.petName = ''
    bookingForm.petType = ''
    bookingForm.dateRange = []
    bookingForm.contactName = ''
    bookingForm.phone = ''
    bookingForm.specialRequests = ''
    bookingModalVisible.value = true
    }

    // 提交预约
    const submitBooking = async () => {
    if (!bookingFormRef.value) return
    
    await bookingFormRef.value.validate(async (valid) => {
        if (!valid) return
        
        submitting.value = true
        try {
        const response = await createFoster({
          petName: bookingForm.petName,
          petType: bookingForm.petType,
          startDate: bookingForm.dateRange[0],
          endDate: bookingForm.dateRange[1],
          dailyFee: selectedPackage.value?.price, // 套餐价格
          description: bookingForm.specialRequests,
          // 其他必要字段根据后端要求补充
        })
        
        if (response.code === 200) {
            bookingModalVisible.value = false
            successModalVisible.value = true
        } else {
            ElMessage.error(response.message || '预约失败')
        }
        } catch (error) {
        ElMessage.error('提交失败，请稍后重试')
        } finally {
        submitting.value = false
        }
    })
    }
</script>

<style scoped>
  /* 全局变量，与 JSP 保持一致 */
  :root {
    --primary-color: #4e97fd;
    --secondary-color: #ff7e5f;
    --dark-color: #343a40;
    --text-light: #777;
    --border-radius: 12px;
    --box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
    --transition: all 0.3s ease;
  }

  /* Hero 区域 */
  .foster-hero {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
    padding: 80px 0;
    text-align: center;
    margin-bottom: 50px;
  }

  .foster-hero h1 {
    font-size: 3rem;
    margin-bottom: 20px;
    font-weight: 700;
  }

  .foster-hero .lead {
    font-size: 1.2rem;
    opacity: 0.9;
    max-width: 700px;
    margin: 0 auto;
  }

  /* 服务卡片区域 */
  .container.my-5 {
    margin-top: 2rem !important;
    margin-bottom: 2rem !important;
  }

  .foster-card {
    border-radius: var(--border-radius);
    overflow: hidden;
    box-shadow: var(--box-shadow);
    transition: var(--transition);
    background: white;
    height: 100%;
    cursor: pointer;
  }

  .foster-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 15px 30px rgba(0, 0, 0, 0.15);
  }

  .foster-card img {
    width: 100%;
    height: 200px;
    object-fit: cover;
    transition: transform 0.5s ease;
  }

  .foster-card:hover img {
    transform: scale(1.05);
  }

  .foster-card .card-body {
    padding: 25px;
    text-align: center;
  }

  .foster-card h5 {
    font-size: 1.3rem;
    margin-bottom: 12px;
    color: var(--dark-color);
    font-weight: 600;
  }

  .foster-card p {
    color: var(--text-light);
    line-height: 1.6;
    margin-bottom: 20px;
  }

  .btn-outline-primary {
    border: 2px solid var(--primary-color);
    color: var(--primary-color);
    background: transparent;
    transition: var(--transition);
    padding: 8px 20px;
    border-radius: 30px;
    font-weight: 500;
  }

  .btn-outline-primary:hover {
    background: var(--primary-color);
    color: white;
    border-color: var(--primary-color);
  }

  /* 寄养流程 */
  .process-section {
    background: #f8f9fa;
    padding: 80px 0;
  }

  .section-title {
    text-align: center;
    font-size: 2.2rem;
    margin-bottom: 50px;
    color: var(--dark-color);
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
    background: var(--primary-color);
    border-radius: 2px;
  }

  .process-steps {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 30px;
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .step-item {
    text-align: center;
    background: linear-gradient(145deg, #f8f7ff 0%, #f1efff 100%);
    padding: 30px 20px;
    border-radius: 24px;
    box-shadow: 0 8px 20px rgba(102, 126, 234, 0.08);
    transition: all 0.3s ease;
    border: 1px solid rgba(102, 126, 234, 0.15);
  }

  .step-item:hover {
    transform: translateY(-8px);
    box-shadow: 0 20px 30px rgba(102, 126, 234, 0.15);
    border-color: rgba(102, 126, 234, 0.3);
    background: linear-gradient(145deg, #ffffff, #f5f3ff);
  }

  .step-number {
    width: 56px;
    height: 56px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 1.5rem;
    font-weight: bold;
    margin: 0 auto 18px;
    box-shadow: 0 6px 12px rgba(102, 126, 234, 0.25);
    transition: transform 0.3s, box-shadow 0.3s;
  }

  .step-item:hover .step-number {
    transform: scale(1.05);
    box-shadow: 0 8px 18px rgba(102, 126, 234, 0.4);
  }

  .step-icon {
    font-size: 2.4rem;
    color: #667eea;
    margin-bottom: 15px;
    transition: transform 0.3s, color 0.3s;
  }

  .step-item:hover .step-icon {
    transform: translateY(-3px);
    color: #764ba2;
  }

  .step-item h4 {
    font-size: 1.25rem;
    margin-bottom: 10px;
    color: #2d3748;
    font-weight: 600;
  }

  .step-item p {
    color: #4a5568;
    font-size: 0.9rem;
    line-height: 1.5;
  }

  /* 寄养套餐卡片 */
  .package-card {
    background: #ffffff;
    border-radius: 28px;
    padding: 32px 24px;
    text-align: center;
    box-shadow: 0 12px 24px rgba(0, 0, 0, 0.05);
    transition: all 0.4s cubic-bezier(0.2, 0.9, 0.4, 1.1);
    position: relative;
    border: 1px solid #f0f0f0;
    backdrop-filter: blur(0);
    height: 100%;
  }

  .package-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 25px 40px rgba(102, 126, 234, 0.15);
    border-color: rgba(102, 126, 234, 0.3);
  }

  /* 热门套餐 */
  .package-card.popular {
    border: 2px solid #667eea;
    transform: scale(1.02);
    background: linear-gradient(145deg, #ffffff, #faf9ff);
    box-shadow: 0 20px 35px rgba(102, 126, 234, 0.2);
  }

  .package-card.popular:hover {
    transform: scale(1.02) translateY(-6px);
  }

  .popular-badge {
    position: absolute;
    top: -14px;
    right: 24px;
    background: linear-gradient(135deg, #667eea, #764ba2);
    color: white;
    padding: 6px 18px;
    border-radius: 40px;
    font-size: 0.8rem;
    font-weight: 600;
    box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
    letter-spacing: 0.5px;
  }

  .package-card h3 {
    font-size: 1.6rem;
    margin-bottom: 12px;
    color: #2d3748;
    font-weight: 700;
  }

  .price {
    margin-bottom: 24px;
  }

  .price-number {
    font-size: 2.8rem;
    font-weight: 800;
    color: #667eea;
    letter-spacing: -0.5px;
  }

  .price-unit {
    font-size: 1rem;
    color: #6c757d;
    margin-left: 4px;
  }

  .package-features {
    list-style: none;
    padding: 0;
    margin: 28px 0 32px;
    text-align: left;
  }

  .package-features li {
    margin-bottom: 12px;
    color: #4a5568;
    font-size: 0.9rem;
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .package-features i {
    color: #667eea;
    width: 20px;
    font-size: 1rem;
  }

  .btn-primary {
    background: linear-gradient(135deg, #667eea, #764ba2);
    border: none;
    padding: 12px 20px;
    border-radius: 40px;
    color: white;
    font-weight: 600;
    transition: all 0.3s;
    cursor: pointer;
    width: 100%;
    font-size: 1rem;
    letter-spacing: 0.5px;
  }

  .btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
    background: linear-gradient(135deg, #764ba2, #667eea);
  }

  /* 常见问题 */
  .faq-section {
    background: #f8f9ff;   /* 与 Hero 同色系的极浅背景 */
    padding: 80px 0;
  }

  .faq-list {
    max-width: 800px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .faq-item {
    background: #ffffff;
    border-radius: 20px;
    margin-bottom: 16px;
    overflow: hidden;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.04);
    transition: all 0.3s;
    border: 1px solid #f0f0f0;
  }

  .faq-item:hover {
    box-shadow: 0 8px 20px rgba(102, 126, 234, 0.1);
    border-color: rgba(102, 126, 234, 0.2);
  }

  .faq-question {
    padding: 20px 24px;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 14px;
    transition: background 0.2s;
  }

  .faq-question:hover {
    background: #faf9ff;
  }

  .faq-question i:first-child {
    color: #667eea;
    font-size: 1.2rem;
    width: 24px;
  }

  .faq-question span {
    flex: 1;
    font-weight: 600;
    color: #2d3748;
    font-size: 1rem;
  }

  .faq-question i:last-child {
    color: #9ca3af;
    font-size: 0.9rem;
    transition: transform 0.2s;
  }

  .faq-answer {
    padding: 0 24px 24px 62px;
    color: #4a5568;
    line-height: 1.6;
    border-top: 1px solid #f0f0f0;
    background: #ffffff;
    font-size: 0.95rem;
  }

  /* 弹窗样式 */
  .success-content {
    text-align: center;
    padding: 20px;
  }

  .success-icon {
    font-size: 60px;
    color: #67c23a;
    margin-bottom: 20px;
  }

  .success-content h3 {
    margin-bottom: 10px;
    color: var(--dark-color);
  }

  .success-content p {
    color: var(--text-light);
    margin-bottom: 8px;
  }

  .small-text {
    font-size: 12px;
    color: #999;
    margin-top: 15px;
  }

  /* 响应式 */
  @media (max-width: 992px) {
    .process-steps {
      grid-template-columns: repeat(2, 1fr);
      gap: 20px;
    }
    .package-card.popular {
      transform: scale(1);
    }
    .package-card.popular:hover {
      transform: translateY(-5px);
    }
  }

  @media (max-width: 768px) {
    .foster-hero {
      padding: 50px 0;
    }
    .foster-hero h1 {
      font-size: 2rem;
    }
    .process-steps {
      grid-template-columns: 1fr;
      gap: 20px;
    }
    .section-title {
      font-size: 1.8rem;
    }
    .faq-question {
      padding: 15px;
      font-size: 0.9rem;
    }
    .faq-answer {
      padding: 0 15px 15px 45px;
    }
  }

  /* 确保服务卡片三栏并排 */
  .foster-page .row {
    display: flex;
    flex-wrap: wrap;
    margin: 0 -15px;  /* 抵消列的内边距 */
  }

  .foster-page .col-md-4 {
    flex: 0 0 33.333333%;
    max-width: 33.333333%;
    padding: 0 15px;
    box-sizing: border-box;
  }

  /* 小屏幕时改为单栏 */
  @media (max-width: 768px) {
    .foster-page .col-md-4 {
      flex: 0 0 100%;
      max-width: 100%;
      margin-bottom: 20px;
    }
  }
</style>