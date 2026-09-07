package com.enviro.assessment.junior.adrianmajavu.dto;

import com.enviro.assessment.junior.adrianmajavu.entity.WithdrawalType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * What is returned after a withdrawal is successfully processed, and what
 * each row of the withdrawal history table / CSV export looks like.
 */
public class WithdrawalResponseDTO {

    private Long id;
    private Long portfolioId;
    private BigDecimal amount;
    private WithdrawalType type;
    private BigDecimal balanceAfter;
    private LocalDateTime createdAt;

    public WithdrawalResponseDTO() {
    }

    public WithdrawalResponseDTO(Long id, Long portfolioId, BigDecimal amount, WithdrawalType type,
                                  BigDecimal balanceAfter, LocalDateTime createdAt) {
        this.id = id;
        this.portfolioId = portfolioId;
        this.amount = amount;
        this.type = type;
        this.balanceAfter = balanceAfter;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
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
