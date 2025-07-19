package com.ian.nursechain.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ian.nursechain.entity.Subject;
import com.ian.nursechain.repository.SubjectRepository;

@Service
public class SubjectService {
    @Autowired
    private SubjectRepository repository;

    public List<Subject> getAllSubjects() {
        return repository.findAll();
    }

    public Optional<Subject> getSubjectById(Long id) {
        return repository.findById(id);

    }

    /**
     * 新增或更新科目
     * 
     * @param subject 要儲存的科目物件
     * @return 儲存後的科目物件
     */
    public Subject saveSubject(Subject subject) {
        return repository.save(subject);
    }

    /**
     * 根據 ID 刪除科目
     * 
     * @param id 要刪除的科目 ID
     */
    public void deleteSubject(Long id) {
        repository.deleteById(id);
    }

    // 如果您在 SubjectRepository 中定義了此方法，請取消註釋
    public List<Subject> getSubjectsByCategory(String category) {
        return repository.findByCategory(category);
    }

}
