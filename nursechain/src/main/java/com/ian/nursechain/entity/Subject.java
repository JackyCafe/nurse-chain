package com.ian.nursechain.entity;

import java.util.Date;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "subject")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = { "nurseCertifications" })
// @EqualsAndHashCode(exclude = { "nurseCertifications" }) // 關鍵修正：排除關聯集合
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "subject_code", nullable = false)
    private int subjectCode;

    @Column(name = "teacher", nullable = false)
    private String teacher;

    @Column(name = "start_time", nullable = false)
    private Date start_time;

    @Column(name = "end_time", nullable = false)
    private Date end_time;

    @Column(name = "points", nullable = false)
    private float points;

    @Column(name = "units", nullable = false)
    private String unit;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference("subject-certifications")
    private Set<NurseCertifications> nurseCertifications = new HashSet<>();
}