package com.enviro.assessment.junior.adrianmajavu.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A record of a withdrawal that was submitted against a Portfolio.
 * Created only after the business rules in WithdrawalService pass -
 * so every row in this table represents an approved, processed withdrawal.
 */
@Entity
@Table(name = "withdrawal_notices")
public class WithdrawalNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING) // stores "GENERAL"/"RETIREMENT" text in the DB, not 0/1 -
                                 // readable directly in H2 console and safe if the enum order changes
    @Column(nullable = false)
    private WithdrawalType type;

    // Snapshot of the balance immediately after this withdrawal was deducted.
    // Storing it (rather than recalculating later) keeps the history table
    // and CSV export accurate even if the portfolio balance changes afterwards.
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balanceAfter;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public WithdrawalNotice() {
    }

    public WithdrawalNotice(Portfolio portfolio, BigDecimal amount, WithdrawalType type, BigDecimal balanceAfter) {
        this.portfolio = portfolio;
        this.amount = amount;
        this.type = type;
        this.balanceAfter = balanceAfter;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public WithdrawalType getType() {
        return type;
    }

    public void setType(WithdrawalType type) {
        this.type = type;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(BigDecimal balanceAfter) {
        this.balanceAfter = balanceAfter;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
