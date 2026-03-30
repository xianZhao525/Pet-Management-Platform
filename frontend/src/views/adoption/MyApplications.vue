<template>
  <div class="my-applications-page">
    <div class="container mt-4">
      <!-- 面包屑导航 -->
      <nav aria-label="breadcrumb">
        <ol class="breadcrumb">
          <li class="breadcrumb-item"><router-link to="/">首页</router-link></li>
          <li class="breadcrumb-item"><router-link to="/user/dashboard">用户中心</router-link></li>
          <li class="breadcrumb-item active">我的领养申请</li>
        </ol>
      </nav>

      <div class="row">
        <!-- 侧边栏组件 -->
        <div class="col-md-3">
          <AppSidebar />
        </div>

        <!-- 主内容区 -->
        <div class="col-md-9">
          <el-card class="main-card" shadow="never">
            <template #header>
              <div class="card-header">
                <i class="fas fa-file-alt"></i> 我的领养申请
              </div>
            </template>

            <!-- 错误提示 -->
            <el-alert
              v-if="errorMessage"
              :title="errorMessage"
              type="error"
              show-icon
              closable
              @close="errorMessage = ''"
              class="mb-3"
            />

            <!-- 空状态 -->
            <div v-if="applications.length === 0" class="empty-state">
              <i class="fas fa-file-alt fa-4x text-muted mb-3"></i>
              <h5 class="text-muted">暂无领养申请</h5>
              <p class="text-muted mb-4">您还没有提交过领养申请，快去领养一只可爱的宠物吧！</p>
              <el-button type="primary" @click="goToPetList">
                <i class="fas fa-paw"></i> 去领养宠物
              </el-button>
            </div>

            <!-- 申请列表 -->
            <div v-else>
              <div
                v-for="app in applications"
                :key="app.id"
                class="application-card"
                :class="`status-${app.status.toLowerCase()}`"
              >
                <div class="card-body">
                  <div class="row">
                    <!-- 宠物图片 -->
                    <div class="col-md-2 text-center">
                      <img
                        :src="getPetImage(app.pet)"
                        :alt="app.pet.name"
                        class="pet-image-thumb"
                      />
                    </div>
                    <!-- 申请信息 -->
                    <div class="col-md-6">
                      <h5 class="card-title">
                        <router-link :to="`/pet/detail?id=${app.pet.id}`" class="text-decoration-none">
                          {{ app.pet.name }}
                        </router-link>
                      </h5>
                      <div class="mb-2">
                        <el-tag size="small" type="info">{{ app.pet.breed }}</el-tag>
                        <el-tag size="small" type="info" class="ms-1">{{ app.pet.age }}岁</el-tag>
                        <el-tag size="small" type="info" class="ms-1">
                          {{ app.pet.gender === 'MALE' ? '公' : '母' }}
                        </el-tag>
                      </div>
                      <p class="mb-1">
                        <strong>申请理由：</strong>
                        <span class="text-truncate d-inline-block" style="max-width: 300px;">
                          {{ app.applicationReason }}
                        </span>
                      </p>
                      <p class="mb-1"><strong>申请时间：</strong> {{ formatDate(app.applicationDate) }}</p>
                      <p v-if="app.familyMembers" class="mb-1"><strong>家庭成员：</strong> {{ app.familyMembers }}人</p>
                      <p v-if="app.houseType" class="mb-1"><strong>住房类型：</strong> {{ app.houseType }}</p>
                      <p v-if="app.reviewDate" class="mb-1"><strong>审核时间：</strong> {{ formatDate(app.reviewDate) }}</p>
                      <p v-if="app.reviewNotes" class="mb-1"><strong>审核意见：</strong> {{ app.reviewNotes }}</p>
                    </div>
                    <!-- 操作区域 -->
                    <div class="col-md-4 text-end">
                      <div class="mb-3">
                        <el-tag :type="getStatusType(app.status)" size="large">
                          {{ getStatusLabel(app.status) }}
                        </el-tag>
                      </div>
                      <div class="btn-group-vertical w-100">
                        <el-button size="small" plain @click="goToPetDetail(app.pet.id)">
                          <i class="fas fa-eye"></i> 查看宠物详情
                        </el-button>
                        <el-button size="small" plain type="info" @click="goToApplicationDetail(app.id)">
                          <i class="fas fa-info-circle"></i> 申请详情
                        </el-button>
                        <el-button
                          v-if="app.status === 'PENDING'"
                          size="small"
                          type="danger"
                          plain
                          :loading="cancelLoading[app.id]"
                          @click="cancelApplication(app.id)"
                        >
                          <i class="fas fa-times"></i> 取消申请
                        </el-button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </el-card>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
  import { ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { ElMessage } from 'element-plus'
  import AppSidebar from '@/components/common/AppSidebar.vue'
  import { getMyAdoptions, cancelAdoption } from '@/api/adoptionApi'

  const router = useRouter()
  const applications = ref([])
  const errorMessage = ref('')
  const cancelLoading = ref({})

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

  // 获取宠物图片
  const getPetImage = (pet) => {
    if (pet.imageUrl) {
      return `/static/images/pets/${pet.imageUrl}`
    }
    return '/static/images/pets/default.jpg'
  }

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

  // 加载我的申请列表
  const loadApplications = async () => {
    try {
      const response = await getMyAdoptions()
      if (response.code === 200) {
        applications.value = response.data
      } else {
        errorMessage.value = response.message || '加载申请列表失败'
      }
    } catch (error) {
      errorMessage.value = '加载申请列表失败，请稍后重试'
      console.error(error)
    }
  }

  // 取消申请
  const cancelApplication = async (id) => {
    cancelLoading.value[id] = true
    try {
      const response = await cancelAdoption(id)
      if (response.code === 200) {
        ElMessage.success('申请已取消')
        await loadApplications() // 刷新列表
      } else {
        ElMessage.error(response.message || '取消失败')
      }
    } catch (error) {
      ElMessage.error('取消失败，请稍后重试')
      console.error(error)
    } finally {
      cancelLoading.value[id] = false
    }
  }

  // 跳转宠物详情
  const goToPetDetail = (id) => {
    router.push(`/pet/detail?id=${id}`)
  }

  // 跳转申请详情
  const goToApplicationDetail = (id) => {
    router.push(`/adoption/detail/${id}`)
  }

  // 跳转宠物列表
  const goToPetList = () => {
    router.push('/adoption/petAdoption')
  }

  onMounted(() => {
    loadApplications()
  })
</script>

<style scoped>
  @import '@/assets/css/style.css';
  .my-applications-page {
    background-color: #f8f9fa;
    min-height: 100vh;
    padding-bottom: 40px;
  }

  .breadcrumb {
    background: transparent;
    padding: 0;
    margin-bottom: 20px;
  }

  .main-card {
    border-radius: 10px;
    overflow: hidden;
  }

  .card-header {
    font-size: 1.2rem;
    font-weight: 600;
    color: #fff;
    background-color: #409eff;
    padding: 12px 20px;
  }

  .application-card {
    margin-bottom: 20px;
    border-left: 4px solid;
    border-radius: 8px;
    background: white;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  }

  .application-card:hover {
    transform: translateY(-3px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
  }

  .status-pending { border-left-color: #e6a23c; }
  .status-approved { border-left-color: #67c23a; }
  .status-rejected { border-left-color: #f56c6c; }
  .status-completed { border-left-color: #409eff; }
  .status-canceled { border-left-color: #909399; }

  .card-body {
    padding: 20px;
  }

  .pet-image-thumb {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 10px;
  }

  .text-truncate {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  .empty-state {
    text-align: center;
    padding: 60px 20px;
    background: #fafafa;
    border-radius: 8px;
  }

  .ms-1 {
    margin-left: 4px;
  }
</style>