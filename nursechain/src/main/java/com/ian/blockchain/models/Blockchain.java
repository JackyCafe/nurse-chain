package com.ian.blockchain.models;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    public static List<Block> blockchain = new ArrayList<>();
    public static int difficulty = 3; // 挖礦難度，前綴零的數量

    // 創世區塊 (Genesis Block) 的建立
    public static void createGenesisBlock() {
        // 第一個區塊通常沒有前一個哈希
        Block genesisBlock = new Block("0", new ArrayList<>());
        System.out.println("Mining Genesis Block...");
        genesisBlock.mineBlock(difficulty); // 對創世區塊進行挖礦
        blockchain.add(genesisBlock);
    }

    // 將新區塊添加到鏈中
    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty); // 在添加到鏈之前進行挖礦
        blockchain.add(newBlock);
    }

    // 驗證區塊鏈是否有效
    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0', '0');

        // 遍歷區塊鏈以檢查哈希值
        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i - 1);

            // 檢查當前區塊的哈希是否正確計算
            if (!currentBlock.getHash().equals(currentBlock.calculateHash())) {
                System.out.println("Current Hashes not equal");
                return false;
            }

            // 檢查前一個區塊的哈希是否與儲存的 previousHash 匹配
            if (!previousBlock.getHash().equals(currentBlock.getPreviousHash())) {
                System.out.println("Previous Hashes not equal");
                return false;
            }

            // 檢查區塊哈希是否滿足難度要求
            if (!currentBlock.getHash().substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }
        }
        return true;
    }

}
