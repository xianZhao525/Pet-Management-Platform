<template>
  <div class="admin-dashboard">
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stats-card" v-for="stat in stats" :key="stat.label">
        <div class="stats-icon" :style="{ background: stat.gradient }">
          <i :class="stat.icon"></i>
        </div>
        <div class="stats-number">{{ stat.value }}</div>
        <div class="stats-label">{{ stat.label }}</div>
      </div>
    </div>

    <!-- 快速操作 -->
    <div class="quick-actions">
      <div
        v-for="action in quickActions"
        :key="action.title"
        class="action-btn"
        @click="handleQuickAction(action.path)"
      >
        <div class="action-icon">
          <i :class="action.icon"></i>
        </div>
        <div class="action-title">{{ action.title }}</div>
        <div class="action-desc">{{ action.desc }}</div>
      </div>
    </div>

    <!-- 最近活动 -->
    <div class="recent-activity">
      <h5 class="section-title">
        <i class="fas fa-history"></i> 最近申请
      </h5>

      <div v-if="recentAdoptions.length === 0" class="empty-state">
        <i class="fas fa-inbox fa-3x text-muted mb-3"></i>
        <p class="text-muted">暂无待处理的申请</p>
      </div>

      <div v-else>
        <div
          v-for="adoption in recentAdoptions"
          :key="adoption.id"
          class="activity-item"
        >
          <div class="activity-icon">
            <i class="fas fa-file-alt"></i>
          </div>
          <div class="activity-content">
            <div>
              <strong>{{ adoption.user?.username }}</strong> 申请领养
              <strong>{{ adoption.pet?.name }}</strong>
            </div>
            <div class="activity-time">
              申请时间: {{ formatDate(adoption.applicationDate) }}
            </div>
          </div>
          <div>
            <el-tag type="warning" size="small">待审核</el-tag>
          </div>
        </div>

        <div class="text-center mt-3">
          <el-button type="primary" plain @click="goToAdoptions">
            <!-- <el-icon><List /></el-icon>  -->
            查看所有申请
          </el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
    import { ref, onMounted } from 'vue'
    import { useRouter } from 'vue-router'
    //import { List } from '@element-plus/icons-vue'
    import { getDashboardStats, getRecentAdoptions } from '@/api/adminApi'

    const router = useRouter()

    // 统计数据
    const stats = ref([
    {
        label: '待处理申请',
        value: 0,
        icon: 'fas fa-file-alt',
        gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    },
    {
        label: '可领养宠物',
        value: 0,
        icon: 'fas fa-paw',
        gradient: 'linear-gradient(135deg, #4CAF50 0%, #2E7D32 100%)'
    },
    {
        label: '总领养数',
        value: 0,
        icon: 'fas fa-heart',
        gradient: 'linear-gradient(135deg, #FF9800 0%, #F57C00 100%)'
    },
    {
        label: '注册用户',
        value: 0,
        icon: 'fas fa-users',
        gradient: 'linear-gradient(135deg, #2196F3 0%, #1976D2 100%)'
    }
    ])

    // 快速操作配置
    const quickActions = ref([
    {
        title: '添加新宠物',
        desc: '添加新的待领养宠物',
        icon: 'fas fa-plus-circle',
        path: '/admin/pets/add'
    },
    {
        title: '审核申请',
        desc: '处理用户领养申请',
        icon: 'fas fa-check-circle',
        path: '/admin/adoptions'
    },
    {
        title: '用户管理',
        desc: '管理平台用户',
        icon: 'fas fa-user-cog',
        path: '/admin/users'
    },
    {
        title: '捐赠管理',
        desc: '管理捐赠项目',
        icon: 'fas fa-hand-holding-heart',
        path: '/admin/donations'
    }
    ])

    // 最近申请列表
    const recentAdoptions = ref([])

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

    // 快速操作跳转
    const handleQuickAction = (path) => {
    router.push(path)
    }

    // 跳转到申请列表
    const goToAdoptions = () => {
    router.push('/admin/adoptions')
    }

    // 加载统计数据
    const loadStats = async () => {
    try {
        const response = await getDashboardStats()
        if (response.code === 200) {
        const data = response.data
        stats.value[0].value = data.pendingAdoptions || 0
        stats.value[1].value = data.availablePets || 0
        stats.value[2].value = data.totalAdoptions || 0
        stats.value[3].value = data.totalUsers || 0
        }
    } catch (error) {
        console.error('加载统计数据失败', error)
    }
    }

    // 加载最近申请
    const loadRecentAdoptions = async () => {
    try {
        const response = await getRecentAdoptions()
        if (response.code === 200) {
        recentAdoptions.value = response.data || []
        }
    } catch (error) {
        console.error('加载最近申请失败', error)
    }
    }

    onMounted(() => {
    loadStats()
    loadRecentAdoptions()
    })
</script>

<style scoped>
    .admin-dashboard {
    padding: 20px;
    }

    /* 统计卡片 */
    .stats-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 20px;
    margin-bottom: 30px;
    }

    .stats-card {
    background: white;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    }

    .stats-card:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 20px rgba(0, 0, 0, 0.15);
    }

    .stats-icon {
    width: 60px;
    height: 60px;
    border-radius: 10px;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 24px;
    margin-bottom: 15px;
    color: white;
    }

    .stats-number {
    font-size: 2rem;
    font-weight: bold;
    margin-bottom: 5px;
    color: #333;
    }

    .stats-label {
    color: #666;
    font-size: 0.9rem;
    }

    /* 快速操作 */
    .quick-actions {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 15px;
    margin-bottom: 30px;
    }

    .action-btn {
    background: white;
    border: 2px solid #e0e0e0;
    border-radius: 10px;
    padding: 20px;
    text-align: center;
    transition: all 0.3s ease;
    cursor: pointer;
    text-decoration: none;
    color: #333;
    }

    .action-btn:hover {
    border-color: #667eea;
    transform: translateY(-3px);
    box-shadow: 0 5px 15px rgba(102, 126, 234, 0.2);
    }

    .action-icon {
    font-size: 2rem;
    color: #667eea;
    margin-bottom: 10px;
    }

    .action-title {
    font-weight: 600;
    margin-bottom: 5px;
    }

    .action-desc {
    font-size: 0.8rem;
    color: #999;
    }

    /* 最近活动 */
    .recent-activity {
    background: white;
    border-radius: 10px;
    padding: 20px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .section-title {
    margin-bottom: 20px;
    font-size: 1rem;
    font-weight: 600;
    color: #333;
    }

    .section-title i {
    margin-right: 8px;
    color: #667eea;
    }

    .activity-item {
    display: flex;
    align-items: center;
    padding: 12px 0;
    border-bottom: 1px solid #f0f0f0;
    }

    .activity-item:last-child {
    border-bottom: none;
    }

    .activity-icon {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: #f0f0f0;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
    color: #667eea;
    }

    .activity-content {
    flex: 1;
    }

    .activity-time {
    font-size: 0.8rem;
    color: #999;
    margin-top: 4px;
    }

    .empty-state {
    text-align: center;
    padding: 40px 20px;
    color: #999;
    }

    .text-center {
    text-align: center;
    }

    .mt-3 {
    margin-top: 15px;
    }

    /* 响应式 */
    @media (max-width: 992px) {
    .stats-grid {
        grid-template-columns: repeat(2, 1fr);
    }
    
    .quick-actions {
        grid-template-columns: repeat(2, 1fr);
    }
    }

    @media (max-width: 768px) {
    .admin-dashboard {
        padding: 10px;
    }
    
    .stats-grid {
        grid-template-columns: 1fr;
        gap: 15px;
    }
    
    .quick-actions {
        grid-template-columns: 1fr;
        gap: 10px;
    }
    
    .stats-card {
        padding: 15px;
    }
    
    .stats-number {
        font-size: 1.5rem;
    }
    
    .stats-icon {
        width: 50px;
        height: 50px;
        font-size: 20px;
    }
    }
</style>