CREATE DATABASE IF NOT EXISTS quizgo DEFAULT CHARACTER SET utf8mb4;
USE quizgo;

-- 用户表
CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
                      password VARCHAR(100) NOT NULL COMMENT '密码(md5)',
                      avatar VARCHAR(200) COMMENT '头像url',
                      role TINYINT DEFAULT 0 COMMENT '0普通用户 1管理员',
                      create_time DATETIME DEFAULT NOW()
);

-- 科目表
CREATE TABLE subject (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         name VARCHAR(50) NOT NULL COMMENT '科目名',
                         description VARCHAR(200) COMMENT '描述',
                         icon VARCHAR(200) COMMENT '图标url'
);

-- 题目表
CREATE TABLE question (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          subject_id BIGINT NOT NULL COMMENT '科目id',
                          content TEXT NOT NULL COMMENT '题目内容',
                          type TINYINT DEFAULT 0 COMMENT '0单选 1多选 2判断',
                          options_json VARCHAR(1000) COMMENT '选项JSON数组',
                          answer VARCHAR(10) NOT NULL COMMENT '正确答案',
                          analysis TEXT COMMENT '解析',
                          difficulty TINYINT DEFAULT 1 COMMENT '1简单 2中等 3困难'
);

-- 答题记录表
CREATE TABLE answer_record (
                               id BIGINT PRIMARY KEY AUTO_INCREMENT,
                               user_id BIGINT NOT NULL,
                               question_id BIGINT NOT NULL,
                               user_answer VARCHAR(10) COMMENT '用户答案',
                               is_correct TINYINT DEFAULT 0 COMMENT '是否正确',
                               create_time DATETIME DEFAULT NOW()
);

-- 错题本表
CREATE TABLE wrong_book (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            user_id BIGINT NOT NULL,
                            question_id BIGINT NOT NULL,
                            create_time DATETIME DEFAULT NOW(),
                            UNIQUE KEY uk_user_question (user_id, question_id)
);

-- 测试数据
INSERT INTO subject VALUES(1,'语文','小学语文',NULL),(2,'数学','小学数学',NULL),(3,'英语','小学英语',NULL);
INSERT INTO user VALUES(1,'admin','e10adc3949ba59abbe56e057f20f883e',NULL,1,NOW());
-- 管理员密码：123456

INSERT INTO question VALUES
                         (1,1,'下列词语中，字形全部正确的一组是？',0,'["A.欢渡春节","B.融会贯通","C.目不转晴","D.再接再厉"]','D','再接再厉是固定搭配，其他均有错字',1),
                         (2,2,'计算：125 × 8 = ？',0,'["A.900","B.1000","C.1100","D.1200"]','B','125×8=1000',1),
                         (3,3,'Which is correct?',0,'["A.I am a student","B.I is a student","C.I are a student","D.I be a student"]','A','主语I对应am',1);