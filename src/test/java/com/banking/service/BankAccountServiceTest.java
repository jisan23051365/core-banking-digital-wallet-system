package com.banking.service;

import com.banking.dto.BankAccountRequest;
import com.banking.dto.BankAccountResponse;
import com.banking.entity.BankAccount;
import com.banking.entity.Customer;
import com.banking.enums.AccountStatus;
import com.banking.enums.AccountType;
import com.banking.exception.ResourceNotFoundException;
import com.banking.repository.BankAccountRepository;
import com.banking.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BankAccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private BankAccountService bankAccountService;

    private Customer customer;
    private BankAccount bankAccount;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1L)
                .firstName("Jane")
                .lastName("Smith")
                .email("jane.smith@example.com")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bankAccount = BankAccount.builder()
                .id(1L)
                .accountNumber("ACC000001")
                .accountType(AccountType.SAVINGS)
                .balance(new BigDecimal("500.00"))
                .status(AccountStatus.ACTIVE)
                .customer(customer)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createAccount_success() {
        BankAccountRequest request = new BankAccountRequest();
        request.setCustomerId(1L);
        request.setAccountType(AccountType.SAVINGS);
        request.setInitialDeposit(new BigDecimal("500.00"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(bankAccountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(bankAccountRepository.save(any())).thenReturn(bankAccount);

        BankAccountResponse response = bankAccountService.createAccount(request);

        assertThat(response.getAccountType()).isEqualTo(AccountType.SAVINGS);
        assertThat(response.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(response.getCustomerId()).isEqualTo(1L);
        verify(bankAccountRepository).save(any());
    }

    @Test
    void createAccount_customerNotFound_throwsResourceNotFoundException() {
        BankAccountRequest request = new BankAccountRequest();
        request.setCustomerId(99L);
        request.setAccountType(AccountType.SAVINGS);

        when(customerRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankAccountService.createAccount(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getAccountById_success() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));

        BankAccountResponse response = bankAccountService.getAccountById(1L);

        assertThat(response.getAccountNumber()).isEqualTo("ACC000001");
        assertThat(response.getBalance()).isEqualByComparingTo("500.00");
    }

    @Test
    void getAccountById_notFound_throwsResourceNotFoundException() {
        when(bankAccountRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bankAccountService.getAccountById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void getAccountsByCustomerId_success() {
        when(customerRepository.existsById(1L)).thenReturn(true);
        when(bankAccountRepository.findByCustomerId(1L)).thenReturn(List.of(bankAccount));

        List<BankAccountResponse> responses = bankAccountService.getAccountsByCustomerId(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getAccountNumber()).isEqualTo("ACC000001");
    }

    @Test
    void updateAccountStatus_success() {
        when(bankAccountRepository.findById(1L)).thenReturn(Optional.of(bankAccount));
        when(bankAccountRepository.save(any())).thenReturn(bankAccount);

        BankAccountResponse response = bankAccountService.updateAccountStatus(1L, AccountStatus.INACTIVE);

        verify(bankAccountRepository).save(any());
    }
}
