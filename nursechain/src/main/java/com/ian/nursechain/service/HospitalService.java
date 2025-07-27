package com.ian.nursechain.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ian.nursechain.dto.HospitalResponseDto;
import com.ian.nursechain.dto.NurseInfoDto;
import com.ian.nursechain.entity.Hospital;
import com.ian.nursechain.entity.NurseInfo;
import com.ian.nursechain.repository.HospitalRepository;
import com.ian.nursechain.repository.NurseInfoRepository;

import jakarta.transaction.Transactional;;

@Service
public class HospitalService {
    private final HospitalRepository hospitalRepository;
    private final NurseInfoRepository nurseInfoRepository;

    @Autowired
    public HospitalService(HospitalRepository hospitalRepository, NurseInfoRepository nurseInfoRepository) {
        this.hospitalRepository = hospitalRepository;
        this.nurseInfoRepository = nurseInfoRepository;

    }

    // 取得所有醫院
    @Transactional
    public List<HospitalResponseDto> getAllHospitalsDTO() {
        List<Hospital> hospitals = hospitalRepository.findAll();
        return hospitals.stream().map(hospital -> {
            HospitalResponseDto dto = new HospitalResponseDto();
            dto.setId(hospital.getId());
            dto.setDept(hospital.getDept());
            dto.setDeptId(hospital.getDeptId());
            dto.setName(hospital.getName());
            dto.setVerified(hospital.isVerified());
            if (hospital.getNurseInfo() != null) {
                NurseInfo nurse = hospital.getNurseInfo(); // 會觸發加載
                dto.setNurseInfo(
                        new NurseInfoDto(nurse.getId(), nurse.getIdentyNo(), nurse.getUser(), nurse.getName()));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    // 根據ID取得醫院
    @Transactional
    public Optional<Hospital> getHospitalById(Long id) {
        Optional<Hospital> hospitalOptional = hospitalRepository.findById(id);
        hospitalOptional.ifPresent(hospital -> {
            // 強制加載 nurseInfo，確保在事務結束前被初始化
            if (hospital.getNurseInfo() != null) {
                hospital.getNurseInfo().getId(); // 任何對 nurseInfo 的訪問都會觸發加載
            }
        });
        return hospitalOptional;
    }

    @Transactional
    public Hospital createHospital(Hospital hospital, Long nurseInfoId) {
        if (nurseInfoId == null) {
            // 如果 nurseInfoId 為 null 且關聯是必需的，則拋出錯誤
            throw new IllegalArgumentException("建立醫院必須提供護理師資訊ID。");
        }

        Optional<NurseInfo> nurseInfoOptional = nurseInfoRepository.findById(nurseInfoId);
        if (nurseInfoOptional.isPresent()) {
            NurseInfo nurseInfo = nurseInfoOptional.get();

            // 檢查護理師是否已經關聯到其他醫院
            // 注意：如果這是一個新的 Hospital，hospital.getId() 可能為 null。
            // 更好的檢查是直接看 nurseInfo.getHospital() 是否為 null
            // 或者如果 hospital 是既有的物件，則檢查是否已關聯到自身。
            if (nurseInfo.getHospital() != null) {
                // 如果護理師已經關聯到其他醫院，則拋出錯誤
                // （除非您想允許更新關聯，那需要更複雜的邏輯判斷是否是更新到當前 Hospital）
                throw new IllegalArgumentException("此護理師已關聯到其他醫院。");
            }

            hospital.setNurseInfo(nurseInfo);
            nurseInfo.setHospital(hospital); // 雙向關聯設置
        } else {
            throw new IllegalArgumentException("找不到ID為 " + nurseInfoId + " 的護理師資訊。");
        }

        return hospitalRepository.save(hospital);
    }

    // 更新醫院資訊
    @Transactional
    public Hospital updateHospital(Long id, Hospital hospitalDetails, Long newNurseInfoId) {
        return hospitalRepository.findById(id)
                .map(hospital -> {
                    hospital.setDept(hospitalDetails.getDept());
                    hospital.setDeptId(hospitalDetails.getDeptId());
                    hospital.setName(hospitalDetails.getName());
                    hospital.setVerified(hospitalDetails.isVerified());

                    // 處理護理師關聯更新
                    if (newNurseInfoId != null) {
                        Optional<NurseInfo> newNurseInfoOptional = nurseInfoRepository.findById(newNurseInfoId);
                        if (newNurseInfoOptional.isPresent()) {
                            NurseInfo newNurseInfo = newNurseInfoOptional.get();
                            // 檢查新的護理師是否已經關聯到其他醫院
                            if (newNurseInfo.getHospital() != null && !newNurseInfo.getHospital().getId().equals(id)) {
                                throw new IllegalArgumentException("此護理師已關聯到其他醫院。");
                            }

                            // 解除舊的護理師關聯（如果存在）
                            if (hospital.getNurseInfo() != null
                                    && !hospital.getNurseInfo().getId().equals(newNurseInfoId)) {
                                hospital.getNurseInfo().setHospital(null);
                            }

                            hospital.setNurseInfo(newNurseInfo);
                            newNurseInfo.setHospital(hospital); // 雙向關聯設置
                        } else {
                            throw new IllegalArgumentException("找不到ID為 " + newNurseInfoId + " 的護理師資訊。");
                        }
                    } else {
                        // 如果 newNurseInfoId 為 null，表示要解除關聯
                        if (hospital.getNurseInfo() != null) {
                            hospital.getNurseInfo().setHospital(null);
                            hospital.setNurseInfo(null);
                        }
                    }
                    return hospitalRepository.save(hospital);
                })
                .orElseThrow(() -> new RuntimeException("找不到ID為 " + id + " 的醫院。"));
    }

    // 刪除醫院
    @Transactional
    public void deleteHospital(Long id) {
        Hospital hospital = hospitalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("找不到ID為 " + id + " 的醫院。"));

        // 解除護理師關聯
        if (hospital.getNurseInfo() != null) {
            hospital.getNurseInfo().setHospital(null);
        }
        hospitalRepository.delete(hospital);
    }

}
