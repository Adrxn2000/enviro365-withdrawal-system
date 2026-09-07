package com.enviro.assessment.junior.adrianmajavu.service;

import com.enviro.assessment.junior.adrianmajavu.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.adrianmajavu.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.adrianmajavu.entity.Portfolio;
import com.enviro.assessment.junior.adrianmajavu.entity.WithdrawalNotice;
import com.enviro.assessment.junior.adrianmajavu.entity.WithdrawalType;
import com.enviro.assessment.junior.adrianmajavu.exception.InvalidWithdrawalException;
import com.enviro.assessment.junior.adrianmajavu.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.adrianmajavu.repository.PortfolioRepository;
import com.enviro.assessment.junior.adrianmajavu.repository.WithdrawalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Owns the three business rules from the assessment brief:
 *   1. Retirement withdrawals only allowed if age > 65
 *   2. Withdrawal must not exceed balance
 *   3. Withdrawal must not exceed 90% of balance
 * All three are checked in createWithdrawal() BEFORE anything is saved,
 * so a rejected withdrawal never partially mutates the database.
 */
@Service
public class WithdrawalService {

    // 90% expressed as a BigDecimal constant, not the double 0.9, to stay
    // consistent with using BigDecimal everywhere money is calculated -
    // mixing double and BigDecimal in the same calculation reintroduces
    // the precision problem BigDecimal exists to avoid.
    private static final BigDecimal MAX_WITHDRAWAL_RATIO = new BigDecimal("0.90");
    private static final int RETIREMENT_AGE_THRESHOLD = 65;

    private final WithdrawalRepository withdrawalRepository;
    private final PortfolioRepository portfolioRepository;

    public WithdrawalService(WithdrawalRepository withdrawalRepository, PortfolioRepository portfolioRepository) {
        this.withdrawalRepository = withdrawalRepository;
        this.portfolioRepository = portfolioRepository;
    }

    // @Transactional here means: if anything after the balance update throws,
    // the whole method rolls back - we never end up with a saved
    // WithdrawalNotice but a balance that wasn't actually deducted, or
    // vice versa. Either both writes happen, or neither does.
    @Transactional
    public WithdrawalResponseDTO createWithdrawal(WithdrawalRequestDTO request) {
        Portfolio portfolio = portfolioRepository.findById(request.getPortfolioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Portfolio not found: " + request.getPortfolioId()));

        validateWithdrawal(portfolio, request.getAmount(), request.getType());

        BigDecimal newBalance = portfolio.getBalance().subtract(request.getAmount());
        portfolio.setBalance(newBalance);
        portfolioRepository.save(portfolio);

        WithdrawalNotice notice = new WithdrawalNotice(portfolio, request.getAmount(), request.getType(), newBalance);
        WithdrawalNotice saved = withdrawalRepository.save(notice);

        return toDto(saved);
    }

    /**
     * Runs the three business rules in order, throwing on the first one
     * violated. Each message is specific enough that the React form can
     * show the investor exactly why the withdrawal was rejected.
     */
    private void validateWithdrawal(Portfolio portfolio, BigDecimal amount, WithdrawalType type) {
        if (type == WithdrawalType.RETIREMENT && portfolio.getInvestor().getAge() <= RETIREMENT_AGE_THRESHOLD) {
            throw new InvalidWithdrawalException(
                    "Retirement withdrawals are only allowed for investors older than "
                            + RETIREMENT_AGE_THRESHOLD + ". Investor age: " + portfolio.getInvestor().getAge());
        }

        if (amount.compareTo(portfolio.getBalance()) > 0) {
            throw new InvalidWithdrawalException(
                    "Withdrawal amount (" + amount + ") exceeds available balance (" + portfolio.getBalance() + ")");
        }

        // Max allowed = 90% of balance. Using compareTo (not ==) because
        // BigDecimal's equals() also checks scale, e.g. 90.0 vs 90.00 would
        // fail equals() despite being the same value - compareTo compares
        // numeric value only, which is what we actually want here.
        BigDecimal maxAllowed = portfolio.getBalance().multiply(MAX_WITHDRAWAL_RATIO);
        if (amount.compareTo(maxAllowed) > 0) {
            throw new InvalidWithdrawalException(
                    "Withdrawal amount (" + amount + ") exceeds 90% of balance. Maximum allowed: " + maxAllowed);
        }
    }

    @Transactional(readOnly = true)
    public List<WithdrawalResponseDTO> getHistory(Long portfolioId) {
        ensurePortfolioExists(portfolioId);
        return withdrawalRepository.findByPortfolioIdOrderByCreatedAtDesc(portfolioId).stream()
                .map(this::toDto)
                .toList();
    }

    /**
     * Used by CsvExportService to pull a filtered set of withdrawals.
     * type/from/to are all optional (nullable) - filtering narrows as
     * more are supplied.
     */
    @Transactional(readOnly = true)
    public List<WithdrawalResponseDTO> getFiltered(Long portfolioId, WithdrawalType type,
                                                     LocalDateTime from, LocalDateTime to) {
        ensurePortfolioExists(portfolioId);

        List<WithdrawalNotice> results;
        if (type != null && from != null && to != null) {
            results = withdrawalRepository.findByPortfolioIdAndTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
                    portfolioId, type, from, to);
        } else if (type != null) {
            results = withdrawalRepository.findByPortfolioIdAndTypeOrderByCreatedAtDesc(portfolioId, type);
        } else if (from != null && to != null) {
            results = withdrawalRepository.findByPortfolioIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                    portfolioId, from, to);
        } else {
            results = withdrawalRepository.findByPortfolioIdOrderByCreatedAtDesc(portfolioId);
        }

        return results.stream().map(this::toDto).toList();
    }

    private void ensurePortfolioExists(Long portfolioId) {
        if (!portfolioRepository.existsById(portfolioId)) {
            throw new ResourceNotFoundException("Portfolio not found: " + portfolioId);
        }
    }

    private WithdrawalResponseDTO toDto(WithdrawalNotice notice) {
        return new WithdrawalResponseDTO(
                notice.getId(),
                notice.getPortfolio().getId(),
                notice.getAmount(),
                notice.getType(),
                notice.getBalanceAfter(),
                notice.getCreatedAt()
        );
    }
}
