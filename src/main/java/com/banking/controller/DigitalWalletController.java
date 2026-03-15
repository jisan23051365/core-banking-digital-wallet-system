package com.banking.controller;

import com.banking.dto.ApiResponse;
import com.banking.dto.WalletRequest;
import com.banking.dto.WalletResponse;
import com.banking.enums.WalletStatus;
import com.banking.service.DigitalWalletService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wallets")
@RequiredArgsConstructor
public class DigitalWalletController {

    private final DigitalWalletService walletService;

    @PostMapping
    public ResponseEntity<ApiResponse<WalletResponse>> createWallet(
            @Valid @RequestBody WalletRequest request) {
        WalletResponse response = walletService.createWallet(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Digital wallet created successfully", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(walletService.getWalletById(id)));
    }

    @GetMapping("/number/{walletNumber}")
    public ResponseEntity<ApiResponse<WalletResponse>> getWalletByNumber(@PathVariable String walletNumber) {
        return ResponseEntity.ok(ApiResponse.success(walletService.getWalletByNumber(walletNumber)));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<WalletResponse>>> getWalletsByCustomer(
            @PathVariable Long customerId) {
        return ResponseEntity.ok(ApiResponse.success(walletService.getWalletsByCustomerId(customerId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<WalletResponse>>> getAllWallets() {
        return ResponseEntity.ok(ApiResponse.success(walletService.getAllWallets()));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<WalletResponse>> updateWalletStatus(
            @PathVariable Long id, @RequestParam WalletStatus status) {
        WalletResponse response = walletService.updateWalletStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Wallet status updated successfully", response));
    }
}
