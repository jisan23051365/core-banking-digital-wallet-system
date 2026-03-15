package com.banking.service;

import com.banking.dto.WalletRequest;
import com.banking.dto.WalletResponse;
import com.banking.entity.Customer;
import com.banking.entity.DigitalWallet;
import com.banking.enums.WalletStatus;
import com.banking.exception.AccountStatusException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.CustomerRepository;
import com.banking.repository.DigitalWalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DigitalWalletService {

    private final DigitalWalletRepository walletRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public WalletResponse createWallet(WalletRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        String walletNumber = generateWalletNumber();

        DigitalWallet wallet = DigitalWallet.builder()
                .walletNumber(walletNumber)
                .balance(request.getInitialBalance() != null ? request.getInitialBalance() : BigDecimal.ZERO)
                .status(WalletStatus.ACTIVE)
                .customer(customer)
                .build();

        DigitalWallet saved = walletRepository.save(wallet);
        return toResponse(saved);
    }

    public WalletResponse getWalletById(Long id) {
        DigitalWallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DigitalWallet", "id", id));
        return toResponse(wallet);
    }

    public WalletResponse getWalletByNumber(String walletNumber) {
        DigitalWallet wallet = walletRepository.findByWalletNumber(walletNumber)
                .orElseThrow(() -> new ResourceNotFoundException("DigitalWallet", "walletNumber", walletNumber));
        return toResponse(wallet);
    }

    public List<WalletResponse> getWalletsByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", "id", customerId);
        }
        return walletRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<WalletResponse> getAllWallets() {
        return walletRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WalletResponse updateWalletStatus(Long id, WalletStatus status) {
        DigitalWallet wallet = walletRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DigitalWallet", "id", id));
        if (wallet.getStatus() == status) {
            throw new AccountStatusException("Wallet is already in status: " + status);
        }
        wallet.setStatus(status);
        return toResponse(walletRepository.save(wallet));
    }

    public DigitalWallet getWalletEntityByNumber(String walletNumber) {
        return walletRepository.findByWalletNumber(walletNumber)
                .orElseThrow(() -> new ResourceNotFoundException("DigitalWallet", "walletNumber", walletNumber));
    }

    private String generateWalletNumber() {
        String number;
        do {
            number = "WAL" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
        } while (walletRepository.existsByWalletNumber(number));
        return number;
    }

    public WalletResponse toResponse(DigitalWallet wallet) {
        return WalletResponse.builder()
                .id(wallet.getId())
                .walletNumber(wallet.getWalletNumber())
                .balance(wallet.getBalance())
                .status(wallet.getStatus())
                .customerId(wallet.getCustomer().getId())
                .customerName(wallet.getCustomer().getFirstName() + " " + wallet.getCustomer().getLastName())
                .createdAt(wallet.getCreatedAt())
                .updatedAt(wallet.getUpdatedAt())
                .build();
    }
}
