package com.enviro.assessment.junior.adrianmajavu.entity;

import jakarta.persistence.*;

/**
 * A single investment product held within a Portfolio, e.g. "Retirement
 * Annuity" or "Unit Trust". Kept deliberately simple - name + type -
 * since the brief only requires that the portfolio view shows products.
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    private Portfolio portfolio;

    public Product() {
    }

    public Product(String name, String type, Portfolio portfolio) {
        this.name = name;
        this.type = type;
        this.portfolio = portfolio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }
}
