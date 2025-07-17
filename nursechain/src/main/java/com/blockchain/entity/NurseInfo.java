package com.blockchain.entity;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "nurse_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NurseInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identyNo", nullable = false)
    private String identyNo;

    @Column(name = "user", nullable = false)
    private String user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "nurse_id", nullable = false)
    private int nurseId;

    @Column(name = "passwd", nullable = false)
    private String passwd;

    @OneToMany(mappedBy = "nurseInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NurseCertifications> nurseCertifications = new HashSet<>();

    @OneToOne(mappedBy = "nurseInfo", fetch = FetchType.LAZY) // mappedBy 指向 Hospital 實體中定義關聯的屬性名稱
    @JsonIgnore // 在非擁有者端使用 JsonIgnore 以避免 JSON 序列化無限循環
    private Hospital hospital;

}
