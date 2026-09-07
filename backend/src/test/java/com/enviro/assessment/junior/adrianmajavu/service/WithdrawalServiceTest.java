package com.enviro.assessment.junior.adrianmajavu.service;

import com.enviro.assessment.junior.adrianmajavu.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.adrianmajavu.entity.Investor;
import com.enviro.assessment.junior.adrianmajavu.entity.Portfolio;
import com.enviro.assessment.junior.adrianmajavu.entity.WithdrawalNotice;
import com.enviro.assessment.junior.adrianmajavu.entity.WithdrawalType;
import com.enviro.assessment.junior.adrianmajavu.exception.InvalidWithdrawalException;
import com.enviro.assessment.junior.adrianmajavu.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.adrianmajavu.repository.PortfolioRepository;
import com.enviro.assessment.junior.adrianmajavu.repository.WithdrawalRepository;
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

/**
 * Unit tests for WithdrawalService - the class carrying the three
 * business rules. These are UNIT tests, not integration tests: the real
 * database is never touched. PortfolioRepository and WithdrawalRepository
 * are replaced with Mockito mocks (@Mock) whose behaviour we control with
 * when(...).thenReturn(...) below, so each test runs in milliseconds and
 * only exercises WithdrawalService's own logic.
 *
 * @ExtendWith(MockitoExtension.class) tells JUnit 5 to process the
 * @Mock/@InjectMocks annotations before each test.
 */
@ExtendWith(MockitoExtension.class)
class WithdrawalServiceTest {

    @Mock
    private WithdrawalRepository withdrawalRepository;

    @Mock
    private PortfolioRepository portfolioRepository;

    // Mockito creates a real WithdrawalService instance and injects the two
    // @Mock fields above into its constructor automatically.
    @InjectMocks
    private WithdrawalService withdrawalService;

    private Portfolio portfolioOverRetirementAge;
    private Portfolio portfolioUnderRetirementAge;

    @BeforeEach
    void setUp() {
        Investor oldInvestor = new Investor("Thabo Nkosi", 68, "thabo@example.com");
        portfolioOverRetirementAge = new Portfolio(oldInvestor, new BigDecimal("100000.00"));
        portfolioOverRetirementAge.setId(1L);

        Investor youngInvestor = new Investor("Lerato Dube", 42, "lerato@example.com");
        portfolioUnderRetirementAge = new Portfolio(youngInvestor, new BigDecimal("100000.00"));
        portfolioUnderRetirementAge.setId(2L);
    }

    @Test
    void createWithdrawal_retirementType_investorUnder65_throws() {
        // Arrange: repository returns the young investor's portfolio
        when(portfolioRepository.findById(2L)).thenReturn(Optional.of(portfolioUnderRetirementAge));

        WithdrawalRequestDTO request = new WithdrawalRequestDTO();
        request.setPortfolioId(2L);
        request.setAmount(new BigDecimal("1000.00"));
        request.setType(WithdrawalType.RETIREMENT);

        // Act + Assert: calling createWithdrawal should throw, and nothing
        // should ever be saved as a result.
        assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                .isInstanceOf(InvalidWithdrawalException.class)
                .hasMessageContaining("older than 65");

        verify(withdrawalRepository, never()).save(any());
        verify(portfolioRepository, never()).save(any());
    }

    @Test
    void createWithdrawal_retirementType_investorOver65_succeeds() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolioOverRetirementAge));
        // save() is stubbed to just return whatever it was given, mimicking
        // a real repository handing back the persisted entity.
        when(withdrawalRepository.save(any(WithdrawalNotice.class))).thenAnswer(inv -> inv.getArgument(0));

        WithdrawalRequestDTO request = new WithdrawalRequestDTO();
        request.setPortfolioId(1L);
        request.setAmount(new BigDecimal("5000.00"));
        request.setType(WithdrawalType.RETIREMENT);

        var response = withdrawalService.createWithdrawal(request);

        assertThat(response.getBalanceAfter()).isEqualByComparingTo("95000.00");
        verify(portfolioRepository).save(portfolioOverRetirementAge);
    }

    @Test
    void createWithdrawal_amountExceedsBalance_throws() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolioOverRetirementAge));

        WithdrawalRequestDTO request = new WithdrawalRequestDTO();
        request.setPortfolioId(1L);
        request.setAmount(new BigDecimal("150000.00")); // balance is only 100000.00
        request.setType(WithdrawalType.GENERAL);

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                .isInstanceOf(InvalidWithdrawalException.class)
                .hasMessageContaining("exceeds available balance");
    }

    @Test
    void createWithdrawal_amountExceeds90PercentOfBalance_throws() {
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolioOverRetirementAge));

        WithdrawalRequestDTO request = new WithdrawalRequestDTO();
        request.setPortfolioId(1L);
        // 91% of 100000.00 - under the balance, but over the 90% cap
        request.setAmount(new BigDecimal("91000.00"));
        request.setType(WithdrawalType.GENERAL);

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                .isInstanceOf(InvalidWithdrawalException.class)
                .hasMessageContaining("exceeds 90% of balance");
    }

    @Test
    void createWithdrawal_exactly90Percent_isAllowed() {
        // Boundary test: exactly 90% should be ALLOWED, not rejected -
        // the rule is "must not exceed 90%", so 90% itself is the edge
        // case that most often gets an off-by-one comparison wrong
        // (> vs >=). This test would fail if compareTo(...) >= 0 were
        // used instead of > 0 in WithdrawalService.
        when(portfolioRepository.findById(1L)).thenReturn(Optional.of(portfolioOverRetirementAge));
        when(withdrawalRepository.save(any(WithdrawalNotice.class))).thenAnswer(inv -> inv.getArgument(0));

        WithdrawalRequestDTO request = new WithdrawalRequestDTO();
        request.setPortfolioId(1L);
        request.setAmount(new BigDecimal("90000.00")); // exactly 90% of 100000.00
        request.setType(WithdrawalType.GENERAL);

        var response = withdrawalService.createWithdrawal(request);

        assertThat(response.getBalanceAfter()).isEqualByComparingTo("10000.00");
    }

    @Test
    void createWithdrawal_portfolioNotFound_throwsResourceNotFound() {
        when(portfolioRepository.findById(99L)).thenReturn(Optional.empty());

        WithdrawalRequestDTO request = new WithdrawalRequestDTO();
        request.setPortfolioId(99L);
        request.setAmount(new BigDecimal("100.00"));
        request.setType(WithdrawalType.GENERAL);

        assertThatThrownBy(() -> withdrawalService.createWithdrawal(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
