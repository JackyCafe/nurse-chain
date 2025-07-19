package com.blockchain.blockchain;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    private String sender; // 發送者（例如，發證機構ID）
    private String recipient; // 接收者（例如，護士ID）
    private String certificateDetails; // 證書詳細資訊（例如，JSON 格式的證書數據）
    private LocalDateTime timestamp;

    public Transaction(String sender, String recipient, String certificateDetails) {
        this.sender = sender;
        this.recipient = recipient;
        this.certificateDetails = certificateDetails;
        this.timestamp = LocalDateTime.now();
    }
}