<template>
  <div class="layout">
    <aside class="sidebar">
      <div class="sidebar-brand">
        <div class="brand-icon">
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none">
            <path d="M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5"
                  stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
        </div>
        <span class="brand-name">QuizGo</span>
      </div>

      <nav class="sidebar-nav">
        <RouterLink to="/question" class="nav-item">
          <span class="nav-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M9 12h6M9 16h6M9 8h6M5 4h14a1 1 0 0 1 1 1v14a1 1 0 0 1-1 1H5a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1z"
                    stroke="currentColor" stroke-width="1.8" stroke-linecap="round"/>
            </svg>
          </span>
          <span>题目管理</span>
        </RouterLink>

        <RouterLink to="/exam" class="nav-item">
          <span class="nav-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M9 5H7a2 2 0 0 0-2 2v12a2 2 0 0 0 2 2h10a2 2 0 0 0 2-2V7a2 2 0 0 0-2-2h-2M9 5a2 2 0 0 0 2 2h2a2 2 0 0 0 2-2M9 5a2 2 0 0 1 2-2h2a2 2 0 0 1 2 2m-6 9 2 2 4-4"
                    stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </span>
          <span>考试 / 刷题</span>
        </RouterLink>

        <RouterLink to="/user" class="nav-item">
          <span class="nav-icon">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z"
                    stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </span>
          <span>用户管理</span>
        </RouterLink>
      </nav>

      <div class="sidebar-footer">
        <div class="version-tag">v1.0.0</div>
      </div>
    </aside>

    <div class="content-wrap">
      <header class="topbar">
        <div class="topbar-left">
          <div class="breadcrumb">
            <span class="breadcrumb-root">QuizGo</span>
            <span class="breadcrumb-sep">/</span>
            <span class="breadcrumb-current">管理后台</span>
          </div>
        </div>
        <div class="topbar-right">
          <div class="avatar">
            <svg width="15" height="15" viewBox="0 0 24 24" fill="none">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2M12 11a4 4 0 1 0 0-8 4 4 0 0 0 0 8z"
                    stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
      </header>

      <main class="main">
        <router-view />
      </main>
    </div>
  </div>
</template>

<style scoped>
/* ── 变量（与子页面保持一致） ── */
.layout {
  --brand: #4f6ef7;
  --brand-dark: #3a56e0;
  --brand-light: #eef1fe;
  --bg: #f3f4f8;
  --card: #ffffff;
  --border: #e8eaf0;
  --text-primary: #1a1d2e;
  --text-secondary: #6b7280;
  --text-muted: #9ca3af;
  --sidebar-w: 220px;

  display: flex;
  min-height: 100vh;
  background: var(--bg);
  font-family: 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
}

/* ── 侧边栏 ── */
.sidebar {
  width: var(--sidebar-w);
  flex-shrink: 0;
  background: #1a1d2e;
  display: flex;
  flex-direction: column;
  position: fixed;
  top: 0;
  left: 0;
  height: 100vh;
  z-index: 100;
}

/* Logo 区 */
.sidebar-brand {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 24px 20px 22px;
  border-bottom: 1px solid rgba(255,255,255,.07);
}

.brand-icon {
  width: 34px;
  height: 34px;
  border-radius: 10px;
  background: linear-gradient(135deg, var(--brand) 0%, #7c91fa 100%);
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  box-shadow: 0 4px 12px rgba(79,110,247,.4);
}

.brand-name {
  font-size: 17px;
  font-weight: 800;
  color: #fff;
  letter-spacing: -0.01em;
}

/* 导航链接 */
.sidebar-nav {
  flex: 1;
  padding: 16px 12px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 12px;
  border-radius: 10px;
  color: rgba(255,255,255,.55);
  text-decoration: none;
  font-size: 14px;
  font-weight: 500;
  transition: all .18s;
}

.nav-item:hover {
  background: rgba(255,255,255,.07);
  color: rgba(255,255,255,.85);
}

.nav-item.router-link-active {
  background: linear-gradient(135deg, rgba(79,110,247,.25) 0%, rgba(124,145,250,.15) 100%);
  color: #fff;
  border: 1px solid rgba(79,110,247,.3);
}

.nav-icon {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 7px;
  background: rgba(255,255,255,.06);
  flex-shrink: 0;
  transition: background .18s;
}

.nav-item.router-link-active .nav-icon {
  background: rgba(79,110,247,.35);
}

/* 版本号 */
.sidebar-footer {
  padding: 16px 20px;
  border-top: 1px solid rgba(255,255,255,.07);
}

.version-tag {
  font-size: 11px;
  color: rgba(255,255,255,.2);
  letter-spacing: .04em;
}

/* ── 右侧内容区 ── */
.content-wrap {
  flex: 1;
  margin-left: var(--sidebar-w);
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* 顶栏 */
.topbar {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 36px;
  background: var(--card);
  border-bottom: 1px solid var(--border);
  position: sticky;
  top: 0;
  z-index: 50;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
}

.breadcrumb-root {
  color: var(--text-muted);
}

.breadcrumb-sep {
  color: var(--border);
}

.breadcrumb-current {
  color: var(--text-secondary);
  font-weight: 500;
}

.avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: var(--brand-light);
  color: var(--brand);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border: 1.5px solid #d5dcfc;
  transition: all .15s;
}

.avatar:hover {
  border-color: var(--brand);
  background: #dce4fd;
}

/* 主内容 */
.main {
  flex: 1;
  overflow: auto;
}

/* ── 响应式 ── */
@media (max-width: 768px) {
  .sidebar {
    display: none;
  }
  .content-wrap {
    margin-left: 0;
  }
  .topbar {
    padding: 0 16px;
  }
}
</style>