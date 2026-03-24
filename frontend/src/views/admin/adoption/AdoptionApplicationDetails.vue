<template>
  <div class="adoption-detail">
    <!-- 错误消息 -->
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      :closable="true"
      show-icon
      @close="errorMessage = ''"
      class="mb-3"
    />

    <div v-loading="loading" class="detail-card">
      <div class="row">
        <!-- 左侧：宠物信息 -->
        <div class="col-md-4">
          <div class="info-group">
            <h4>宠物信息</h4>
            <el-image
              :src="adoption.pet?.imageUrl || defaultPetImage"
              :alt="adoption.pet?.name"
              class="pet-image-large"
              fit="cover"
            />
            
            <div class="info-item">
              <div class="info-label">宠物名称</div>
              <div class="info-value">{{ adoption.pet?.name }}</div>
            </div>
            
            <div class="row">
              <div class="col-6">
                <div class="info-label">品种</div>
                <div class="info-value">{{ adoption.pet?.breed }}</div>
              </div>
              <div class="col-6">
                <div class="info-label">年龄</div>
                <div class="info-value">{{ adoption.pet?.age }}岁</div>
              </div>
            </div>
            
            <div class="row">
              <div class="col-6">
                <div class="info-label">性别</div>
                <div class="info-value">{{ petGenderText }}</div>
              </div>
              <div class="col-6">
                <div class="info-label">状态</div>
                <div class="info-value">
                  <el-tag :type="petStatusType" size="small">
                    {{ petStatusText }}
                  </el-tag>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 右侧：申请信息 -->
        <div class="col-md-8">
          <!-- 申请人信息 -->
          <div class="info-group">
            <h4>申请人信息</h4>
            <div class="row">
              <div class="col-md-6">
                <div class="info-label">用户名</div>
                <div class="info-value">{{ adoption.user?.username }}</div>
              </div>
              <div class="col-md-6">
                <div class="info-label">联系电话</div>
                <div class="info-value">{{ adoption.contactPhone }}</div>
              </div>
            </div>
            
            <div class="info-item">
              <div class="info-label">联系地址</div>
              <div class="info-value">{{ adoption.contactAddress || '-' }}</div>
            </div>
            
            <div class="row">
              <div class="col-md-4">
                <div class="info-label">家庭成员</div>
                <div class="info-value">{{ adoption.familyMembers || '-' }}人</div>
              </div>
              <div class="col-md-4">
                <div class="info-label">住房类型</div>
                <div class="info-value">{{ adoption.houseType || '-' }}</div>
              </div>
              <div class="col-md-4">
                <div class="info-label">养宠经验</div>
                <div class="info-value">
                  {{ adoption.hasPetExperience ? '有经验' : '无经验' }}
                </div>
              </div>
            </div>
          </div>
          
          <!-- 申请信息 -->
          <div class="info-group">
            <h4>申请信息</h4>
            <div class="info-item">
              <div class="info-label">申请状态</div>
              <div class="info-value">
                <el-tag :type="statusType" size="large">
                  {{ statusText }}
                </el-tag>
              </div>
            </div>
            
            <div class="row">
              <div class="col-md-6">
                <div class="info-label">申请时间</div>
                <div class="info-value">{{ formatDate(adoption.applicationDate) }}</div>
              </div>
              <div v-if="adoption.reviewDate" class="col-md-6">
                <div class="info-label">审核时间</div>
                <div class="info-value">{{ formatDate(adoption.reviewDate) }}</div>
              </div>
            </div>
            
            <div class="info-item">
              <div class="info-label">申请理由</div>
              <div class="info-value description-text">{{ adoption.applicationReason }}</div>
            </div>
            
            <div v-if="adoption.familyEnvironment" class="info-item">
              <div class="info-label">家庭环境描述</div>
              <div class="info-value description-text">{{ adoption.familyEnvironment }}</div>
            </div>
          </div>
          
          <!-- 审核信息 -->
          <div v-if="adoption.reviewNotes" class="review-section">
            <h5>审核信息</h5>
            <div class="info-item">
              <div class="info-label">审核意见</div>
              <div class="info-value description-text">{{ adoption.reviewNotes }}</div>
            </div>
            
            <div v-if="adoption.adminId" class="info-item">
              <div class="info-label">审核管理员</div>
              <div class="info-value">ID: {{ adoption.adminId }}</div>
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="action-section">
            <!-- 待审核状态显示审核表单 -->
            <div v-if="adoption.status === 'PENDING'" class="review-section">
              <h5>审核操作</h5>
              <el-form :model="reviewForm" ref="reviewFormRef" label-width="80px">
                <el-form-item label="意见" prop="reviewNotes" :rules="reviewRules">
                  <el-input
                    v-model="reviewForm.reviewNotes"
                    type="textarea"
                    :rows="3"
                    placeholder="请输入审核意见..."
                  />
                </el-form-item>
              </el-form>
              
              <div class="action-buttons">
                <el-button type="success" @click="submitReview('approve')" :loading="submitting">
                  <!-- <el-icon><Check /></el-icon>  -->
                  批准申请
                </el-button>
                <el-button type="danger" @click="submitReview('reject')" :loading="submitting">
                  <!-- <el-icon><Close /></el-icon>  -->
                  拒绝申请
                </el-button>
              </div>
            </div>
            
            <!-- 已批准状态显示完成按钮 -->
            <div v-if="adoption.status === 'APPROVED'" class="review-section">
              <el-button type="primary" @click="completeAdoption" :loading="submitting">
                <!-- <el-icon><Check /></el-icon>  -->
                完成领养
              </el-button>
            </div>
            
            <!-- 返回按钮 -->
            <div class="back-button">
              <el-button @click="goBack">
                <!-- <el-icon><ArrowLeft /></el-icon>  -->
                返回列表
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
    import { ref, computed, onMounted } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import { ElMessage, ElMessageBox } from 'element-plus'
    //import { Check, Close, ArrowLeft } from '@element-plus/icons-vue'
    import { getAdoptionDetail, approveAdoption, rejectAdoption, completeAdoption as completeAdoptionApi } from '@/api/adoptionApi'

    const router = useRouter()
    const route = useRoute()

    const adoptionId = computed(() => route.params.id)

    // 数据
    const adoption = ref({
    id: null,
    pet: {},
    user: {},
    status: '',
    applicationDate: '',
    applicationReason: '',
    contactPhone: '',
    contactAddress: '',
    familyMembers: null,
    houseType: '',
    hasPetExperience: false,
    familyEnvironment: '',
    reviewNotes: '',
    reviewDate: '',
    adminId: null
    })
    const loading = ref(false)
    const submitting = ref(false)
    const errorMessage = ref('')

    // 审核表单
    const reviewForm = ref({
    reviewNotes: ''
    })
    const reviewFormRef = ref(null)

    // 默认宠物图片
    const defaultPetImage = '/images/pets/default-pet.jpg'

    // 验证规则
    const reviewRules = [
    { required: true, message: '请输入审核意见', trigger: 'blur' },
    { min: 5, max: 500, message: '审核意见长度在5-500个字符之间', trigger: 'blur' }
    ]

    // 宠物性别文本
    const petGenderText = computed(() => {
    const gender = adoption.value.pet?.gender
    if (gender === 'MALE') return '公'
    if (gender === 'FEMALE') return '母'
    return '未知'
    })

    // 宠物状态文本和类型
    const petStatusText = computed(() => {
    const status = adoption.value.pet?.status
    const map = {
        AVAILABLE: '可领养',
        ADOPTED: '已领养',
        PENDING: '待处理'
    }
    return map[status] || '未知'
    })

    const petStatusType = computed(() => {
    const status = adoption.value.pet?.status
    const map = {
        AVAILABLE: 'success',
        ADOPTED: 'danger',
        PENDING: 'warning'
    }
    return map[status] || 'info'
    })

    // 申请状态文本和类型
    const statusText = computed(() => {
    const status = adoption.value.status
    const map = {
        PENDING: '待审核',
        APPROVED: '已批准',
        REJECTED: '已拒绝',
        COMPLETED: '已完成',
        CANCELED: '已取消'
    }
    return map[status] || status
    })

    const statusType = computed(() => {
    const status = adoption.value.status
    const map = {
        PENDING: 'warning',
        APPROVED: 'success',
        REJECTED: 'danger',
        COMPLETED: 'primary',
        CANCELED: 'info'
    }
    return map[status] || 'info'
    })

    // 格式化日期
    const formatDate = (dateStr) => {
    if (!dateStr) return '-'
    const date = new Date(dateStr)
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
    })
    }

    // 加载申请详情
    const loadDetail = async () => {
    loading.value = true
    try {
        const response = await getAdoptionDetail(adoptionId.value)
        if (response.code === 200) {
        adoption.value = response.data
        } else {
        errorMessage.value = response.message || '加载失败'
        }
    } catch (error) {
        errorMessage.value = '加载申请详情失败'
    } finally {
        loading.value = false
    }
    }

    // 提交审核
    const submitReview = async (action) => {
    if (!reviewFormRef.value) return
    
    try {
        await reviewFormRef.value.validate()
        
        submitting.value = true
        let response
        
        if (action === 'approve') {
        response = await approveAdoption(adoptionId.value, reviewForm.value.reviewNotes)
        } else {
        response = await rejectAdoption(adoptionId.value, reviewForm.value.reviewNotes)
        }
        
        if (response.code === 200) {
        ElMessage.success(action === 'approve' ? '申请已批准' : '申请已拒绝')
        // 重新加载详情
        await loadDetail()
        // 清空表单
        reviewForm.value.reviewNotes = ''
        } else {
        ElMessage.error(response.message || '操作失败')
        }
    } catch (error) {
        if (error !== 'cancel') {
        ElMessage.error('操作失败，请稍后重试')
        }
    } finally {
        submitting.value = false
    }
    }

    // 完成领养
    const completeAdoption = () => {
    ElMessageBox.confirm('确认完成此领养？', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        submitting.value = true
        try {
        const response = await completeAdoptionApi(adoptionId.value)
        if (response.code === 200) {
            ElMessage.success('领养已完成')
            await loadDetail()
        } else {
            ElMessage.error(response.message || '操作失败')
        }
        } catch (error) {
        ElMessage.error('操作失败，请稍后重试')
        } finally {
        submitting.value = false
        }
    }).catch(() => {})
    }

    // 返回列表
    const goBack = () => {
    router.push('/admin/adoptions')
    }

    onMounted(() => {
    loadDetail()
    })
</script>

<style scoped>
    .adoption-detail {
    padding: 20px;
    }

    .detail-card {
    background: white;
    border-radius: 10px;
    padding: 30px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .pet-image-large {
    width: 100%;
    height: 300px;
    border-radius: 10px;
    margin-bottom: 15px;
    }

    .info-group {
    margin-bottom: 25px;
    padding-bottom: 20px;
    border-bottom: 1px solid #eee;
    }

    .info-group h4 {
    margin-bottom: 20px;
    color: #333;
    font-size: 1.2rem;
    font-weight: 600;
    }

    .info-item {
    margin-bottom: 15px;
    }

    .info-label {
    font-weight: 600;
    color: #666;
    margin-bottom: 5px;
    font-size: 0.85rem;
    }

    .info-value {
    color: #333;
    font-size: 1rem;
    }

    .description-text {
    white-space: pre-wrap;
    line-height: 1.6;
    background: #f8f9fa;
    padding: 12px;
    border-radius: 8px;
    margin-top: 5px;
    }

    .review-section {
    background: #f8f9fa;
    border-radius: 8px;
    padding: 20px;
    margin-top: 20px;
    }

    .review-section h5 {
    margin-bottom: 15px;
    color: #333;
    font-size: 1rem;
    font-weight: 600;
    }

    .action-section {
    margin-top: 20px;
    }

    .action-buttons {
    display: flex;
    gap: 15px;
    margin-top: 15px;
    }

    .back-button {
    margin-top: 20px;
    }

    .mb-3 {
    margin-bottom: 15px;
    }

    /* 响应式 */
    @media (max-width: 768px) {
    .adoption-detail {
        padding: 10px;
    }
    
    .detail-card {
        padding: 20px;
    }
    
    .pet-image-large {
        height: 200px;
    }
    
    .action-buttons {
        flex-direction: column;
    }
    
    .action-buttons .el-button {
        width: 100%;
    }
    
    .info-group {
        margin-bottom: 20px;
    }
    }
</style>