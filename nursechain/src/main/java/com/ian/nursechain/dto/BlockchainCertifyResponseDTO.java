package com.ian.nursechain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Lombok: 自動生成 getter, setter, toString, equals, hashCode
@NoArgsConstructor // Lombok: 生成無參建構子
@AllArgsConstructor // Lombok: 生成包含所有字段的建構子
public class BlockchainCertifyResponseDTO {
    private boolean success;
    private String message;
    private String blockHash;
    private String merkleRoot;
    private long timestamp;
    private int nonce;

}
