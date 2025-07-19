package com.ian.nursechain.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "nurse_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "nurseCertifications", "hospital" })
// @EqualsAndHashCode(exclude = { "nurseCertifications", "hospital" }) //
// 關鍵修正：排除關聯集合
public class NurseInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "identy_no", nullable = false)
    private String identyNo;

    @Column(name = "user_name", nullable = false) // 建議名稱，避免與 SQL 關鍵字衝突
    private String user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "passwd", nullable = false)
    private String passwd;

    @OneToMany(mappedBy = "nurseInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Set<NurseCertifications> nurseCertifications = new HashSet<>();

    @OneToOne(mappedBy = "nurseInfo", fetch = FetchType.LAZY)
    private Hospital hospital; // 在 Hospital 實體中應有對應的 @JsonBackReference

    public NurseInfo(String identyNo, String user, String name, String passwd) {
        this.identyNo = identyNo;
        this.user = user;
        this.name = name;
        this.passwd = passwd;
    }
}