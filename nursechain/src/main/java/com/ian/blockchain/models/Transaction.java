package com.ian.blockchain.models;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.ian.blockchain.misc.StringUtil;

import lombok.Data;

@Data
public class Transaction implements Serializable {
    private String sender;
    private String recipient;
    private double amount; // 機
    private String data; // 交易的具體數據 (例如：證書 ID、護士 ID、科目等)
    private LocalDateTime timestamp; // 交易創建時間

    public Transaction(String sender, String recipient, double amount, String data) {
        this.sender = sender;
        this.recipient = recipient;
        this.amount = amount;
        this.data = data;
        this.timestamp = LocalDateTime.now();
    }

    // 可以在這裡添加用於生成交易哈希的方法，例如使用 SHA256
    public String calculateHash() {
        String transactionData = this.sender + this.recipient + this.amount + this.data + this.timestamp.toString();
        return StringUtil.applySha256(transactionData); // 需要一個 StringUtil 類別
    }

}
