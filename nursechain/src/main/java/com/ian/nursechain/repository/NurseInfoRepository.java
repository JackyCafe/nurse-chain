package com.ian.nursechain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ian.nursechain.entity.NurseInfo;

public interface NurseInfoRepository extends JpaRepository<NurseInfo, Long> {
    List<NurseInfo> findByIdentyNo(String user);
    List<NurseInfo> findAll();

}
