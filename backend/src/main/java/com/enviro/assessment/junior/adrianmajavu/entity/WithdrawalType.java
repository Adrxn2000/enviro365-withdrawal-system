package com.enviro.assessment.junior.adrianmajavu.entity;

/**
 * Distinguishes a normal withdrawal from a retirement withdrawal.
 * RETIREMENT triggers the "investor must be older than 65" business rule.
 * An enum is used instead of a raw String so invalid values (e.g. a typo
 * like "Retirment") are impossible - the compiler and JSON deserializer
 * reject anything that isn't one of these two constants.
 */
public enum WithdrawalType {
    GENERAL,
    RETIREMENT
}
