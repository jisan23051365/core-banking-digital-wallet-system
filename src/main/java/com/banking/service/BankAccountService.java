package com.banking.service;

import com.banking.dto.BankAccountRequest;
import com.banking.dto.BankAccountResponse;
import com.banking.entity.BankAccount;
import com.banking.entity.Customer;
import com.banking.enums.AccountStatus;
import com.banking.exception.AccountStatusException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.BankAccountRepository;
import com.banking.repository.CustomerRepository;
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
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final CustomerRepository customerRepository;

    @Transactional
    public BankAccountResponse createAccount(BankAccountRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", request.getCustomerId()));

        String accountNumber = generateAccountNumber();

        BankAccount account = BankAccount.builder()
                .accountNumber(accountNumber)
                .accountType(request.getAccountType())
                .balance(request.getInitialDeposit() != null ? request.getInitialDeposit() : BigDecimal.ZERO)
                .status(AccountStatus.ACTIVE)
                .customer(customer)
                .build();

        BankAccount saved = bankAccountRepository.save(account);
        return toResponse(saved);
    }

    public BankAccountResponse getAccountById(Long id) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "id", id));
        return toResponse(account);
    }

    public BankAccountResponse getAccountByNumber(String accountNumber) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "accountNumber", accountNumber));
        return toResponse(account);
    }

    public List<BankAccountResponse> getAccountsByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer", "id", customerId);
        }
        return bankAccountRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<BankAccountResponse> getAllAccounts() {
        return bankAccountRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BankAccountResponse updateAccountStatus(Long id, AccountStatus status) {
        BankAccount account = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "id", id));
        if (account.getStatus() == AccountStatus.CLOSED) {
            throw new AccountStatusException("Cannot update status of a closed account");
        }
        account.setStatus(status);
        return toResponse(bankAccountRepository.save(account));
    }

    public BankAccount getAccountEntityByNumber(String accountNumber) {
        return bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "accountNumber", accountNumber));
    }

    private String generateAccountNumber() {
        String number;
        do {
            number = "ACC" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
        } while (bankAccountRepository.existsByAccountNumber(number));
        return number;
    }

    public BankAccountResponse toResponse(BankAccount account) {
        return BankAccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .customerId(account.getCustomer().getId())
                .customerName(account.getCustomer().getFirstName() + " " + account.getCustomer().getLastName())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}
