# QuizGo 手机端接口对接说明（给 C / D）

> 这里写的是目前大家已经约定好的接口用途，方便前端直接对接。  
> 如果后端字段跟这里不一样，以最终 Controller 为准。

---

## 1. 登录

### POST `/api/user/login`

请求体：

```json
{
  "username": "张三",
  "password": "123456"
}
```

返回示例：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "token": "eyJhbGci...",
    "userId": 1,
    "username": "张三",
    "role": 0
  }
}
```

---

## 2. 注册

### POST `/api/user/register`

请求体：

```json
{
  "username": "张三",
  "password": "123456"
}
```

---

## 3. 科目列表

### GET `/api/subject/list`

请求头：

```text
Authorization: Bearer token
```

---

## 4. 题目列表

### GET `/api/question/list?subjectId=1&page=1&size=10`

用于刷题页获取题目。  
注意：通常这里不会返回答案和解析。

---

## 5. 提交答案

### POST `/api/practice/submit`

请求体：

```json
{
  "questionId": 1,
  "userAnswer": "D"
}
```

返回：

```json
{
  "code": 200,
  "msg": "success",
  "data": {
    "isCorrect": true,
    "correctAnswer": "D",
    "analysis": "题目解析",
    "addedToWrongBook": false
  }
}
```

---

## 6. 错题本

### GET `/api/wrong/list?page=1&size=10`

### DELETE `/api/wrong/{id}`

---

## 7. 用户信息

### GET `/api/user/info`

---

## 8. 请求封装统一位置

所有接口统一从这里发：

```text
utils/request.js
```

这样如果后面后端 IP 变了，只改一个地方就行。
