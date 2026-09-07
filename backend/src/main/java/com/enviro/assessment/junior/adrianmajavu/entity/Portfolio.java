package com.enviro.assessment.junior.adrianmajavu.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A Portfolio belongs to one Investor, holds a cash balance, and contains
 * a list of Products. Withdrawals are made against the balance here.
 *
 * BigDecimal is used for money instead of double/float because binary
 * floating point cannot represent values like 0.1 exactly, which causes
 * rounding errors that compound over many calculations - unacceptable
 * for financial balances.
 */
@Entity
@Table(name = "portfolios")
public class Portfolio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many portfolios could belong to one investor in theory, but this
    // assessment treats it as one investor -> one portfolio.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investor_id", nullable = false)
    private Investor investor;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    // mappedBy = "portfolio" means the Product entity owns the foreign key;
    // this side is just the read-only Java-side view of that relationship.
    // cascade = ALL means saving/deleting a Portfolio also saves/deletes its Products.
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    public Portfolio() {
    }

    public Portfolio(Investor investor, BigDecimal balance) {
        this.investor = investor;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Investor getInvestor() {
        return investor;
    }

    public void setInvestor(Investor investor) {
        this.investor = investor;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
