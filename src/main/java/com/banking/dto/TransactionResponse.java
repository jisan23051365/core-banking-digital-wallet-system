package com.banking.dto;

import com.banking.enums.TransactionStatus;
import com.banking.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {

    private Long id;
    private String referenceNumber;
    private TransactionType type;
    private TransactionStatus status;
    private BigDecimal amount;
    private String description;
    private String sourceAccountNumber;
    private String targetAccountNumber;
    private String walletNumber;
    private LocalDateTime createdAt;
}
