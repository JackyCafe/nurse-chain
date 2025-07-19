package com.ian.blockchain.models;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

import com.ian.blockchain.misc.StringUtil;

import lombok.Data;

@Data
public class Block implements Serializable {

    private String hash; // 當前區塊的哈希值
    private String previousHash; // 前一個區塊的哈希值
    private List<Transaction> transactions; // 區塊包含的交易列表
    private long timestamp; // 區塊創建的時間戳 (Unix 時間)
    private int nonce; // 工作量證明中的隨機數 (用於挖礦)
    private String merkleRoot; // Merkle Root (用於驗證交易完整性，簡化版可以省略)

    // 建構子
    public Block(String previousHash, List<Transaction> transactions) {
        this.previousHash = previousHash;
        this.transactions = transactions;
        this.timestamp = Instant.now().toEpochMilli();
        this.nonce = 0; // 初始 nonce
        this.merkleRoot = calculateMerkleRoot(); // 計算 Merkle Root
        this.hash = calculateHash(); // 計算區塊哈希
    }

    public String calculateMerkleRoot() {
        if (transactions == null || transactions.isEmpty()) {
            return " ";
        }
        StringBuilder transactionHashs = new StringBuilder();
        for (Transaction tx : transactions) {
            transactionHashs.append(tx.calculateHash());
        }
        return StringUtil.applySha256(transactionHashs.toString());
    }

    public String calculateHash() {
        String hash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timestamp) +
                        Integer.toString(nonce) + merkleRoot);
        return hash;
    }

    // // 工作量證明 (PoW) - 挖礦方法
    public void mineBlock(int difficulty) {
        // 目標哈希的前綴應包含指定數量的零
        String target = new String(new char[difficulty]).replace('\0', '0');
        while (!hash.substring(0, difficulty).equals(target)) {
            nonce++; // 不斷增加 nonce 值
            hash = calculateHash(); // 重新計算哈希
        }
        System.out.println("Block Mined!!! : " + hash);
    }

}
