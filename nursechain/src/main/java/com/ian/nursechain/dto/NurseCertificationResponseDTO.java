package com.ian.nursechain.dto;

import java.time.LocalDateTime;
import java.util.Optional;

import com.ian.nursechain.entity.NurseCertifications;
import com.ian.nursechain.entity.NurseInfo;
import com.ian.nursechain.entity.Subject;

// 用於返回護士證書資訊的響應數據
public class NurseCertificationResponseDTO {
    private Long id;
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
    public NurseCertificationResponseDTO() {
    }

    /*
     * 將實體轉換為 DTO 的輔助方法
     * private NurseCertificationResponseDTO convertToDto(NurseCertifications
     * entity) {
     * String nurseName = Optional.ofNullable(entity.getNurseInfo())
     * .map(NurseInfo::getName)
     * .orElse("Unknown Nurse");
     * String category = Optional.ofNullable(entity.getSubject())
     * .map(Subject::getCategory) // <-- 這裡是關鍵
     * .orElse("Unknown Subject");
     * String subjectName = Optional.ofNullable(entity.getSubject())
     * .map(Subject::getSubjectName) // <-- 這裡是關鍵
     * .orElse("Unknown Subject");
     * String unit = Optional.ofNullable(entity.getSubject())
     * .map(Subject::getUnit)
     * .orElse("Unknown unit");
     * 
     * return new NurseCertificationResponseDTO(
     * entity.getId(),
     * entity.getNurseInfo().getId(),
     * nurseName,
     * entity.getSubject().getId(),
     * entity.getStartTime(),
     * entity.getEndTime(),
     * entity.getPoints(),
     * category,
     * subjectName,
     * unit);
     * }
     */

    public static NurseCertificationResponseDTO fromEntity(NurseCertifications entity) {
        String nurseName = Optional.ofNullable(entity.getNurseInfo())
                .map(NurseInfo::getName)
                .orElse("Unknown Nurse"); // 這裡的字串應該是 "Unknown Nurse"

        // 從 Subject 實體中獲取 category, subjectName, 和 unit
        String category = Optional.ofNullable(entity.getSubject())
                .map(Subject::getCategory)
                .orElse("Unknown Subject"); // 這裡的字串應該是 "Unknown Subject"

        String subjectName = Optional.ofNullable(entity.getSubject())
                .map(Subject::getSubjectName)
                .orElse("Unknown Subject"); // 這裡的字串應該是 "Unknown Subject"

        String unit = Optional.ofNullable(entity.getSubject())
                .map(Subject::getUnit)
                .orElse("Unknown unit"); // 這裡的字串應該是 "Unknown unit"

        return new NurseCertificationResponseDTO(
                entity.getId(),
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

    public NurseCertificationResponseDTO(Long id, Long nurseId, String nurseName, Long subjectId,
            LocalDateTime startTime, LocalDateTime endTime, Float points, String category, String subject,
            String unit) {
        this.id = id;
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNurseId() {
        return nurseId;
    }

    public void setNurseId(Long nurseId) {
        this.nurseId = nurseId;
    }

    public String getNurseName() {
        return nurseName;
    }

    public void setNurseName(String nurseName) {
        this.nurseName = nurseName;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
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

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return this.category;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnit() {
        return this.unit;
    }
}