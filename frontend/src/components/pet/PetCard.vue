<template>
  <div class="pet-card">
    <div class="pet-image-container">
      <img 
        :src="pet.image || '/images/pets/default.jpg'" 
        :alt="pet.name" 
        class="pet-image"
        @error="handleImageError"
      >
      <span class="pet-type-badge" :class="petTypeClass">
        {{ petTypeName }}
      </span>
    </div>
    <div class="pet-info">
      <h3 class="pet-name">{{ pet.name }}</h3>
      <div class="pet-meta">
        <div class="pet-meta-item">
          <i class="fas fa-tag"></i>
          <span>{{ pet.breed || '未知品种' }}</span>
        </div>
        <div class="pet-meta-item">
          <i class="fas fa-{{ pet.gender === 'MALE' ? 'mars' : 'venus' }}"></i>
          <span>{{ pet.gender === 'MALE' ? '公' : '母' }}</span>
        </div>
        <div class="pet-meta-item">
          <i class="fas fa-birthday-cake"></i>
          <span>{{ pet.age }}岁</span>
        </div>
      </div>
      <p class="pet-description">{{ pet.description }}</p>
      <div class="pet-meta">
        <div class="pet-meta-item">
          <i class="fas fa-heartbeat"></i>
          <span>{{ pet.healthStatus || '健康' }}</span>
        </div>
        <div class="pet-meta-item">
          <i class="fas fa-syringe"></i>
          <span>{{ pet.vaccinated ? '已接种疫苗' : '未接种疫苗' }}</span>
        </div>
      </div>
      <router-link :to="`/adoption/petAdoptionDetails/${pet.id}`" class="btn btn-primary w-100">
        <i class="fas fa-info-circle"></i> 查看详情
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  pet: {
    type: Object,
    required: true
  }
})

const petTypeName = computed(() => {
  const typeMap = {
    DOG: '狗狗',
    CAT: '猫咪',
    RABBIT: '兔子',
    OTHER: '其他'
  }
  return typeMap[props.pet.type] || '宠物'
})

const petTypeClass = computed(() => {
  const classMap = {
    DOG: 'dog',
    CAT: 'cat',
    RABBIT: 'rabbit',
    OTHER: 'other'
  }
  return classMap[props.pet.type] || 'other'
})

const handleImageError = (e) => {
  e.target.src = '/images/pets/default.jpg'
}
</script>

<style scoped>
.pet-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  opacity: 0;
  animation: fadeInUp 0.6s ease-out forwards;
}

.pet-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

.pet-image-container {
  width: 100%;
  height: 250px;
  overflow: hidden;
  position: relative;
}

.pet-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.pet-card:hover .pet-image {
  transform: scale(1.1);
}

.pet-type-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 5px 15px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
  color: white;
  z-index: 2;
}

.pet-type-badge.dog { background: linear-gradient(135deg, #4e97fd 0%, #6a11cb 100%); }
.pet-type-badge.cat { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); }
.pet-type-badge.rabbit { background: linear-gradient(135deg, #fa709a 0%, #fee140 100%); }
.pet-type-badge.other { background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%); }

.pet-info {
  padding: 25px;
}

.pet-name {
  font-size: 1.5rem;
  font-weight: 700;
  margin-bottom: 10px;
  color: var(--dark-color, #343a40);
}

.pet-meta {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
  flex-wrap: wrap;
}

.pet-meta-item {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #666;
  font-size: 0.9rem;
}

.pet-description {
  color: #555;
  line-height: 1.6;
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.btn-primary {
  background: linear-gradient(135deg, #4e97fd 0%, #6a11cb 100%);
  border: none;
  padding: 12px;
  border-radius: 8px;
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  text-align: center;
  text-decoration: none;
  display: inline-block;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(78, 151, 253, 0.3);
}

.w-100 {
  width: 100%;
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
</style>