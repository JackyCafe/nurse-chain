package com.ian.nursechain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NurseInfoDto {
    private long id;
    private String identyNo;
    private String userName;
    private String name;

}
