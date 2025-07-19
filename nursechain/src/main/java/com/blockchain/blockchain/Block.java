package com.blockchain.blockchain;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import com.google.common.hash.Hashing;

import lombok.Data;

@Data
public class Block {
    private int index; // 區塊在鏈中的位置
    private LocalDateTime timestamp; // 區塊建立時間
    private List<Transaction> transactions; // 區塊中包含的交易列表
    private int nonce; // 工作量證明的隨機數
    private String previousHash; // 前一個區塊的雜湊值
    private String hash; // 當前區塊的雜湊值

    public Block(int index, String previousHash, List<Transaction> transactions) {
        this.index = index;
        this.timestamp = LocalDateTime.now();
        this.transactions = transactions;
        this.previousHash = previousHash;
        this.nonce = 0; // 初始 nonce 值
        this.hash = calculateHash(); // 計算初始雜湊值
    }

    // 計算區塊的雜湊值
    public String calculateHash() {
        String dataToHash = index + timestamp.toString() + transactions.toString() + nonce + previousHash;
        return Hashing.sha256()
                .hashString(dataToHash, StandardCharsets.UTF_8)
                .toString();
    }

    // 執行工作量證明，找到符合難度要求的雜湊值
    public void mineBlock(int difficulty) {
        String targetPrefix = new String(new char[difficulty]).replace('\0', '0'); // 例如 "00"
        while (!hash.substring(0, difficulty).equals(targetPrefix)) {
            nonce++;
            hash = calculateHash();
        }
        System.out.println("Block mined: " + hash);
    }
}