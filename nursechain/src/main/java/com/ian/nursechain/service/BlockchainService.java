package com.ian.nursechain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.ian.nursechain.dto.BlockchainCertifyResponseDTO;
import com.ian.nursechain.entity.NurseCertifications;
import com.ian.nursechain.repository.NurseCertificationRepository;

import jakarta.transaction.Transactional;

@Service
public class BlockchainService {
    /*
     * NureChain 上鏈後將將資料送到 BlockchainService 處理
     * 
     * 
     * 
     */

    private final NurseCertificationRepository repository;

    @Autowired
    public BlockchainService(NurseCertificationRepository nurseCertificationRepository) {
        this.repository = nurseCertificationRepository;
        // this.blockchainClient = blockchainClient;
    }

    @Transactional
    public BlockchainCertifyResponseDTO certifyCertification(Long certificationId) {
        // 1. 從資料庫中獲取證書資料
        NurseCertifications certification = repository.findById(certificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nurse Certification not found with ID: " + certificationId));

        // 2. 準備要上鏈的數據 (例如，證書ID、護士姓名、科目、積分、時間戳等)
        // 您可能需要將 NurseCertifications 實體轉換成區塊鏈所需格式的數據
        // String dataToCertify = String.format("Certification ID: %d, Nurse: %s,
        // Subject: %s, Points: %.1f",
        // certification.getId(),
        // certification.getNurseInfo() != null ? certification.getNurseInfo().getName()
        // : "N/A",
        // certification.getSubject() != null ?
        // certification.getSubject().getSubjectName() : "N/A",
        // certification.getPoints());

        String transactionHash = "MOCK_TX_HASH_" + System.currentTimeMillis(); // 替換為實際的區塊鏈交易哈希
        boolean blockchainSuccess = true; // 替換為實際的區塊鏈操作結果

        // Todo 上鏈過程
        // 5. 構建並返回響應
        if (blockchainSuccess) {
            return new BlockchainCertifyResponseDTO(true, "Certification successfully sent to blockchain.",
                    transactionHash);
        } else {
            return new BlockchainCertifyResponseDTO(false, "Failed to send certification to blockchain.", null);
        }

    }
}
