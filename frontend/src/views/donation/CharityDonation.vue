<template>
  <div class="donation-page">
    <!-- Hero区域 -->
    <div class="donation-hero">
      <div class="container">
        <h1 class="display-4">❤️ 爱心捐赠</h1>
        <p class="lead">每一份爱心，都能为流浪动物带来希望</p>
      </div>
    </div>

    <!-- 统计信息卡片 -->
    <div class="container">
      <div class="donation-stats">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-number">¥{{ formatNumber(stats.totalDonation) }}</div>
            <div>累计捐款</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ stats.totalDonors }}</div>
            <div>爱心人士</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ stats.rescuedAnimals }}</div>
            <div>获救动物</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 捐赠方式卡片 -->
    <div class="container my-5">
      <div class="row">
        <div class="col-md-6 mb-4">
          <div class="card h-100 donation-card">
            <img 
              src="https://images.unsplash.com/photo-1532629345422-7515f3d16bb6?w=500" 
              class="card-img-top" 
              alt="物资捐赠"
            >
            <div class="card-body">
              <h5 class="card-title">📦 物资捐赠</h5>
              <p class="card-text">捐赠宠物粮食、用品、药品等物资</p>
              <button class="btn btn-primary" @click="openDonationModal('material')">
                我要捐赠物资
              </button>
            </div>
          </div>
        </div>
        
        <div class="col-md-6 mb-4">
          <div class="card h-100 donation-card">
            <img 
              src="https://ts3.tc.mm.bing.net/th/id/OIP-C.6djlxUWf3kcBW1LioT5rwAHaHa?cb=ucfimg2&ucfimg=1&rs=1&pid=ImgDetMain&o=7&rm=3" 
              class="card-img-top" 
              alt="资金捐赠"
            >
            <div class="card-body">
              <h5 class="card-title">💰 资金捐赠</h5>
              <p class="card-text">直接捐款支持流浪动物救助工作</p>
              <button class="btn btn-primary" @click="openDonationModal('money')">
                我要捐款
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 捐赠表单弹窗 -->
    <el-dialog 
      v-model="donationModalVisible" 
      :title="donationType === 'money' ? '资金捐赠' : '物资捐赠'"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="donationForm" :rules="donationRules" ref="donationFormRef" label-width="100px">
        <el-form-item label="捐赠金额" prop="amount" v-if="donationType === 'money'">
          <div class="amount-buttons">
            <el-button 
              v-for="amount in [50, 100, 200, 500]" 
              :key="amount"
              :type="donationForm.amount === amount ? 'primary' : 'default'"
              @click="donationForm.amount = amount"
            >
              ¥{{ amount }}
            </el-button>
            <el-input 
              v-model="donationForm.customAmount" 
              placeholder="自定义金额"
              style="width: 120px; margin-left: 10px;"
              @input="handleCustomAmount"
            />
          </div>
        </el-form-item>

        <el-form-item label="捐赠人姓名" prop="donorName">
          <el-input v-model="donationForm.donorName" placeholder="请输入您的姓名" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="donationForm.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="电子邮箱" prop="email">
          <el-input v-model="donationForm.email" placeholder="请输入电子邮箱" />
        </el-form-item>

        <el-form-item label="捐赠留言" prop="message">
          <el-input 
            v-model="donationForm.message" 
            type="textarea" 
            :rows="3"
            placeholder="请留下您的祝福或留言"
          />
        </el-form-item>

        <el-form-item v-if="donationType === 'material'" label="物资类型" prop="materialType">
          <el-select v-model="donationForm.materialType" placeholder="请选择物资类型">
            <el-option label="宠物粮食" value="food" />
            <el-option label="宠物用品" value="supplies" />
            <el-option label="药品" value="medicine" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>

        <el-form-item v-if="donationType === 'material'" label="物资数量" prop="materialQuantity">
          <el-input-number 
            v-model="donationForm.materialQuantity" 
            :min="1" 
            :max="999"
            placeholder="请输入数量"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="donationModalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitDonation" :loading="submitting">
            {{ submitting ? '提交中...' : '确认捐赠' }}
          </el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 捐赠成功弹窗 -->
    <el-dialog v-model="successModalVisible" title="捐赠成功" width="400px" center>
      <div class="success-content">
        <i class="fas fa-check-circle success-icon"></i>
        <h3>感谢您的爱心！</h3>
        <p>您的捐赠将为流浪动物带来希望</p>
        <p class="small-text">我们已收到您的捐赠信息，工作人员会尽快与您联系确认。</p>
      </div>
      <template #footer>
        <el-button type="primary" @click="successModalVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
    import { ref, reactive } from 'vue'
    import { ElMessage } from 'element-plus'
    import { submitDonation as apiSubmitDonation, getDonationStats } from '@/api/donationApi'

    // 统计数据
    const stats = ref({
    totalDonation: 128560,
    totalDonors: 1256,
    rescuedAnimals: 342
    })

    // 弹窗状态
    const donationModalVisible = ref(false)
    const successModalVisible = ref(false)
    const donationType = ref('money')
    const submitting = ref(false)
    const donationFormRef = ref(null)

    // 捐赠表单
    const donationForm = reactive({
    amount: null,
    customAmount: '',
    donorName: '',
    phone: '',
    email: '',
    message: '',
    materialType: '',
    materialQuantity: 1
    })

    // 表单验证规则
    const donationRules = {
    donorName: [
        { required: true, message: '请输入捐赠人姓名', trigger: 'blur' },
        { min: 2, max: 20, message: '姓名长度在2-20个字符之间', trigger: 'blur' }
    ],
    phone: [
        { required: true, message: '请输入联系电话', trigger: 'blur' },
        { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入电子邮箱', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    materialType: [
        { required: true, message: '请选择物资类型', trigger: 'change' }
    ],
    materialQuantity: [
        { required: true, message: '请输入物资数量', trigger: 'blur' },
        { type: 'number', min: 1, message: '数量必须大于0', trigger: 'blur' }
    ]
    }

    // 金额相关验证
    const amountValidator = (rule, value, callback) => {
    const amount = donationForm.amount
    const customAmount = donationForm.customAmount
    if (!amount && !customAmount) {
        callback(new Error('请选择或输入捐赠金额'))
    } else if (customAmount && (isNaN(customAmount) || customAmount < 1)) {
        callback(new Error('请输入有效的金额'))
    } else {
        callback()
    }
    }

    // 为资金捐赠添加动态验证
    const getDonationRules = () => {
    if (donationType.value === 'money') {
        return {
        ...donationRules,
        amount: [{ validator: amountValidator, trigger: 'change' }]
        }
    }
    return donationRules
    }

    // 格式化数字
    const formatNumber = (num) => {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
    }

    // 处理自定义金额
    const handleCustomAmount = (value) => {
    if (value) {
        donationForm.amount = null
    }
    }

    // 打开捐赠弹窗
    const openDonationModal = (type) => {
    donationType.value = type
    // 重置表单
    donationForm.amount = null
    donationForm.customAmount = ''
    donationForm.donorName = ''
    donationForm.phone = ''
    donationForm.email = ''
    donationForm.message = ''
    donationForm.materialType = ''
    donationForm.materialQuantity = 1
    donationModalVisible.value = true
    }

    // 提交捐赠
    const submitDonation = async () => {
    // 获取实际金额
    let finalAmount = donationForm.amount
    if (donationForm.customAmount) {
        finalAmount = parseFloat(donationForm.customAmount)
    }
    
    const submitData = {
        type: donationType.value,
        amount: donationType.value === 'money' ? finalAmount : null,
        donorName: donationForm.donorName,
        phone: donationForm.phone,
        email: donationForm.email,
        message: donationForm.message,
        ...(donationType.value === 'material' && {
        materialType: donationForm.materialType,
        materialQuantity: donationForm.materialQuantity
        })
    }
    
    submitting.value = true
    try {
        const response = await apiSubmitDonation(submitData)
        if (response.code === 200) {
        donationModalVisible.value = false
        successModalVisible.value = true
        // 更新统计数据
        loadStats()
        } else {
        ElMessage.error(response.message || '捐赠失败')
        }
    } catch (error) {
        ElMessage.error('提交失败，请稍后重试')
    } finally {
        submitting.value = false
    }
    }

    // 加载统计数据
    const loadStats = async () => {
    try {
        const response = await getDonationStats()
        if (response.code === 200) {
        stats.value = response.data
        }
    } catch (error) {
        console.error('加载统计数据失败', error)
    }
    }

    // 页面加载时获取统计数据
    loadStats()
</script>

<style scoped>
  /* Hero 区域（保持不变） */
  .donation-hero {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
    color: white;
    padding: 80px 0;
    text-align: center;
  }

  .donation-hero h1 {
    margin-bottom: 20px;
    font-size: 3rem;
  }

  .donation-hero .lead {
    font-size: 1.2rem;
    opacity: 0.95;
  }

  /* 统计卡片（保持不变） */
  .donation-stats {
    background: white;
    border-radius: 15px;
    padding: 30px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
    margin: -50px auto 50px;
    max-width: 800px;
    position: relative;
    z-index: 10;
  }

  .stats-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
  }

  .stat-item {
    text-align: center;
    padding: 20px;
  }

  .stat-number {
    font-size: 2.5rem;
    font-weight: bold;
    color: #f5576c;
    margin-bottom: 8px;
  }

  .stat-item div:last-child {
    color: #666;
    font-size: 0.95rem;
  }

  /* ========== 捐赠卡片两栏布局（优化） ========== */
  .container.my-5 .row {
    display: flex;
    flex-wrap: wrap;
    margin: 0 -15px; /* 抵消列内边距 */
  }

  .container.my-5 .col-md-6 {
    flex: 0 0 50%;
    max-width: 50%;
    padding: 0 15px;
    box-sizing: border-box;
  }

  .donation-card {
    border: none;
    border-radius: 20px;
    overflow: hidden;
    background: white;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.05);
    transition: all 0.3s ease;
    height: 100%;
    display: flex;
    flex-direction: column;
  }

  .donation-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 20px 35px rgba(0, 0, 0, 0.12);
  }

  .donation-card .card-img-top {
    height: 220px;
    object-fit: cover;
    width: 100%;
    transition: transform 0.5s ease;
  }

  .donation-card:hover .card-img-top {
    transform: scale(1.03);
  }

  .donation-card .card-body {
    padding: 25px 20px 30px;
    text-align: center;
    flex: 1;
    display: flex;
    flex-direction: column;
  }

  .donation-card .card-title {
    font-size: 1.6rem;
    margin-bottom: 12px;
    color: #333;
    font-weight: 600;
  }

  .donation-card .card-text {
    color: #666;
    margin-bottom: 25px;
    line-height: 1.5;
    flex: 1;
  }

  /* 按钮样式与 Hero 渐变色一致 */
  .btn-primary {
    background: linear-gradient(135deg, #f093fb, #f5576c);
    border: none;
    padding: 10px 25px;
    border-radius: 40px;
    font-weight: 500;
    color: white;
    transition: all 0.3s ease;
    align-self: center;
    width: auto;
    min-width: 140px;
  }

  .btn-primary:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 18px rgba(245, 87, 108, 0.35);
    background: linear-gradient(135deg, #f5576c, #f093fb);
  }

  /* 弹窗样式（保持不变） */
  .amount-buttons {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    align-items: center;
  }

  .amount-buttons .el-button {
    margin: 0;
  }

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
    color: #333;
  }

  .success-content p {
    color: #666;
    margin-bottom: 8px;
  }

  .success-content .small-text {
    font-size: 12px;
    color: #999;
    margin-top: 15px;
  }

  /* 响应式：小屏幕堆叠 */
  @media (max-width: 768px) {
    .donation-hero {
      padding: 50px 0;
    }
    .donation-hero h1 {
      font-size: 2rem;
    }
    .stats-grid {
      grid-template-columns: 1fr;
      gap: 0;
    }
    .stat-item {
      border-bottom: 1px solid #eee;
    }
    .stat-item:last-child {
      border-bottom: none;
    }
    .stat-number {
      font-size: 1.8rem;
    }
    .container.my-5 .col-md-6 {
      flex: 0 0 100%;
      max-width: 100%;
      margin-bottom: 30px;
    }
    .donation-card .card-img-top {
      height: 180px;
    }
    .donation-card .card-title {
      font-size: 1.4rem;
    }
    .btn-primary {
      padding: 8px 20px;
      font-size: 0.9rem;
    }
  }
</style>