package com.blockchain.dto;

import java.time.LocalDateTime;
import java.util.Optional;

import com.blockchain.entity.NurseCertifications;
import com.blockchain.entity.NurseInfo;
import com.blockchain.entity.Subject;

// 用於接收創建或更新護士證書的請求數據
public class NurseCertificationRequestDTO {
    // 不需要 id，因為創建時由資料庫生成

    private Long nurseId; // 關聯的護士ID
    private String nurseName; // 護士姓名，方便前端顯示
    private Long subjectId; // 關聯的科目ID
    private String subjectName; // 科目名稱，方便前端顯示
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Float points;
    private String category;
    private String unit;

    // Constructors, Getters, Setters (可以使用 Lombok 的 @Getter, @Setter,
    // @NoArgsConstructor, @AllArgsConstructor)
    public NurseCertificationRequestDTO() {
    }

    public NurseCertificationRequestDTO(Long nurseId, String nurseName, Long subjectId,
            LocalDateTime startTime, LocalDateTime endTime, Float points, String category, String subject,
            String unit) {
        this.nurseId = nurseId;
        this.nurseName = nurseName;
        this.subjectId = subjectId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.points = points;
        this.category = category;
        this.subjectName = subject;
        this.unit = unit;
    }

    public static NurseCertificationRequestDTO fromEntity(NurseCertifications entity) {
        String nurseName = Optional.ofNullable(entity.getNurseInfo())
                .map(NurseInfo::getName)
                .orElse("Unknown Nurse"); // 這裡的字串應該是 "Unknown Nurse"

        // 從 Subject 實體中獲取 category, subjectName, 和 unit
        String category = Optional.ofNullable(entity.getSubject())
                .map(Subject::getCategory)
                .orElse("Unknown Subject Category"); // 這裡的字串應該是 "Unknown Subject Category"

        String subjectName = Optional.ofNullable(entity.getSubject())
                .map(Subject::getSubjectName)
                .orElse("Unknown Subject Name"); // 這裡的字串應該是 "Unknown Subject Name"

        String unit = Optional.ofNullable(entity.getSubject())
                .map(Subject::getUnit)
                .orElse("Unknown unit"); // 這裡的字串應該是 "Unknown unit"

        return new NurseCertificationRequestDTO(
                entity.getNurseInfo() != null ? entity.getNurseInfo().getId() : null, // 避免空指針
                nurseName,
                entity.getSubject() != null ? entity.getSubject().getId() : null, // 避免空指針
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getPoints(),
                category,
                subjectName, // 傳入 subjectName
                unit); // 傳入 unit
    }

    public Long getNurseId() {
        return nurseId;
    }

    public void setNurseId(Long nurseId) {
        this.nurseId = nurseId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Float getPoints() {
        return points;
    }

    public void setPoints(Float points) {
        this.points = points;
    }
}