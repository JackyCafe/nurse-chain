package com.blockchain.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 請根據您的專案結構調整套件名稱

import com.blockchain.entity.Subject;
import com.blockchain.service.SubjectService;

@RestController // 標註為 RESTful 控制器
@RequestMapping("/api/subjects") // 定義基礎路徑
public class SubjectController {

    @Autowired // 自動注入 SubjectService 實例
    private SubjectService subjectService;

    /**
     * 獲取所有科目列表
     * GET /api/subjects
     * 
     * @return 包含所有科目物件的列表，狀態碼 200 OK
     */
    @GetMapping
    public ResponseEntity<List<Subject>> getAllSubjects() {
        List<Subject> subjects = subjectService.getAllSubjects();
        return ResponseEntity.ok(subjects); // 返回 200 OK 和科目列表
    }

    /**
     * 根據 ID 獲取特定科目
     * GET /api/subjects/{id}
     * 
     * @param id 科目 ID
     * @return 包含科目物件的 ResponseEntity，如果找到則為 200 OK，否則為 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubjectById(@PathVariable Long id) {
        return subjectService.getSubjectById(id)
                .map(ResponseEntity::ok) // 如果找到，返回 200 OK 和科目
                .orElse(ResponseEntity.notFound().build()); // 否則返回 404 Not Found
    }

    /**
     * 新增科目
     * POST /api/subjects
     * 
     * @param subject 要新增的科目物件 (從請求體中獲取)
     * @return 新增後的科目物件，狀態碼 201 Created
     */
    @PostMapping
    public ResponseEntity<Subject> createSubject(@RequestBody Subject subject) {
        Subject createdSubject = subjectService.saveSubject(subject); // 使用 service 的 save 方法
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubject); // 返回 201 Created
    }

    /**
     * 更新科目
     * PUT /api/subjects/{id}
     * 
     * @param id             要更新的科目 ID
     * @param subjectDetails 包含更新資訊的科目物件
     * @return 更新後的科目物件，狀態碼 200 OK；如果找不到，則為 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @RequestBody Subject subjectDetails) {
        return subjectService.getSubjectById(id) // 先嘗試查找現有科目
                .map(existingSubject -> {
                    // 更新現有科目的屬性
                    existingSubject.setCategory(subjectDetails.getCategory());
                    existingSubject.setSubjectName(subjectDetails.getSubjectName());
                    existingSubject.setUnit(subjectDetails.getUnit());
                    // 如果 Subject 還有其他字段，也需要在這裡更新

                    Subject updatedSubject = subjectService.saveSubject(existingSubject); // 儲存更新
                    return ResponseEntity.ok(updatedSubject); // 返回 200 OK
                })
                .orElse(ResponseEntity.notFound().build()); // 如果找不到，返回 404 Not Found
    }

    /**
     * 刪除科目
     * DELETE /api/subjects/{id}
     * 
     * @param id 要刪除的科目 ID
     * @return 狀態碼 204 No Content (表示成功刪除但沒有內容返回)；如果找不到，則為 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        if (subjectService.getSubjectById(id).isPresent()) { // 檢查科目是否存在
            subjectService.deleteSubject(id);
            return ResponseEntity.noContent().build(); // 返回 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 返回 404 Not Found
        }
    }

    // 如果您在 SubjectService 中添加了 getSubjectsByCategory 方法，可以像這樣創建一個端點：
    // @GetMapping("/category/{category}")
    // public ResponseEntity<List<Subject>> getSubjectsByCategory(@PathVariable
    // String category) {
    // List<Subject> subjects = subjectService.getSubjectsByCategory(category);
    // if (!subjects.isEmpty()) {
    // return ResponseEntity.ok(subjects);
    // } else {
    // return ResponseEntity.notFound().build();
    // }
    // }
}