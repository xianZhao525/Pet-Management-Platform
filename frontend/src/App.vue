<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
// App.vue 只负责显示路由内容，不需要任何业务逻辑
</script>

<style>
/* 全局样式可以放在这里 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Poppins', 'Microsoft YaHei', sans-serif;
  background-color: #f8f9fa;
}

#app {
  min-height: 100vh;
}
</style>