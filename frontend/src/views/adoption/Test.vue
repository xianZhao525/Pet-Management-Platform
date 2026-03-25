<template>
  <div>
    <h1>测试页面</h1>
    <p>参数 ID: {{ $route.params.id }}</p>
    <router-link to="/adoption/petAdoption">返回列表</router-link>
  </div>
</template>