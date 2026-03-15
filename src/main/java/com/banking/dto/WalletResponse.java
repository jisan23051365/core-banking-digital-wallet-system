package com.banking.dto;

import com.banking.enums.WalletStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletResponse {

    private Long id;
    private String walletNumber;
    private BigDecimal balance;
    private WalletStatus status;
    private Long customerId;
    private String customerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
