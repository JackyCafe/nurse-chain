package com.ian.nursechain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ian.nursechain.entity.Hospital;

@Repository
public interface HospitalRepository extends JpaRepository<Hospital, Long> {
    // Spring Data JPA 會自動為您生成基本的 CRUD 方法。
    // 您可以在這裡定義自定義查詢方法，例如：
    // List<NurseCertifications> findByNurseInfo_Id(Long nurseId);
    // List<NurseCertifications> findBySubject_Id(Long subjectId);
}
