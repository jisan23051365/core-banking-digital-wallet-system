package com.banking.controller;

import com.banking.dto.ApiResponse;
import com.banking.dto.TransactionRequest;
import com.banking.dto.TransactionResponse;
import com.banking.dto.TransferRequest;
import com.banking.dto.WalletTransactionRequest;
import com.banking.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TransactionResponse>> deposit(
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.deposit(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Deposit completed successfully", response));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> withdraw(
            @Valid @RequestBody TransactionRequest request) {
        TransactionResponse response = transactionService.withdraw(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Withdrawal completed successfully", response));
    }

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transfer(
            @Valid @RequestBody TransferRequest request) {
        TransactionResponse response = transactionService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Transfer completed successfully", response));
    }

    @PostMapping("/wallet/topup")
    public ResponseEntity<ApiResponse<TransactionResponse>> walletTopUp(
            @Valid @RequestBody WalletTransactionRequest request) {
        TransactionResponse response = transactionService.walletTopUp(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Wallet top-up completed successfully", response));
    }

    @PostMapping("/wallet/withdraw")
    public ResponseEntity<ApiResponse<TransactionResponse>> walletWithdraw(
            @Valid @RequestBody WalletTransactionRequest request) {
        TransactionResponse response = transactionService.walletWithdraw(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Wallet withdrawal completed successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransactionById(id)));
    }

    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionByReference(
            @PathVariable String referenceNumber) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransactionByReference(referenceNumber)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getAllTransactions() {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getAllTransactions()));
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByAccountId(
            @PathVariable Long accountId) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransactionsByAccountId(accountId)));
    }

    @GetMapping("/account/number/{accountNumber}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByAccountNumber(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransactionsByAccountNumber(accountNumber)));
    }

    @GetMapping("/wallet/{walletNumber}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByWallet(
            @PathVariable String walletNumber) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransactionsByWalletNumber(walletNumber)));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionsByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(transactionService.getTransactionsByCustomerId(customerId)));
    }
}
