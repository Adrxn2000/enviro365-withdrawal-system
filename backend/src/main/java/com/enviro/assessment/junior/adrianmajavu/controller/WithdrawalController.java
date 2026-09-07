package com.enviro.assessment.junior.adrianmajavu.controller;

import com.enviro.assessment.junior.adrianmajavu.dto.WithdrawalRequestDTO;
import com.enviro.assessment.junior.adrianmajavu.dto.WithdrawalResponseDTO;
import com.enviro.assessment.junior.adrianmajavu.entity.WithdrawalType;
import com.enviro.assessment.junior.adrianmajavu.service.CsvExportService;
import com.enviro.assessment.junior.adrianmajavu.service.WithdrawalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/withdrawals")
@CrossOrigin(origins = "*")
public class WithdrawalController {

    private final WithdrawalService withdrawalService;
    private final CsvExportService csvExportService;

    public WithdrawalController(WithdrawalService withdrawalService, CsvExportService csvExportService) {
        this.withdrawalService = withdrawalService;
        this.csvExportService = csvExportService;
    }

    // POST /api/withdrawals
    // @Valid triggers the @NotNull/@DecimalMin checks on WithdrawalRequestDTO
    // BEFORE this method body runs. If any fail, Spring throws
    // MethodArgumentNotValidException, which GlobalExceptionHandler catches
    // and turns into a 400 - this method never even executes in that case.
    // @RequestBody deserializes the incoming JSON into the DTO.
    @PostMapping
    public ResponseEntity<WithdrawalResponseDTO> createWithdrawal(@Valid @RequestBody WithdrawalRequestDTO request) {
        WithdrawalResponseDTO response = withdrawalService.createWithdrawal(request);
        // 201 Created is the correct REST status for "a new resource was made" -
        // 200 OK would be misleading since a new WithdrawalNotice row now exists.
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/withdrawals/portfolio/1 -> full unfiltered history for the table
    @GetMapping("/portfolio/{portfolioId}")
    public List<WithdrawalResponseDTO> getHistory(@PathVariable Long portfolioId) {
        return withdrawalService.getHistory(portfolioId);
    }

    // GET /api/withdrawals/portfolio/1/export?type=GENERAL&from=2026-01-01&to=2026-12-31
    // All three query params are optional (required = false) - the frontend's
    // "CSV download button" can call this with none, some, or all of them set.
    @GetMapping("/portfolio/{portfolioId}/export")
    public ResponseEntity<String> exportCsv(
            @PathVariable Long portfolioId,
            @RequestParam(required = false) WithdrawalType type,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate to) {

        // Dates arrive as just a day (2026-09-05); widen to cover the whole
        // day range so "to=2026-09-05" includes withdrawals made during that day,
        // not just ones at exactly midnight.
        LocalDateTime fromDateTime = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDateTime = to != null ? to.atTime(23, 59, 59) : null;

        String csv = csvExportService.generateCsv(portfolioId, type, fromDateTime, toDateTime);

        HttpHeaders headers = new HttpHeaders();
        // Content-Disposition: attachment tells the browser to trigger a file
        // download named withdrawals.csv, instead of rendering the CSV as text.
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=withdrawals-" + portfolioId + ".csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv);
    }
}
