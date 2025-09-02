package com.ian.nursechain.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ian.nursechain.dto.BlockchainCertifyRequestDTO;
import com.ian.nursechain.dto.BlockchainCertifyResponseDTO;
import com.ian.nursechain.service.BlockchainService;

@RestController
@RequestMapping("/api/blockchain")
public class BlockchainController {

    private final BlockchainService blockchainService;

    public BlockchainController(BlockchainService blockchainService) {
        this.blockchainService = blockchainService;
    }

    /**
     * 處理護士證書上鏈的請求
     * 接收 JSON 格式的請求體，其中包含 certificationId
     *
     * @param request 包含要上鏈的證書ID的請求 DTO
     * @return 包含上鏈結果的響應 DTO
     */
    @PostMapping("/certify")
    public ResponseEntity<BlockchainCertifyResponseDTO> certifyNurseCertificationOnBlockchain(
            @RequestBody BlockchainCertifyRequestDTO request) {
        if (request.getCertificationId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Certification ID cannot be null for blockchain operation.");
        }

        try {
            // 調用服務層來處理實際的上鏈邏輯
            BlockchainCertifyResponseDTO response = blockchainService
                    .certifyCertification(request.getCertificationId());
            System.out.println(response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 處理上鏈過程中可能發生的任何異常
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error during blockchain certification: " + e.getMessage(), e);
        }
    }


   /**
     * 以護士ID為 key 將所有護士的證書分組上鏈並統計時間和總積分
     * GET /api/blockchain/certify-by-nurse
     * @return 每個護士的上鏈總時間、證書數量和總積分

    @GetMapping("/certify-by-nurse")
    public ResponseEntity<Map<Long, Map<String, Object>>> certifyByNurseId() {
        Map<Long, Map<String, Object>> result = blockchainService.certifyByNurseId();
        return ResponseEntity.ok(result);
    }
*/
}
