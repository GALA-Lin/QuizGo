<template>
  <div class="layout">
    <header class="header">
      <h1>QuizGo Frontend</h1>
      <nav class="nav">
        <RouterLink to="/question">题目</RouterLink>
        <RouterLink to="/exam">考试/刷题</RouterLink>
        <RouterLink to="/user">用户</RouterLink>
      </nav>
    </header>

    <main class="main">
      <router-view />
    </main>
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  background: #f5f7fa;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 24px;
  background: #409eff;
  color: #fff;
}

.header h1 {
  margin: 0;
  font-size: 20px;
}

.nav {
  display: flex;
  gap: 16px;
}

.nav a {
  color: #fff;
  text-decoration: none;
}

.nav a.router-link-active {
  font-weight: 700;
  text-decoration: underline;
}

.main {
  padding: 24px;
}
</style>
