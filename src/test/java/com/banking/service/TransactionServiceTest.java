package com.banking.service;

import com.banking.dto.TransactionRequest;
import com.banking.dto.TransactionResponse;
import com.banking.dto.TransferRequest;
import com.banking.entity.BankAccount;
import com.banking.entity.Customer;
import com.banking.enums.AccountStatus;
import com.banking.enums.AccountType;
import com.banking.enums.TransactionStatus;
import com.banking.enums.TransactionType;
import com.banking.exception.AccountStatusException;
import com.banking.exception.InsufficientFundsException;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.BankAccountRepository;
import com.banking.repository.DigitalWalletRepository;
import com.banking.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private DigitalWalletRepository walletRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Customer customer;
    private BankAccount sourceAccount;
    private BankAccount targetAccount;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();

        sourceAccount = BankAccount.builder()
                .id(1L)
                .accountNumber("ACC000001")
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("1000.00"))
                .status(AccountStatus.ACTIVE)
                .customer(customer)
                .build();

        targetAccount = BankAccount.builder()
                .id(2L)
                .accountNumber("ACC000002")
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("500.00"))
                .status(AccountStatus.ACTIVE)
                .customer(customer)
                .build();
    }

    @Test
    void deposit_success() {
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("ACC000001");
        request.setAmount(new BigDecimal("200.00"));
        request.setDescription("Test deposit");

        when(bankAccountRepository.findByAccountNumber("ACC000001")).thenReturn(Optional.of(sourceAccount));
        when(bankAccountRepository.save(any())).thenReturn(sourceAccount);
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            com.banking.entity.Transaction t = inv.getArgument(0);
            t = com.banking.entity.Transaction.builder()
                    .id(1L)
                    .referenceNumber(t.getReferenceNumber())
                    .type(TransactionType.DEPOSIT)
                    .status(TransactionStatus.COMPLETED)
                    .amount(t.getAmount())
                    .description(t.getDescription())
                    .targetAccount(sourceAccount)
                    .build();
            return t;
        });

        TransactionResponse response = transactionService.deposit(request);

        assertThat(response.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(response.getAmount()).isEqualByComparingTo("200.00");
        verify(bankAccountRepository).save(any());
    }

    @Test
    void deposit_accountNotFound_throwsResourceNotFoundException() {
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("NONEXISTENT");
        request.setAmount(new BigDecimal("100.00"));

        when(bankAccountRepository.findByAccountNumber("NONEXISTENT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.deposit(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void withdraw_success() {
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("ACC000001");
        request.setAmount(new BigDecimal("300.00"));
        request.setDescription("Test withdrawal");

        when(bankAccountRepository.findByAccountNumber("ACC000001")).thenReturn(Optional.of(sourceAccount));
        when(bankAccountRepository.save(any())).thenReturn(sourceAccount);
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            com.banking.entity.Transaction t = inv.getArgument(0);
            t = com.banking.entity.Transaction.builder()
                    .id(2L)
                    .referenceNumber(t.getReferenceNumber())
                    .type(TransactionType.WITHDRAWAL)
                    .status(TransactionStatus.COMPLETED)
                    .amount(t.getAmount())
                    .description(t.getDescription())
                    .sourceAccount(sourceAccount)
                    .build();
            return t;
        });

        TransactionResponse response = transactionService.withdraw(request);

        assertThat(response.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        verify(bankAccountRepository).save(any());
    }

    @Test
    void withdraw_insufficientFunds_throwsInsufficientFundsException() {
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("ACC000001");
        request.setAmount(new BigDecimal("5000.00"));

        when(bankAccountRepository.findByAccountNumber("ACC000001")).thenReturn(Optional.of(sourceAccount));

        assertThatThrownBy(() -> transactionService.withdraw(request))
                .isInstanceOf(InsufficientFundsException.class);
    }

    @Test
    void withdraw_inactiveAccount_throwsAccountStatusException() {
        sourceAccount.setStatus(AccountStatus.SUSPENDED);
        TransactionRequest request = new TransactionRequest();
        request.setAccountNumber("ACC000001");
        request.setAmount(new BigDecimal("100.00"));

        when(bankAccountRepository.findByAccountNumber("ACC000001")).thenReturn(Optional.of(sourceAccount));

        assertThatThrownBy(() -> transactionService.withdraw(request))
                .isInstanceOf(AccountStatusException.class);
    }

    @Test
    void transfer_success() {
        TransferRequest request = new TransferRequest();
        request.setSourceAccountNumber("ACC000001");
        request.setTargetAccountNumber("ACC000002");
        request.setAmount(new BigDecimal("200.00"));
        request.setDescription("Test transfer");

        when(bankAccountRepository.findByAccountNumber("ACC000001")).thenReturn(Optional.of(sourceAccount));
        when(bankAccountRepository.findByAccountNumber("ACC000002")).thenReturn(Optional.of(targetAccount));
        when(bankAccountRepository.save(any())).thenReturn(sourceAccount);
        when(transactionRepository.save(any())).thenAnswer(inv -> {
            com.banking.entity.Transaction t = inv.getArgument(0);
            t = com.banking.entity.Transaction.builder()
                    .id(3L)
                    .referenceNumber(t.getReferenceNumber())
                    .type(TransactionType.TRANSFER)
                    .status(TransactionStatus.COMPLETED)
                    .amount(t.getAmount())
                    .description(t.getDescription())
                    .sourceAccount(sourceAccount)
                    .targetAccount(targetAccount)
                    .build();
            return t;
        });

        TransactionResponse response = transactionService.transfer(request);

        assertThat(response.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(response.getAmount()).isEqualByComparingTo("200.00");
        verify(bankAccountRepository, times(2)).save(any());
    }

    @Test
    void transfer_sameAccount_throwsIllegalArgumentException() {
        TransferRequest request = new TransferRequest();
        request.setSourceAccountNumber("ACC000001");
        request.setTargetAccountNumber("ACC000001");
        request.setAmount(new BigDecimal("100.00"));

        assertThatThrownBy(() -> transactionService.transfer(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("same");
    }

    @Test
    void transfer_insufficientFunds_throwsInsufficientFundsException() {
        TransferRequest request = new TransferRequest();
        request.setSourceAccountNumber("ACC000001");
        request.setTargetAccountNumber("ACC000002");
        request.setAmount(new BigDecimal("9999.00"));

        when(bankAccountRepository.findByAccountNumber("ACC000001")).thenReturn(Optional.of(sourceAccount));
        when(bankAccountRepository.findByAccountNumber("ACC000002")).thenReturn(Optional.of(targetAccount));

        assertThatThrownBy(() -> transactionService.transfer(request))
                .isInstanceOf(InsufficientFundsException.class);
    }
}
