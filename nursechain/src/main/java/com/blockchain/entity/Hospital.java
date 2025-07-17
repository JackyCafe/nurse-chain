package com.blockchain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter; // 使用 Getter 替代 Data
import lombok.NoArgsConstructor;
import lombok.Setter; // 使用 Setter 替代 Data
import lombok.ToString;

@Entity
@Table(name = "hospital")
@Getter // 替代 @Data
@Setter // 替代 @Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "nurseInfo" }) // 修正：排除關聯實體以避免循環
// @EqualsAndHashCode(exclude = { "nurseInfo" }) // 關鍵修正：排除關聯實體
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dept", nullable = false)
    private String dept;

    @Column(name = "dept_id", nullable = false)
    private int deptId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id", referencedColumnName = "id", nullable = false)
    @JsonBackReference // 表示這是關係的「反向」部分，不應序列化
    private NurseInfo nurseInfo;
}