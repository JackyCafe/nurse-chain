package com.ian.nursechain.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ian.nursechain.dto.HospitalResponseDto;
import com.ian.nursechain.entity.Hospital;
import com.ian.nursechain.service.HospitalService;

@RestController
@RequestMapping("/api/hospitals")
public class HospitalController {
    HospitalService hospitalService;

    @Autowired
    public HospitalController(HospitalService service) {
        this.hospitalService = service;
    }

    // 取得所有醫院
    // GET /api/hospitals
    @GetMapping
    public List<HospitalResponseDto> getAllHospitals() {
        return hospitalService.getAllHospitalsDTO();
    }

    // 根據ID取得醫院
    // GET /api/hospitals/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Hospital> getHospitalById(@PathVariable Long id) {
        Optional<Hospital> hospital = hospitalService.getHospitalById(id);
        return hospital.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Hospital> createHospital(@RequestBody Map<String, Object> payload) {
        // 從 payload 中提取醫院資訊
        String dept = (String) payload.get("dept");
        Integer deptId = (Integer) payload.get("deptId");
        String name = (String) payload.get("name");
        Boolean verified = (Boolean) payload.get("verified");

        Map<String, Object> nurseInfoPayload = (Map<String, Object>) payload.get("nurseInfo");

        Long nurseInfoId = null;
        if (nurseInfoPayload != null && nurseInfoPayload.containsKey("id")) {
            // 確保 id 是 Long 類型，或者根據實際情況轉換
            // 如果 JSON 中的 id 是整數，payload.get("id") 會是 Integer
            // 建議直接使用 Long.valueOf() 進行轉換，更安全
            nurseInfoId = Long.valueOf(nurseInfoPayload.get("id").toString());
        }

        if (dept == null || deptId == null || name == null || verified == null) {
            return ResponseEntity.badRequest().build();
        }
        // System.out.println(dept);

        Hospital hospital = new Hospital();
        hospital.setDept(dept);
        hospital.setDeptId(deptId);
        hospital.setName(name);
        hospital.setVerified(verified);
        // nurseInfo 會在 service 層設定
        System.out.println(payload);

        try {
            Hospital createdHospital = hospitalService.createHospital(hospital,
                    nurseInfoId);
            return new ResponseEntity<>(createdHospital, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null); // 或者返回更詳細的錯誤訊息
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Hospital> updateHospital(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
        // 從 payload 中提取醫院資訊
        String dept = (String) payload.get("dept");
        Integer deptId = (Integer) payload.get("deptId");
        String name = (String) payload.get("name");
        Boolean verified = (Boolean) payload.get("verified");
        Long newNurseInfoId = payload.get("nurseInfoId") != null ? ((Number) payload.get("nurseInfoId")).longValue()
                : null;

        if (dept == null || deptId == null || name == null || verified == null) {
            return ResponseEntity.badRequest().build();
        }

        Hospital hospitalDetails = new Hospital();
        hospitalDetails.setDept(dept);
        hospitalDetails.setDeptId(deptId);
        hospitalDetails.setName(name);
        hospitalDetails.setVerified(verified);

        try {
            Hospital updatedHospital = hospitalService.updateHospital(id, hospitalDetails, newNurseInfoId);
            return ResponseEntity.ok(updatedHospital);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 刪除醫院
    // DELETE /api/hospitals/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHospital(@PathVariable Long id) {
        try {
            hospitalService.deleteHospital(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

}
