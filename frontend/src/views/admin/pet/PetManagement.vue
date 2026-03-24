<template>
  <div class="pet-management">
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

    <!-- 头部操作栏 -->
    <div class="admin-header">
      <div class="header-title">
        <h1><i class="fas fa-paw"></i> 宠物管理</h1>
        <el-breadcrumb separator="/">
          <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">控制台</el-breadcrumb-item>
          <el-breadcrumb-item>宠物管理</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="goToAdd">
          <!-- <el-icon><Plus /></el-icon>  -->
          添加宠物
        </el-button>
      </div>
    </div>

    <!-- 搜索栏 -->
    <div class="search-form">
      <el-form :inline="true" @submit.prevent="handleSearch" class="search-form-inline">
        <el-form-item>
          <el-input
            v-model="searchParams.keyword"
            placeholder="搜索宠物名称或品种..."
            clearable
            @clear="handleSearch"
            style="width: 250px"
          />
        </el-form-item>
        <el-form-item>
          <el-select
            v-model="searchParams.status"
            placeholder="所有状态"
            clearable
            @change="handleSearch"
            style="width: 150px"
          >
            <el-option label="可领养" value="AVAILABLE" />
            <el-option label="已领养" value="ADOPTED" />
            <el-option label="待处理" value="PENDING" />
            <el-option label="寄养中" value="FOSTERED" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">
            <!-- <el-icon><Search /></el-icon>  -->
            搜索
          </el-button>
        </el-form-item>
        <el-form-item>
          <el-button @click="resetSearch">
            <!-- <el-icon><Refresh /></el-icon>  -->
            重置
          </el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 宠物表格 -->
    <div class="table-container">
      <el-table
        v-loading="loading"
        :data="pets"
        stripe
        border
        style="width: 100%"
        @sort-change="handleSortChange"
      >
        <el-table-column prop="id" label="ID" width="80" sortable="custom" />
        
        <el-table-column label="照片" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="getPetImage(row.imageUrl)" />
          </template>
        </el-table-column>
        
        <el-table-column label="名称" width="150">
          <template #default="{ row }">
            <div>
              <strong>{{ row.name }}</strong>
              <div v-if="row.color" class="text-muted small">颜色: {{ row.color }}</div>
            </div>
          </template>
        </el-table-column>
        
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            {{ getTypeLabel(row.type) }}
          </template>
        </el-table-column>
        
        <el-table-column prop="breed" label="品种" width="120" />
        
        <el-table-column label="年龄" width="80">
          <template #default="{ row }">
            {{ row.age }}岁
          </template>
        </el-table-column>
        
        <el-table-column label="性别" width="70">
          <template #default="{ row }">
            {{ getGenderLabel(row.gender) }}
          </template>
        </el-table-column>
        
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusLabel(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        
        <el-table-column prop="createdAt" label="创建时间" width="120" sortable="custom">
          <template #default="{ row }">
            {{ formatDate(row.createdAt) }}
          </template>
        </el-table-column>
        
        <el-table-column label="操作" width="150" fixed="right">
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
              <el-button
                size="small"
                type="warning"
                link
                @click="editPet(row.id)"
                title="编辑"
              >
                <!-- <el-icon><Edit /></el-icon> -->
              </el-button>
              <el-button
                size="small"
                type="danger"
                link
                @click="deletePet(row.id, row.name)"
                title="删除"
              >
                <!-- <el-icon><Delete /></el-icon> -->
              </el-button>
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
  </div>
</template>

<script setup>
    import { ref, reactive, onMounted } from 'vue'
    import { useRouter } from 'vue-router'
    import { ElMessage, ElMessageBox } from 'element-plus'
    //import { Plus, Search, Refresh, View, Edit, Delete } from '@element-plus/icons-vue'
    import { getPetList, deletePet as deletePetApi } from '@/api/petApi'

    const router = useRouter()

    // 数据
    const pets = ref([])
    const loading = ref(false)
    const total = ref(0)
    const currentPage = ref(1)
    const pageSize = ref(10)
    const sortField = ref('id')
    const sortOrder = ref('descending')
    const successMessage = ref('')
    const errorMessage = ref('')

    // 搜索参数
    const searchParams = reactive({
    keyword: '',
    status: ''
    })

    // 类型映射
    const typeMap = {
    DOG: '狗狗',
    CAT: '猫咪',
    RABBIT: '兔子',
    BIRD: '鸟类',
    OTHER: '其他'
    }

    const getTypeLabel = (type) => {
    return typeMap[type] || '其他'
    }

    // 性别映射
    const getGenderLabel = (gender) => {
    if (gender === 'MALE') return '公'
    if (gender === 'FEMALE') return '母'
    return '未知'
    }

    // 状态映射
    const statusMap = {
    AVAILABLE: { label: '可领养', type: 'success' },
    ADOPTED: { label: '已领养', type: 'danger' },
    PENDING: { label: '待处理', type: 'warning' },
    FOSTERED: { label: '寄养中', type: 'info' }
    }

    const getStatusLabel = (status) => {
    return statusMap[status]?.label || '未知'
    }

    const getStatusType = (status) => {
    return statusMap[status]?.type || 'info'
    }

    // 获取宠物图片
    const getPetImage = (imageUrl) => {
    if (!imageUrl) return '/images/pets/default-pet.jpg'
    if (imageUrl.startsWith('http')) return imageUrl
    return `/images/pets/${imageUrl}`
    }

    // 格式化日期
    const formatDate = (dateStr) => {
    if (!dateStr) return '-'
    const date = new Date(dateStr)
    return date.toLocaleDateString('zh-CN')
    }

    // 加载宠物列表
    const loadPets = async () => {
    loading.value = true
    try {
        const params = {
        page: currentPage.value,
        size: pageSize.value,
        sortField: sortField.value,
        sortOrder: sortOrder.value === 'ascending' ? 'asc' : 'desc',
        keyword: searchParams.keyword || undefined,
        status: searchParams.status || undefined
        }
        
        const response = await getPetList(params)
        if (response.code === 200) {
        pets.value = response.data.list
        total.value = response.data.total
        } else {
        errorMessage.value = response.message || '加载失败'
        }
    } catch (error) {
        errorMessage.value = '加载宠物列表失败'
    } finally {
        loading.value = false
    }
    }

    // 搜索
    const handleSearch = () => {
    currentPage.value = 1
    loadPets()
    }

    // 重置搜索
    const resetSearch = () => {
    searchParams.keyword = ''
    searchParams.status = ''
    currentPage.value = 1
    loadPets()
    }

    // 排序变化
    const handleSortChange = ({ prop, order }) => {
    if (prop) {
        sortField.value = prop
        sortOrder.value = order || 'descending'
        loadPets()
    }
    }

    // 分页变化
    const handleSizeChange = (size) => {
    pageSize.value = size
    currentPage.value = 1
    loadPets()
    }

    const handleCurrentChange = (page) => {
    currentPage.value = page
    loadPets()
    }

    // 跳转添加页面
    const goToAdd = () => {
    router.push('/admin/pets/add')
    }

    // 查看详情
    const viewDetail = (id) => {
    router.push(`/pet/detail/${id}`)
    }

    // 编辑宠物
    const editPet = (id) => {
    router.push(`/admin/pets/edit/${id}`)
    }

    // 删除宠物
    const deletePet = (id, name) => {
    ElMessageBox.confirm(
        `确定要删除宠物"${name}"吗？此操作不可恢复！`,
        '删除确认',
        {
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
        type: 'warning'
        }
    ).then(async () => {
        try {
        const response = await deletePetApi(id)
        if (response.code === 200) {
            successMessage.value = '宠物删除成功'
            loadPets()
        } else {
            errorMessage.value = response.message || '删除失败'
        }
        } catch (error) {
        errorMessage.value = '删除失败，请稍后重试'
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
    loadPets()
    })
</script>

<style scoped>
    .pet-management {
    padding: 20px;
    }

    .admin-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    flex-wrap: wrap;
    gap: 15px;
    }

    .header-title h1 {
    font-size: 1.5rem;
    margin-bottom: 8px;
    color: #333;
    }

    .header-title h1 i {
    margin-right: 10px;
    color: #409eff;
    }

    .header-actions {
    flex-shrink: 0;
    }

    .search-form {
    background: #f8f9fa;
    padding: 15px;
    border-radius: 8px;
    margin-bottom: 20px;
    }

    .search-form-inline {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    gap: 10px;
    }

    .table-container {
    background: white;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .action-buttons {
    display: flex;
    gap: 5px;
    flex-wrap: wrap;
    }

    .pagination-container {
    margin-top: 20px;
    display: flex;
    justify-content: flex-end;
    }

    .text-muted {
    color: #909399;
    }

    .small {
    font-size: 12px;
    }

    .mb-3 {
    margin-bottom: 15px;
    }

    /* 响应式 */
    @media (max-width: 768px) {
    .pet-management {
        padding: 10px;
    }
    
    .admin-header {
        flex-direction: column;
        align-items: flex-start;
    }
    
    .search-form-inline {
        flex-direction: column;
        align-items: stretch;
    }
    
    .search-form-inline .el-form-item {
        width: 100%;
        margin-bottom: 10px;
    }
    
    .search-form-inline .el-input,
    .search-form-inline .el-select {
        width: 100% !important;
    }
    
    .table-container {
        padding: 10px;
        overflow-x: auto;
    }
    
    .action-buttons {
        flex-direction: column;
        gap: 5px;
    }
    }
</style>