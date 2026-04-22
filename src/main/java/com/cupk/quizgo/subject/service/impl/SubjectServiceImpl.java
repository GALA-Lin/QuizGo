package com.cupk.quizgo.subject.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cupk.quizgo.entity.Subject;
import com.cupk.quizgo.subject.mapper.SubjectMapper;
import com.cupk.quizgo.subject.service.ISubjectService;
import org.springframework.stereotype.Service;

@Service
public class SubjectServiceImpl extends ServiceImpl<SubjectMapper, Subject> implements ISubjectService {
}