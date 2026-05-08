# QuizGo UniApp 前端骨架

这是给 QuizGo 移动端准备的 UniApp 项目骨架。

## 放在哪

放在项目根目录下，与 SpringBoot 后端平级：

```text
QuizGo/
├── src/                  # SpringBoot 后端
├── pom.xml
└── quizgo-app/           # 这个 UniApp 项目
```

## 这个项目是给谁的

- C：登录、首页、刷题
- D：错题本、个人中心

## 当前目录说明

```text
quizgo-app/
├── App.vue
├── main.js
├── manifest.json
├── pages.json
├── utils/
│   └── request.js
└── pages/
    ├── login/
    │   └── login.vue      # C 负责
    ├── index/
    │   └── index.vue      # C 负责
    ├── quiz/
    │   └── quiz.vue       # C 负责
    ├── wrong/
    │   └── wrong.vue      # D 负责
    └── profile/
        └── profile.vue    # D 负责
```

## 队友分工

### C 负责

1. `pages/login/login.vue`
   - 登录
   - 注册
   - token 存储

2. `pages/index/index.vue`
   - 科目列表
   - 跳转刷题页

3. `pages/quiz/quiz.vue`
   - 拉题目
   - 选答案
   - 提交答案
   - 显示结果和解析

### D 负责

1. `pages/wrong/wrong.vue`
   - 错题本列表
   - 删除错题

2. `pages/profile/profile.vue`
   - 用户信息
   - 统计数据
   - 退出登录

## 统一约定

### 1. 所有人请求都走 `utils/request.js`

不要自己再单独写请求封装。

### 2. 页面只改自己负责的目录

- C 只改：`pages/login` `pages/index` `pages/quiz`
- D 只改：`pages/wrong` `pages/profile`

### 3. 如果后端地址变了，只改一处

改这里：

```js
utils/request.js
const BASE_URL = 'http://localhost:8080'
```

## 对接接口

### C 用到
- `POST /api/user/login`
- `POST /api/user/register`
- `GET /api/subject/list`
- `GET /api/question/list`
- `POST /api/practice/submit`

### D 用到
- `GET /api/wrong/list`
- `DELETE /api/wrong/{id}`
- `GET /api/user/info`

## 你可以直接发给队友的话

```text
UniApp 骨架我已经搭好了，大家只写自己页面：
C 写 login/index/quiz
D 写 wrong/profile
不要自己另起项目，也不要乱改 request.js
后端地址统一在 utils/request.js 里改
```
