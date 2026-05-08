# QuizGo Frontend

这是给小组协作用的前端骨架项目，先把结构搭好，后面每个人在自己的目录里写。

## 运行

```bash
npm install
npm run dev
```

## 目录分工

```text
src/
├── views/
│   ├── question/   # B 负责
│   ├── exam/       # C 负责
│   └── user/       # D 负责
└── router/
    └── index.js    # 所有人加自己的路由，尽量只追加，不改别人已有部分
```

## 统一约定

- 接口统一走 `src/utils/request.js`
- token 统一存 `localStorage` 的 `token`
- 页面先能跑再说，不追求复杂封装
- 别随便改公共文件，尤其是 `main.js`、`App.vue`、`router/index.js`
