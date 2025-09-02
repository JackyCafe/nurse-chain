package com.ian.nursechain.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.web.server.ResponseStatusException;

import com.ian.blockchain.models.Block;
import com.ian.blockchain.models.Blockchain;
import com.ian.blockchain.models.Transaction;
import com.ian.nursechain.dto.BlockchainCertifyResponseDTO;
import com.ian.nursechain.entity.NurseCertifications;
import com.ian.nursechain.repository.NurseCertificationRepository;

import jakarta.transaction.Transactional;

@Service
public class BlockchainService {

    private final NurseCertificationRepository repository;

    @Autowired
    public BlockchainService(NurseCertificationRepository nurseCertificationRepository) {
        this.repository = nurseCertificationRepository;
        if (Blockchain.blockchain.isEmpty()) {
            System.out.println("Initializing Blockchain: Creating Genesis Block...");
            Blockchain.createGenesisBlock();
        }
    }

    @Transactional
    public BlockchainCertifyResponseDTO certifyCertification(Long certificationId) {
        // 1. 從資料庫中獲取證書資料
        NurseCertifications certification = repository.findById(certificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nurse Certification not found with ID: " + certificationId));

        String transactionHash = "MOCK_TX_HASH_" + System.currentTimeMillis(); // 替換為實際的區塊鏈交易哈希
        boolean blockchainSuccess = true; // 替換為實際的區塊鏈操作結果

        // Todo 上鏈過程
        String transactionData = String.format(
                "CertID:%d;NurseID:%s;NurseName:%s;Subject:%s;Points:%.1f",
                certification.getId(),
                certification.getNurseInfo() != null ? certification.getNurseInfo().getIdentyNo() : "N/A",
                certification.getNurseInfo() != null ? certification.getNurseInfo().getName() : "N/A",
                certification.getSubject() != null ? certification.getSubject().getSubjectName() : "N/A",
                certification.getPoints());
        // 創建一個交易，這裡簡化了發送方和接收方
        Transaction newTransaction = new Transaction("System", "Blockchain", 0.0, transactionData);

        // 2. 獲取前一個區塊的哈希值
        String previousBlockHash = Blockchain.blockchain.isEmpty() ? "0"
                : Blockchain.blockchain.get(Blockchain.blockchain.size() - 1).getHash();

        // 3. 創建新的區塊，並加入交易
        List<Transaction> transactions = new ArrayList<>();
        transactions.add(newTransaction);
        Block newBlock = new Block(previousBlockHash, transactions);

        // 4. 執行 PoW (挖礦)，並將區塊添加到區塊鏈中
        System.out.println("Attempting to mine new block for certification ID: " + certificationId);
        Blockchain.addBlock(newBlock); // addBlock 內部會執行 mineBlock
        String blockHash = newBlock.getHash(); // 取得計算後的新區塊哈希
        String merkleRoot = newBlock.getMerkleRoot(); // 取得計算後的 Merkle Root
        long timestamp = newBlock.getTimestamp(); // 取得區塊時間戳
        int nonce = newBlock.getNonce(); // 取得挖礦後的 Nonce
        // 5. 驗證區塊鏈 (可選，但推薦用於測試)
        System.out.println("Is Blockchain Valid? " + Blockchain.isChainValid());

        // 6. 返回上鏈結果
        if (Blockchain.isChainValid()) { // 假設鏈有效則上鏈成功
            return new BlockchainCertifyResponseDTO(true, "Certification successfully sent to blockchain.",
                    blockHash, merkleRoot, timestamp, nonce);
        } else {
            return new BlockchainCertifyResponseDTO(false,
                    "Failed to send certification to blockchain. Blockchain invalid.", null, null, 0, 0);

        }

    }

     /**
     * 以 nurse_id 為key，將相同的證書上鏈，並分別統計每個護士的上鏈總時間、上鏈數量及總積分
     * @return 包含每個護士上鏈總時間、證書數量和總積分的 Map
     */
    public Map<Long, Map<String, Object>> certifyByNurseId() {
        Map<Long, Map<String, Object>> result = new HashMap<>();

        // 1. 取得所有證書並按 nurse_id 分組
        Map<Long, List<NurseCertifications>> certificationsByNurse = repository.findAll().stream()
                .collect(Collectors.groupingBy(nc -> nc.getNurseInfo().getId()));

        // 2. 遍歷每個護士的分組
        for (Map.Entry<Long, List<NurseCertifications>> entry : certificationsByNurse.entrySet()) {
            Long nurseId = entry.getKey();
            List<NurseCertifications> certifications = entry.getValue();

            // 創建兩個獨立的 Map 來存放專業類和非專業類的數據
            Map<String, Object> proStats = new HashMap<>();
            proStats.put("上鍊證書數量", 0);
            proStats.put("上鍊總積分", 0.0f);
            proStats.put("上鍊總時間(秒)", 0.0);

            Map<String, Object> nonProStats = new HashMap<>();
            nonProStats.put("上鍊證書數量", 0);
            nonProStats.put("上鍊總積分", 0.0f);
            nonProStats.put("上鍊總時間(秒)", 0.0);

            // 3. 遍歷該護士的所有證書並逐一上鏈
            for (NurseCertifications cert : certifications) {
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                try {
                    certifyCertification(cert.getId());
                } catch (Exception e) {
                    System.err.println("上鏈失敗 for Certification ID: " + cert.getId() + " - " + e.getMessage());
                }
                stopWatch.stop();

                if (cert.getSubject().getSubjectCode() == 1) {
                    // 更新專業類統計
                    proStats.put("上鍊證書數量", (int) proStats.get("上鍊證書數量") + 1);
                    proStats.put("上鍊總積分", (float) proStats.get("上鍊總積分") + cert.getPoints());
                    proStats.put("上鍊總時間(秒)", (double) proStats.get("上鍊總時間(秒)") + (stopWatch.getLastTaskTimeMillis() / 1000.0));
                } else {
                    // 更新非專業類統計
                    nonProStats.put("上鍊證書數量", (int) nonProStats.get("上鍊證書數量") + 1);
                    nonProStats.put("上鍊總積分", (float) nonProStats.get("上鍊總積分") + cert.getPoints());
                    nonProStats.put("上鍊總時間(秒)", (double) nonProStats.get("上鍊總時間(秒)") + (stopWatch.getLastTaskTimeMillis() / 1000.0));
                }
            }

            // 將專業類和非專業類的統計結果放入總結果地圖
            Map<String, Object> nurseTotalStats = new HashMap<>();
            nurseTotalStats.put("專業類", proStats);
            nurseTotalStats.put("非專業類", nonProStats);
            result.put(nurseId, nurseTotalStats);
        }

        return result;
    }
}