
package com.ian.nursechain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HospitalResponseDto {
    private long id;
    private String dept;
    private int deptId;
    private String name;
    private boolean verified;
    private NurseInfoDto nurseInfo; // 包含

}