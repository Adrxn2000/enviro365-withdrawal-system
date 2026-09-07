package com.enviro.assessment.junior.adrianmajavu.service;

import com.enviro.assessment.junior.adrianmajavu.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.adrianmajavu.entity.WithdrawalType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Builds the CSV text for the "export withdrawal statements with
 * filtering" requirement. Kept separate from WithdrawalService because
 * this class's job is formatting/presentation, not business logic or
 * data access - a different reason to change, so a different class
 * (single responsibility principle).
 */
@Service
public class CsvExportService {

    private final WithdrawalService withdrawalService;

    public CsvExportService(WithdrawalService withdrawalService) {
        this.withdrawalService = withdrawalService;
    }

    public String generateCsv(Long portfolioId, WithdrawalType type, LocalDateTime from, LocalDateTime to) {
        List<WithdrawalResponseDTO> withdrawals = withdrawalService.getFiltered(portfolioId, type, from, to);

        StringBuilder csv = new StringBuilder();
        csv.append("ID,Portfolio ID,Type,Amount,Balance After,Date\n");

        for (WithdrawalResponseDTO w : withdrawals) {
            csv.append(w.getId()).append(",")
               .append(w.getPortfolioId()).append(",")
               .append(w.getType()).append(",")
               .append(w.getAmount()).append(",")
               .append(w.getBalanceAfter()).append(",")
               .append(w.getCreatedAt())
               .append("\n");
        }

        return csv.toString();
    }
}
