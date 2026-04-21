# QuizGo 队友协作说明（UniApp）

## 1. 这个项目是什么

这是 **QuizGo 手机端 UniApp 项目**。  
大家都在这个目录里写，但请按分工改自己的页面。

---

## 2. 分工

### C 负责
- `pages/login/login.vue`：登录 / 注册
- `pages/index/index.vue`：首页 / 科目列表
- `pages/quiz/quiz.vue`：刷题页
- `pages/result/result.vue`：结果页（可选）

### D 负责
- `pages/wrong/wrong.vue`：错题本列表
- `pages/wrong/wrong_detail.vue`：错题详情 / 重做
- `pages/profile/profile.vue`：个人中心

### B 协助
- 帮大家统一目录
- 帮大家统一接口请求方式
- 帮大家处理 git 合并
- 如果需要，也可以补一些页面样式或接口对接

---

## 3. 不要乱改的文件

下面这些文件改之前先说一声：

- `pages.json`
- `utils/request.js`
- `manifest.json`

因为这些文件是全局共用的，乱改容易把别人的页面搞挂。

---

## 4. 统一请求方式

所有接口都通过：

```js
import { request } from '@/utils/request.js'
```

不要每个人自己写一套 `uni.request`。

---

## 5. 统一 token 存储方式

登录成功后统一这样存：

```js
uni.setStorageSync('token', token)
uni.setStorageSync('userInfo', userInfo)
```

退出登录统一这样清：

```js
uni.removeStorageSync('token')
uni.removeStorageSync('userInfo')
```

---

## 6. 统一接口返回格式

后端统一格式：

```json
{
  "code": 200,
  "msg": "success",
  "data": {}
}
```

前端判断逻辑按这个来，不要自己发明格式。

---

## 7. git 建议

### 每个人尽量只改自己页面
这样最不容易冲突。

### 如果要改公共文件
先在群里说一声，比如：

- 要加页面路由
- 要改请求封装
- 要改 tabBar

---

## 8. 最终目标

不用做得特别漂亮，先保证：

- 页面能打开
- 能请求后端
- 能完成基本演示流程

课程项目优先级：

**能跑 > 好看 > 规范**
