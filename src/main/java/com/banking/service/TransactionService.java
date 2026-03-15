package com.banking.service;

import com.banking.dto.TransactionRequest;
import com.banking.dto.TransactionResponse;
import com.banking.dto.TransferRequest;
import com.banking.dto.WalletTransactionRequest;
import com.banking.entity.BankAccount;
import com.banking.entity.DigitalWallet;
import com.banking.entity.Transaction;
import com.banking.enums.AccountStatus;
import com.banking.enums.TransactionStatus;
import com.banking.enums.TransactionType;
import com.banking.enums.WalletStatus;
import com.banking.exception.AccountStatusException;
import com.banking.exception.InsufficientFundsException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.BankAccountRepository;
import com.banking.repository.DigitalWalletRepository;
import com.banking.repository.TransactionRepository;
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
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BankAccountRepository bankAccountRepository;
    private final DigitalWalletRepository walletRepository;

    @Transactional
    public TransactionResponse deposit(TransactionRequest request) {
        BankAccount account = bankAccountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "accountNumber", request.getAccountNumber()));

        validateAccountActive(account);

        account.setBalance(account.getBalance().add(request.getAmount()));
        bankAccountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .referenceNumber(generateReferenceNumber())
                .type(TransactionType.DEPOSIT)
                .status(TransactionStatus.COMPLETED)
                .amount(request.getAmount())
                .description(request.getDescription() != null ? request.getDescription() : "Deposit")
                .targetAccount(account)
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse withdraw(TransactionRequest request) {
        BankAccount account = bankAccountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "accountNumber", request.getAccountNumber()));

        validateAccountActive(account);
        validateSufficientFunds(account.getBalance(), request.getAmount());

        account.setBalance(account.getBalance().subtract(request.getAmount()));
        bankAccountRepository.save(account);

        Transaction transaction = Transaction.builder()
                .referenceNumber(generateReferenceNumber())
                .type(TransactionType.WITHDRAWAL)
                .status(TransactionStatus.COMPLETED)
                .amount(request.getAmount())
                .description(request.getDescription() != null ? request.getDescription() : "Withdrawal")
                .sourceAccount(account)
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse transfer(TransferRequest request) {
        if (request.getSourceAccountNumber().equals(request.getTargetAccountNumber())) {
            throw new IllegalArgumentException("Source and target accounts cannot be the same");
        }

        BankAccount sourceAccount = bankAccountRepository.findByAccountNumber(request.getSourceAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "accountNumber", request.getSourceAccountNumber()));
        BankAccount targetAccount = bankAccountRepository.findByAccountNumber(request.getTargetAccountNumber())
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "accountNumber", request.getTargetAccountNumber()));

        validateAccountActive(sourceAccount);
        validateAccountActive(targetAccount);
        validateSufficientFunds(sourceAccount.getBalance(), request.getAmount());

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(request.getAmount()));
        targetAccount.setBalance(targetAccount.getBalance().add(request.getAmount()));

        bankAccountRepository.save(sourceAccount);
        bankAccountRepository.save(targetAccount);

        Transaction transaction = Transaction.builder()
                .referenceNumber(generateReferenceNumber())
                .type(TransactionType.TRANSFER)
                .status(TransactionStatus.COMPLETED)
                .amount(request.getAmount())
                .description(request.getDescription() != null ? request.getDescription() : "Transfer")
                .sourceAccount(sourceAccount)
                .targetAccount(targetAccount)
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse walletTopUp(WalletTransactionRequest request) {
        DigitalWallet wallet = walletRepository.findByWalletNumber(request.getWalletNumber())
                .orElseThrow(() -> new ResourceNotFoundException("DigitalWallet", "walletNumber", request.getWalletNumber()));

        validateWalletActive(wallet);

        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .referenceNumber(generateReferenceNumber())
                .type(TransactionType.WALLET_TOPUP)
                .status(TransactionStatus.COMPLETED)
                .amount(request.getAmount())
                .description(request.getDescription() != null ? request.getDescription() : "Wallet top-up")
                .wallet(wallet)
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse walletWithdraw(WalletTransactionRequest request) {
        DigitalWallet wallet = walletRepository.findByWalletNumber(request.getWalletNumber())
                .orElseThrow(() -> new ResourceNotFoundException("DigitalWallet", "walletNumber", request.getWalletNumber()));

        validateWalletActive(wallet);
        validateSufficientFunds(wallet.getBalance(), request.getAmount());

        wallet.setBalance(wallet.getBalance().subtract(request.getAmount()));
        walletRepository.save(wallet);

        Transaction transaction = Transaction.builder()
                .referenceNumber(generateReferenceNumber())
                .type(TransactionType.WALLET_WITHDRAW)
                .status(TransactionStatus.COMPLETED)
                .amount(request.getAmount())
                .description(request.getDescription() != null ? request.getDescription() : "Wallet withdrawal")
                .wallet(wallet)
                .build();

        return toResponse(transactionRepository.save(transaction));
    }

    public TransactionResponse getTransactionById(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "id", id));
        return toResponse(transaction);
    }

    public TransactionResponse getTransactionByReference(String referenceNumber) {
        Transaction transaction = transactionRepository.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction", "referenceNumber", referenceNumber));
        return toResponse(transaction);
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findAllByAccountId(accountId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByAccountNumber(String accountNumber) {
        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new ResourceNotFoundException("BankAccount", "accountNumber", accountNumber));
        return transactionRepository.findAllByAccountId(account.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByWalletNumber(String walletNumber) {
        DigitalWallet wallet = walletRepository.findByWalletNumber(walletNumber)
                .orElseThrow(() -> new ResourceNotFoundException("DigitalWallet", "walletNumber", walletNumber));
        return transactionRepository.findByWalletId(wallet.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByCustomerId(Long customerId) {
        return transactionRepository.findAllByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private void validateAccountActive(BankAccount account) {
        if (account.getStatus() != AccountStatus.ACTIVE) {
            throw new AccountStatusException(
                    "Account " + account.getAccountNumber() + " is not active. Current status: " + account.getStatus());
        }
    }

    private void validateWalletActive(DigitalWallet wallet) {
        if (wallet.getStatus() != WalletStatus.ACTIVE) {
            throw new AccountStatusException(
                    "Wallet " + wallet.getWalletNumber() + " is not active. Current status: " + wallet.getStatus());
        }
    }

    private void validateSufficientFunds(BigDecimal available, BigDecimal requested) {
        if (available.compareTo(requested) < 0) {
            throw new InsufficientFundsException(available, requested);
        }
    }

    private String generateReferenceNumber() {
        return "TXN" + UUID.randomUUID().toString().replace("-", "").toUpperCase();
    }

    public TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .referenceNumber(transaction.getReferenceNumber())
                .type(transaction.getType())
                .status(transaction.getStatus())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .sourceAccountNumber(transaction.getSourceAccount() != null
                        ? transaction.getSourceAccount().getAccountNumber() : null)
                .targetAccountNumber(transaction.getTargetAccount() != null
                        ? transaction.getTargetAccount().getAccountNumber() : null)
                .walletNumber(transaction.getWallet() != null
                        ? transaction.getWallet().getWalletNumber() : null)
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
