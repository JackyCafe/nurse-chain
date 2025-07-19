package com.blockchain.repository; // 建議放在 repository 包下

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.blockchain.entity.Subject;

/**
 * SubjectRepository 是一個 Spring Data JPA Repository 介面。
 * 它繼承了 JpaRepository，提供了對 Subject 實體
 * 進行基本 CRUD 操作（如保存、查找、更新、刪除）的功能。
 */
@Repository // 表明這是一個 Spring Repository 組件
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    // Spring Data JPA 會自動為您生成基本的 CRUD 方法。
    // 您可以在這裡定義自定義查詢方法。
    List<Subject> findByCategory(String category);
}