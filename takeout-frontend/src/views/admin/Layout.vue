<template>
  <div class="admin-layout">
    <!-- 左侧导航 -->
    <aside class="sidebar">
      <nav>
        <router-link to="/admin/home">首页</router-link>
        <!-- 添加更多管理菜单 -->
      </nav>
    </aside>

    <!-- 主要内容区域 -->
    <main class="content">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
}

.sidebar {
  width: 200px;
  background: #f0f2f5;
  padding: 20px;
}

.content {
  flex: 1;
  padding: 20px;
  background: white;
}
</style>
