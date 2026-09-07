package com.enviro.assessment.junior.adrianmajavu.entity;

import jakarta.persistence.*;

/**
 * Represents an investor who owns a Portfolio.
 * Age is stored directly (rather than a date of birth) to keep the
 * "retirement withdrawal requires age > 65" rule simple to evaluate.
 */
@Entity
@Table(name = "investors")
public class Investor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private int age;

    @Column(nullable = false, unique = true)
    private String email;

    // JPA requires a no-args constructor so it can build objects via reflection
    // when it reads rows back out of the database.
    public Investor() {
    }

    public Investor(String fullName, int age, String email) {
        this.fullName = fullName;
        this.age = age;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
