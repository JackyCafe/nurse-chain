package com.blockchain.dto;

import java.time.LocalDateTime;

// 用於接收創建或更新護士證書的請求數據
public class NurseCertificationRequestDTO {
    // 不需要 id，因為創建時由資料庫生成
    private Long nurseId; // 關聯的護士ID
    private Long subjectId; // 關聯的科目ID
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Float points;

    // Constructors, Getters, Setters (可以使用 Lombok 的 @Getter, @Setter,
    // @NoArgsConstructor, @AllArgsConstructor)
    public NurseCertificationRequestDTO() {
    }

    public NurseCertificationRequestDTO(Long nurseId, Long subjectId, LocalDateTime startTime, LocalDateTime endTime,
            Float points) {
        this.nurseId = nurseId;
        this.subjectId = subjectId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.points = points;
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