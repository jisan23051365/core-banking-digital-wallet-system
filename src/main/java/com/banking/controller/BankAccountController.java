package com.banking.controller;

import com.banking.dto.ApiResponse;
import com.banking.dto.BankAccountRequest;
import com.banking.dto.BankAccountResponse;
import com.banking.enums.AccountStatus;
import com.banking.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @PostMapping
    public ResponseEntity<ApiResponse<BankAccountResponse>> createAccount(
            @Valid @RequestBody BankAccountRequest request) {
        BankAccountResponse response = bankAccountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Bank account created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BankAccountResponse>> getAccountById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getAccountById(id)));
    }

    @GetMapping("/number/{accountNumber}")
    public ResponseEntity<ApiResponse<BankAccountResponse>> getAccountByNumber(
            @PathVariable String accountNumber) {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getAccountByNumber(accountNumber)));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<BankAccountResponse>>> getAccountsByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getAccountsByCustomerId(customerId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BankAccountResponse>>> getAllAccounts() {
        return ResponseEntity.ok(ApiResponse.success(bankAccountService.getAllAccounts()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<BankAccountResponse>> updateAccountStatus(
            @PathVariable Long id, @RequestParam AccountStatus status) {
        BankAccountResponse response = bankAccountService.updateAccountStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Account status updated successfully", response));
    }
}
