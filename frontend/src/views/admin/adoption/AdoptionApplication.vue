<template>
  <div class="adoption-management">
    <!-- 成功/错误消息 -->
    <el-alert
      v-if="successMessage"
      :title="successMessage"
      type="success"
      :closable="true"
      show-icon
      @close="successMessage = ''"
      class="mb-3"
    />
    <el-alert
      v-if="errorMessage"
      :title="errorMessage"
      type="error"
      :closable="true"
      show-icon
      @close="errorMessage = ''"
      class="mb-3"
    />

    <!-- 状态筛选 -->
    <div class="filter-badges">
      <el-button
        v-for="status in statusFilters"
        :key="status.value"
        :type="currentStatus === status.value ? 'primary' : 'default'"
        :size="'small'"
        @click="filterByStatus(status.value)"
        class="filter-badge"
      >
        {{ status.label }} ({{ getStatusCount(status.value) }})
      </el-button>
    </div>

    <!-- 申请表格 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="filteredAdoptions"
        stripe
        border
        style="width: 100%"
        @sort-change="handleSortChange"
      >
        <el-table-column prop="id" label="ID" width="80" sortable="custom" />
        
        <el-table-column label="宠物" width="180">
          <template #default="{ row }">
            <div class="d-flex align-items-center">
              <el-avatar :size="40" :src="row.pet?.imageUrl || defaultPetImage" />
              <div class="ml-2">
                <div><strong>{{ row.pet?.name }}</strong></div>
                <small class="text-muted">{{ row.pet?.breed }}</small>
              </div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="申请人" width="150">
          <template #default="{ row }">
            <div>{{ row.user?.username }}</div>
            <small class="text-muted">{{ row.contactPhone }}</small>
          </template>
        </el-table-column>
        
        <el-table-column prop="applicationDate" label="申请时间" width="160" sortable="custom">
          <template #default="{ row }">
            {{ formatDate(row.applicationDate) }}
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="reviewDate" label="审核时间" width="160" sortable="custom">
          <template #default="{ row }">
            {{ row.reviewDate ? formatDate(row.reviewDate) : '-' }}
          </template>
        </el-table-column>
        
        <el-table-column label="审核意见" width="150">
          <template #default="{ row }">
            <el-tooltip
              v-if="row.reviewNotes"
              :content="row.reviewNotes"
              placement="top"
              :disabled="row.reviewNotes.length <= 20"
            >
              <span class="text-truncate">{{ row.reviewNotes }}</span>
            </el-tooltip>
            <span v-else>-</span>
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button
                size="small"
                type="info"
                link
                @click="viewDetail(row.id)"
                title="查看详情"
              >
                <!-- <el-icon><View /></el-icon> -->
              </el-button>
              
              <template v-if="row.status === 'PENDING'">
                <el-button
                  size="small"
                  type="success"
                  link
                  @click="openReviewModal(row, 'approve')"
                  title="批准申请"
                >
                  <!-- <el-icon><Check /></el-icon> -->
                </el-button>
                
                <el-button
                  size="small"
                  type="danger"
                  link
                  @click="openReviewModal(row, 'reject')"
                  title="拒绝申请"
                >
                  <!-- <el-icon><Close /></el-icon> -->
                </el-button>
              </template>
              
              <template v-if="row.status === 'APPROVED'">
                <el-button
                  size="small"
                  type="primary"
                  link
                  @click="completeAdoption(row.id)"
                  title="完成领养"
                >
                  <!-- <el-icon><Check /></el-icon> -->
                </el-button>
              </template>
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :page-sizes="[10, 20, 50, 100]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <!-- 审核意见弹窗 -->
    <el-dialog
      v-model="reviewModalVisible"
      :title="reviewModalTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="审核意见">
          <el-input
            v-model="reviewForm.reviewNotes"
            type="textarea"
            :rows="4"
            placeholder="请输入审核意见..."
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewModalVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReview" :loading="reviewSubmitting">
            确认
          </el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
    import { ref, computed, onMounted } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage, ElMessageBox } from 'element-plus'
    //import { View, Check, Close } from '@element-plus/icons-vue'
    import { getAdoptions, approveAdoption, rejectAdoption, completeAdoption } from '@/api/adoptionApi'

    const router = useRouter()

    // 数据
    const adoptions = ref([])
    const loading = ref(false)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const total = ref(0)
    const currentStatus = ref('')
    const sortField = ref('id')
    const sortOrder = ref('descending')
    const successMessage = ref('')
    const errorMessage = ref('')

    // 审核弹窗
    const reviewModalVisible = ref(false)
    const reviewSubmitting = ref(false)
    const currentAdoption = ref(null)
    const reviewAction = ref('')
    const reviewForm = ref({
    reviewNotes: ''
    })

    // 状态筛选配置
    const statusFilters = [
    { value: '', label: '全部' },
    { value: 'PENDING', label: '待审核' },
    { value: 'APPROVED', label: '已批准' },
    { value: 'REJECTED', label: '已拒绝' },
    { value: 'COMPLETED', label: '已完成' },
    { value: 'CANCELED', label: '已取消' }
    ]

    // 默认宠物图片
    const defaultPetImage = '/images/pets/default-pet.jpg'

    // 计算属性
    const filteredAdoptions = computed(() => adoptions.value)

    // 状态标签映射
    const getStatusLabel = (status) => {
    const map = {
        PENDING: '待审核',
        APPROVED: '已批准',
        REJECTED: '已拒绝',
        COMPLETED: '已完成',
        CANCELED: '已取消'
    }
    return map[status] || status
    }

    // 状态类型映射
    const getStatusType = (status) => {
    const map = {
        PENDING: 'warning',
        APPROVED: 'success',
        REJECTED: 'danger',
        COMPLETED: 'primary',
        CANCELED: 'info'
    }
    return map[status] || 'info'
    }

    // 获取各状态数量
    const getStatusCount = (statusValue) => {
    if (!statusValue) return adoptions.value.length
    return adoptions.value.filter(a => a.status === statusValue).length
    }

    // 格式化日期
    const formatDate = (dateStr) => {
    if (!dateStr) return '-'
    const date = new Date(dateStr)
    return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    })
    }

    // 加载申请列表
    const loadAdoptions = async () => {
    loading.value = true
    try {
        const params = {
        page: currentPage.value,
        size: pageSize.value,
        status: currentStatus.value || undefined,
        sortField: sortField.value,
        sortOrder: sortOrder.value === 'ascending' ? 'asc' : 'desc'
        }
        
        const response = await getAdoptions(params)
        if (response.code === 200) {
        adoptions.value = response.data.list
        total.value = response.data.total
        } else {
        errorMessage.value = response.message || '加载失败'
        }
    } catch (error) {
        errorMessage.value = '加载申请列表失败'
    } finally {
        loading.value = false
    }
    }

    // 按状态筛选
    const filterByStatus = (status) => {
    currentStatus.value = status
    currentPage.value = 1
    loadAdoptions()
    }

    // 排序变化
    const handleSortChange = ({ prop, order }) => {
    if (prop) {
        sortField.value = prop
        sortOrder.value = order || 'descending'
        loadAdoptions()
    }
    }

    // 分页变化
    const handleSizeChange = (size) => {
    pageSize.value = size
    currentPage.value = 1
    loadAdoptions()
    }

    const handleCurrentChange = (page) => {
    currentPage.value = page
    loadAdoptions()
    }

    // 查看详情
    const viewDetail = (id) => {
    router.push(`/admin/adoptions/detail/${id}`)
    }

    // 打开审核弹窗
    const openReviewModal = (adoption, action) => {
    currentAdoption.value = adoption
    reviewAction.value = action
    reviewForm.value.reviewNotes = ''
    reviewModalTitle.value = action === 'approve' ? '批准领养申请' : '拒绝领养申请'
    reviewModalVisible.value = true
    }

    // 提交审核
    const submitReview = async () => {
    if (!reviewForm.value.reviewNotes.trim()) {
        ElMessage.warning('请输入审核意见')
        return
    }
    
    reviewSubmitting.value = true
    try {
        let response
        if (reviewAction.value === 'approve') {
        response = await approveAdoption(currentAdoption.value.id, reviewForm.value.reviewNotes)
        } else {
        response = await rejectAdoption(currentAdoption.value.id, reviewForm.value.reviewNotes)
        }
        
        if (response.code === 200) {
        successMessage.value = reviewAction.value === 'approve' ? '申请已批准' : '申请已拒绝'
        reviewModalVisible.value = false
        loadAdoptions()
        } else {
        errorMessage.value = response.message || '操作失败'
        }
    } catch (error) {
        errorMessage.value = '操作失败，请稍后重试'
    } finally {
        reviewSubmitting.value = false
    }
    }

    // 完成领养
    const completeAdoption = (id) => {
    ElMessageBox.confirm('确认完成此领养？', '提示', {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: 'warning'
    }).then(async () => {
        try {
        const response = await completeAdoption(id)
        if (response.code === 200) {
            successMessage.value = '领养已完成'
            loadAdoptions()
        } else {
            errorMessage.value = response.message || '操作失败'
        }
        } catch (error) {
        errorMessage.value = '操作失败，请稍后重试'
        }
    }).catch(() => {})
    }

    // 自动隐藏消息
    let messageTimer = null
    const autoHideMessage = () => {
    if (messageTimer) clearTimeout(messageTimer)
    if (successMessage.value || errorMessage.value) {
        messageTimer = setTimeout(() => {
        successMessage.value = ''
        errorMessage.value = ''
        }, 5000)
    }
    }

    // 监听消息变化
    import { watch } from 'vue'
    watch([successMessage, errorMessage], () => {
    autoHideMessage()
    })

    onMounted(() => {
    loadAdoptions()
    })
</script>

<style scoped>
    .adoption-management {
    padding: 20px;
    }

    .filter-badges {
    margin-bottom: 20px;
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    }

    .filter-badge {
    cursor: pointer;
    }

    .table-container {
    background: white;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .d-flex {
    display: flex;
    }

    .align-items-center {
    align-items: center;
    }

    .ml-2 {
    margin-left: 8px;
    }

    .text-muted {
    color: #909399;
    font-size: 12px;
    }

    .text-truncate {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
    display: inline-block;
    max-width: 150px;
    }

    .action-buttons {
    display: flex;
    gap: 8px;
    flex-wrap: wrap;
    }

    .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    }

    .mb-3 {
    margin-bottom: 15px;
    }

    /* 响应式 */
    @media (max-width: 768px) {
    .adoption-management {
        padding: 10px;
    }
    
    .table-container {
        padding: 10px;
        overflow-x: auto;
    }
    
    .action-buttons {
        flex-direction: column;
        gap: 5px;
    }
    
    .filter-badges {
        gap: 5px;
    }
    }
</style>