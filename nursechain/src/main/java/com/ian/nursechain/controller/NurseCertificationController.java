package com.ian.nursechain.controller;

import java.util.List;

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

import com.ian.nursechain.dto.NurseCertificationRequestDTO;
import com.ian.nursechain.dto.NurseCertificationResponseDTO;
import com.ian.nursechain.service.NurseCertificationService;

@RestController
@RequestMapping("/api/nursecertifications") // 定義 API 路徑
public class NurseCertificationController {

    private final NurseCertificationService nurseCertificationService;

    public NurseCertificationController(NurseCertificationService nurseCertificationService) {
        this.nurseCertificationService = nurseCertificationService;
    }

    // 創建新的護士證書
    @PostMapping
    public ResponseEntity<NurseCertificationResponseDTO> createNurseCertification(
            @RequestBody NurseCertificationRequestDTO requestDTO) {
        NurseCertificationResponseDTO createdCertification = nurseCertificationService
                .createNurseCertification(requestDTO);
        return new ResponseEntity<>(createdCertification, HttpStatus.CREATED); // 返回 201 Created
    }

    // 獲取所有護士證書
    @GetMapping
    public ResponseEntity<List<NurseCertificationResponseDTO>> getAllNurseCertifications() {

        List<NurseCertificationResponseDTO> certifications = nurseCertificationService.getAllNurseCertifications();
        return ResponseEntity.ok(certifications); // 返回 200 OK
    }

    // 根據 ID 獲取單個護士證書
    @GetMapping("/{id}")
    public ResponseEntity<NurseCertificationResponseDTO> getNurseCertificationById(@PathVariable Long id) {
        // NurseCertificationResponseDTO certification = nurseCertificationService.getNurseCertificationById(id);
        NurseCertificationResponseDTO certification = nurseCertificationService.getNurseCertificationById(id);
        return ResponseEntity.ok(certification); // 返回 200 OK
    }



    // 更新護士證書
    @PutMapping("/{id}")
    public ResponseEntity<NurseCertificationResponseDTO> updateNurseCertification(@PathVariable Long id,
            @RequestBody NurseCertificationRequestDTO requestDTO) {
        NurseCertificationResponseDTO updatedCertification = nurseCertificationService.updateNurseCertification(id,
                requestDTO);
        return ResponseEntity.ok(updatedCertification); // 返回 200 OK
    }

    // 刪除護士證書
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNurseCertification(@PathVariable Long id) {
        nurseCertificationService.deleteNurseCertification(id);
        return ResponseEntity.noContent().build(); // 返回 204 No Content
    }

    // 根據護士ID獲取所有證書
    @GetMapping("/nurse/{nurseId}")
    public ResponseEntity<List<NurseCertificationResponseDTO>> getNurseCertificationsByNurseId(@PathVariable Long nurseId) {
        List<NurseCertificationResponseDTO> certifications = nurseCertificationService.getNurseCertificationsByNurseId(nurseId);
        return ResponseEntity.ok(certifications);
    }
}