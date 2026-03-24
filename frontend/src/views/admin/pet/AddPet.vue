<template>
  <div class="pet-form-page">
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

    <div class="form-container">
      <el-form
        ref="petFormRef"
        :model="petForm"
        :rules="formRules"
        label-width="100px"
        @submit.prevent="handleSubmit"
      >
        <!-- 基本信息 -->
        <div class="form-section">
          <h3 class="section-title">基本信息</h3>
          
          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="宠物名称" prop="petName" required>
                <el-input v-model="petForm.petName" placeholder="请输入宠物名称" maxlength="100" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="宠物类型" prop="petType" required>
                <el-select v-model="petForm.petType" placeholder="请选择类型" style="width: 100%">
                  <el-option
                    v-for="type in petTypes"
                    :key="type.value"
                    :label="type.label"
                    :value="type.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="当前状态" prop="petStatus" required>
                <el-select v-model="petForm.petStatus" placeholder="请选择状态" style="width: 100%">
                  <el-option
                    v-for="status in petStatuses"
                    :key="status.value"
                    :label="status.label"
                    :value="status.value"
                  />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="品种" prop="petBreed">
                <el-input v-model="petForm.petBreed" placeholder="请输入品种" maxlength="100" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="年龄(岁)" prop="petAge" required>
                <el-input-number
                  v-model="petForm.petAge"
                  :min="0"
                  :max="50"
                  :step="0.5"
                  placeholder="请输入年龄"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="性别" prop="petGender">
                <el-select v-model="petForm.petGender" placeholder="请选择性别" style="width: 100%">
                  <el-option label="公" value="MALE" />
                  <el-option label="母" value="FEMALE" />
                  <el-option label="未知" value="UNKNOWN" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="毛色" prop="petColor">
                <el-input v-model="petForm.petColor" placeholder="请输入毛色" maxlength="50" />
              </el-form-item>
            </el-col>
          </el-row>
        </div>

        <!-- 详细信息 -->
        <div class="form-section">
          <h3 class="section-title">详细信息</h3>
          
          <el-form-item label="宠物描述" prop="petDescription">
            <el-input
              v-model="petForm.petDescription"
              type="textarea"
              :rows="4"
              maxlength="1000"
              placeholder="请输入宠物描述"
              show-word-limit
            />
          </el-form-item>

          <el-row :gutter="20">
            <el-col :span="12">
              <el-form-item label="健康状况" prop="healthStatus">
                <el-input v-model="petForm.healthStatus" placeholder="请输入健康状况" maxlength="200" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="疫苗接种情况" prop="vaccination">
                <el-input v-model="petForm.vaccination" placeholder="请输入疫苗接种情况" maxlength="200" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="图片URL" prop="imageUrl">
            <el-input
              v-model="petForm.imageUrl"
              placeholder="请输入图片URL或留空使用默认图片"
              @input="handleImageUrlChange"
            />
            <div class="form-text text-muted">示例：dog1.jpg、cat1.jpg，或完整的URL</div>
          </el-form-item>

          <div class="image-preview">
            <el-image
              v-if="previewUrl"
              :src="previewUrl"
              fit="cover"
              class="preview-img"
            >
              <template #error>
                <div class="image-placeholder">
                  <!-- <el-icon><Picture /></el-icon> -->
                  <p>图片加载失败</p>
                </div>
              </template>
            </el-image>
            <div v-else class="image-placeholder">
              <!-- <el-icon><Picture /></el-icon> -->
              <p>图片预览</p>
            </div>
          </div>
        </div>

        <!-- 其他信息 -->
        <div class="form-section">
          <h3 class="section-title">其他信息</h3>
          
          <el-form-item label="主人ID" prop="ownerId">
            <el-input-number
              v-model="petForm.ownerId"
              :min="1"
              placeholder="请输入主人用户ID"
              style="width: 100%"
            />
            <div class="form-text text-muted">如果有主人，请输入用户的ID</div>
          </el-form-item>
        </div>

        <!-- 按钮区域 -->
        <div class="form-actions">
          <el-button @click="goBack">
            <!-- <el-icon><ArrowLeft /></el-icon>  -->
            取消返回
          </el-button>
          <el-button type="primary" native-type="submit" :loading="submitting">
            <!-- <el-icon><Save /></el-icon> -->
             {{ isEdit ? '更新信息' : '添加宠物' }}
          </el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
    import { ref, reactive, computed, onMounted } from 'vue'
    import { useRouter, useRoute } from 'vue-router'
    import { ElMessage } from 'element-plus'
    //import { ArrowLeft, Save, Picture } from '@element-plus/icons-vue'
    import { getPetDetail, addPet, updatePet } from '@/api/petApi'

    const router = useRouter()
    const route = useRoute()

    const petId = computed(() => route.params.id)
    const isEdit = computed(() => !!petId.value)

    // 表单数据
    const petForm = reactive({
    petId: null,
    petName: '',
    petType: '',
    petStatus: '',
    petBreed: '',
    petAge: null,
    petGender: '',
    petColor: '',
    petDescription: '',
    healthStatus: '',
    vaccination: '',
    imageUrl: '',
    ownerId: null
    })

    // 宠物类型选项
    const petTypes = ref([
    { value: 'DOG', label: '狗狗' },
    { value: 'CAT', label: '猫咪' },
    { value: 'RABBIT', label: '兔子' },
    { value: 'BIRD', label: '鸟类' },
    { value: 'OTHER', label: '其他' }
    ])

    // 宠物状态选项
    const petStatuses = ref([
    { value: 'AVAILABLE', label: '可领养' },
    { value: 'ADOPTED', label: '已领养' },
    { value: 'PENDING', label: '待处理' },
    { value: 'FOSTERED', label: '寄养中' },
    { value: 'PENDING_ADOPTION', label: '待领养' }
    ])

    // 图片预览
    const previewUrl = ref('')
    const defaultPetImage = '/images/pets/default-pet.jpg'

    // 表单验证规则
    const formRules = {
    petName: [
        { required: true, message: '请输入宠物名称', trigger: 'blur' },
        { max: 100, message: '宠物名称不能超过100个字符', trigger: 'blur' }
    ],
    petType: [
        { required: true, message: '请选择宠物类型', trigger: 'change' }
    ],
    petStatus: [
        { required: true, message: '请选择宠物状态', trigger: 'change' }
    ],
    petAge: [
        { required: true, message: '请输入宠物年龄', trigger: 'blur' },
        { type: 'number', min: 0, max: 50, message: '年龄必须在0-50岁之间', trigger: 'blur' }
    ]
    }

    const petFormRef = ref(null)
    const submitting = ref(false)
    const loading = ref(false)
    const errorMessage = ref('')

    // 处理图片URL变化
    const handleImageUrlChange = (url) => {
    if (url) {
        if (url.startsWith('http')) {
        previewUrl.value = url
        } else {
        previewUrl.value = `/images/pets/${url}`
        }
    } else {
        previewUrl.value = ''
    }
    }

    // 加载宠物详情（编辑模式）
    const loadPetDetail = async () => {
    if (!isEdit.value) return
    
    loading.value = true
    try {
        const response = await getPetDetail(petId.value)
        if (response.code === 200) {
        const data = response.data
        Object.assign(petForm, {
            petId: data.id,
            petName: data.name,
            petType: data.type,
            petStatus: data.status,
            petBreed: data.breed,
            petAge: data.age,
            petGender: data.gender,
            petColor: data.color,
            petDescription: data.description,
            healthStatus: data.healthStatus,
            vaccination: data.vaccination,
            imageUrl: data.imageUrl,
            ownerId: data.ownerId
        })
        
        if (data.imageUrl) {
            handleImageUrlChange(data.imageUrl)
        }
        } else {
        errorMessage.value = response.message || '加载宠物信息失败'
        }
    } catch (error) {
        errorMessage.value = '加载宠物信息失败'
    } finally {
        loading.value = false
    }
    }

    // 提交表单
    const handleSubmit = async () => {
    if (!petFormRef.value) return
    
    try {
        await petFormRef.value.validate()
        
        submitting.value = true
        const submitData = {
        petName: petForm.petName,
        petType: petForm.petType,
        petStatus: petForm.petStatus,
        petBreed: petForm.petBreed,
        petAge: petForm.petAge,
        petGender: petForm.petGender,
        petColor: petForm.petColor,
        petDescription: petForm.petDescription,
        healthStatus: petForm.healthStatus,
        vaccination: petForm.vaccination,
        imageUrl: petForm.imageUrl,
        ownerId: petForm.ownerId
        }
        
        let response
        if (isEdit.value) {
        response = await updatePet(petId.value, submitData)
        } else {
        response = await addPet(submitData)
        }
        
        if (response.code === 200) {
        ElMessage.success(isEdit.value ? '宠物信息更新成功' : '宠物添加成功')
        router.push('/admin/pets')
        } else {
        ElMessage.error(response.message || (isEdit.value ? '更新失败' : '添加失败'))
        }
    } catch (error) {
        if (error !== 'cancel') {
        ElMessage.error('操作失败，请稍后重试')
        }
    } finally {
        submitting.value = false
    }
    }

    // 返回列表
    const goBack = () => {
    router.push('/admin/pets')
    }

    onMounted(() => {
    loadPetDetail()
    })
</script>

<style scoped>
    .pet-form-page {
    padding: 20px;
    }

    .form-container {
    background: white;
    border-radius: 10px;
    padding: 30px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
    }

    .form-section {
    margin-bottom: 30px;
    padding-bottom: 20px;
    border-bottom: 1px solid #eee;
    }

    .form-section:last-child {
    border-bottom: none;
    margin-bottom: 0;
    padding-bottom: 0;
    }

    .section-title {
    font-size: 1.2rem;
    font-weight: 600;
    color: #4361ee;
    margin-bottom: 20px;
    padding-bottom: 10px;
    border-bottom: 2px solid #f0f0f0;
    }

    .image-preview {
    width: 200px;
    height: 200px;
    border: 2px dashed #ddd;
    border-radius: 10px;
    overflow: hidden;
    background-color: #f8f9fa;
    margin-top: 10px;
    }

    .preview-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    }

    .image-placeholder {
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #999;
    }

    .image-placeholder .el-icon {
    font-size: 48px;
    margin-bottom: 10px;
    }

    .image-placeholder p {
    font-size: 12px;
    margin: 0;
    }

    .form-text {
    font-size: 12px;
    color: #909399;
    margin-top: 4px;
    }

    .text-muted {
    color: #909399;
    }

    .form-actions {
    display: flex;
    justify-content: flex-end;
    gap: 15px;
    margin-top: 20px;
    padding-top: 20px;
    border-top: 1px solid #eee;
    }

    .mb-3 {
    margin-bottom: 15px;
    }

    /* 响应式 */
    @media (max-width: 768px) {
    .pet-form-page {
        padding: 10px;
    }
    
    .form-container {
        padding: 20px;
    }
    
    .form-actions {
        flex-direction: column;
    }
    
    .form-actions .el-button {
        width: 100%;
    }
    
    .image-preview {
        width: 150px;
        height: 150px;
    }
    }
</style>