package com.enviro.assessment.junior.adrianmajavu.repository;

import com.enviro.assessment.junior.adrianmajavu.entity.WithdrawalNotice;
import com.enviro.assessment.junior.adrianmajavu.entity.WithdrawalType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WithdrawalRepository extends JpaRepository<WithdrawalNotice, Long> {

    // Spring Data JPA reads this method NAME and generates the query from it -
    // no @Query annotation needed. "findByPortfolioId" -> WHERE portfolio_id = ?
    // ordered newest first, for the history table.
    List<WithdrawalNotice> findByPortfolioIdOrderByCreatedAtDesc(Long portfolioId);

    // Same mechanism, but chaining conditions with And/Between to support the
    // CSV export's filtering by type and/or date range.
    List<WithdrawalNotice> findByPortfolioIdAndTypeOrderByCreatedAtDesc(Long portfolioId, WithdrawalType type);

    List<WithdrawalNotice> findByPortfolioIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long portfolioId, LocalDateTime from, LocalDateTime to);

    List<WithdrawalNotice> findByPortfolioIdAndTypeAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long portfolioId, WithdrawalType type, LocalDateTime from, LocalDateTime to);
}
