package com.ian.nursechain.repository; // 建議放在 repository 包下

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.ian.nursechain.entity.NurseCertifications;

/**
 * NurseCertificationRepository 是一個 Spring Data JPA Repository 介面。
 * 它繼承了 JpaRepository，提供了對 NurseCertifications 實體
 * 進行基本 CRUD 操作（如保存、查找、更新、刪除）的功能。
 *
 * JpaRepository<T, ID> 中的 T 是實體類型，ID 是實體主鍵的類型。
 */
@Repository // 表明這是一個 Spring Repository 組件
public interface NurseCertificationRepository extends JpaRepository<NurseCertifications, Long> {
    // Spring Data JPA 會自動為您生成基本的 CRUD 方法。
    // 您可以在這裡定義自定義查詢方法，例如：
    // List<NurseCertifications> findByNurseInfo_Id(Long nurseId);
    // List<NurseCertifications> findBySubject_Id(Long subjectId);
    @Query("SELECT nc FROM NurseCertifications nc ORDER BY nc.nurseInfo.id")
    List<NurseCertifications> findAllOrderByNurseId();

    /**
     * 根據護士ID查找所有證照
     * @param nurseId 護士的ID
     * @return 該護士的所有證照清單
     */
    List<NurseCertifications> findByNurseInfoId(Long nurseId);


}