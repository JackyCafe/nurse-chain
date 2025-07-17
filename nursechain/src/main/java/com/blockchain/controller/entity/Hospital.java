package com.blockchain.controller.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "hospital")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "dept", nullable = false)
    String dept;

    @Column(name = "dept_id", nullable = false)
    int deptId;

    @Column(name = "name", nullable = false)
    String name;

    @Column(name = "vertified", nullable = false)
    boolean vertified;

    @OneToOne
    @JoinColumn(name = "nurse_id", referencedColumnName = "id", nullable = false) // hospital 表中的 nurse_id 欄位參考
                                                                                  // nurse_info 表的 id
    private NurseInfo nurseInfo; // 這裡的屬性名稱將用於 NurseInfo 中的 mappedBy

}
