package com.enviro.assessment.junior.adrianmajavu.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * What the frontend receives when it asks for a portfolio: investor
 * details flattened onto the portfolio, plus the balance and products.
 * Flattening avoids the frontend having to reach into a nested
 * "portfolio.investor.fullName" structure.
 */
public class PortfolioResponseDTO {

    private Long portfolioId;
    private String investorName;
    private int investorAge;
    private BigDecimal balance;
    private List<ProductDTO> products;

    public PortfolioResponseDTO() {
    }

    public PortfolioResponseDTO(Long portfolioId, String investorName, int investorAge,
                                 BigDecimal balance, List<ProductDTO> products) {
        this.portfolioId = portfolioId;
        this.investorName = investorName;
        this.investorAge = investorAge;
        this.balance = balance;
        this.products = products;
    }

    public Long getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(Long portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getInvestorName() {
        return investorName;
    }

    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public int getInvestorAge() {
        return investorAge;
    }

    public void setInvestorAge(int investorAge) {
        this.investorAge = investorAge;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }

    public void setProducts(List<ProductDTO> products) {
        this.products = products;
    }
}
