package com.enviro.assessment.junior.adrianmajavu.dto;

import com.enviro.assessment.junior.adrianmajavu.entity.WithdrawalType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/**
 * What the frontend sends when submitting a withdrawal.
 *
 * The @NotNull/@DecimalMin annotations are "input validation" - one of
 * the three-of-five advanced requirements. Spring checks these BEFORE the
 * request even reaches the service layer's business-rule checks, when the
 * controller method is annotated with @Valid. This is a cheap first line
 * of defence: "did the client send a sane shape of request at all?"
 * before we get into "is this withdrawal allowed given the business rules?"
 */
public class WithdrawalRequestDTO {

    @NotNull(message = "portfolioId is required")
    private Long portfolioId;

    @NotNull(message = "amount is required")
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    private java.math.BigDecimal amount;

    @NotNull(message = "type is required (GENERAL or RETIREMENT)")
    private WithdrawalType type;

    public WithdrawalRequestDTO() {
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public java.math.BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(java.math.BigDecimal amount) {
        this.amount = amount;
    }

    public WithdrawalType getType() {
        return type;
    }

    public void setType(WithdrawalType type) {
        this.type = type;
    }
}
