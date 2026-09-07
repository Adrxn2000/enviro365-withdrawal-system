package com.enviro.assessment.junior.adrianmajavu.controller;

import com.enviro.assessment.junior.adrianmajavu.dto.PortfolioResponseDTO;
import com.enviro.assessment.junior.adrianmajavu.service.PortfolioService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @RestController = @Controller + @ResponseBody: every method's return
 * value is serialized straight to JSON in the response body, rather than
 * resolved to an HTML view name (which is what plain @Controller does -
 * a Spring MVC convention carried over from server-rendered apps).
 *
 * @RequestMapping("/api/portfolios") is the shared prefix for every
 * endpoint in this class, so each method only needs to add the bit that
 * varies.
 */
@RestController
@RequestMapping("/api/portfolios")
// Allows the React dev server (a different origin: localhost:5173/3000)
// to call this API. Without this, the browser blocks the request under
// the Same-Origin Policy (CORS). Fine for an assessment; a production
// app would restrict this to the real deployed frontend URL.
@CrossOrigin(origins = "*")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    // GET /api/portfolios -> list, used to populate a portfolio picker if needed
    @GetMapping
    public List<PortfolioResponseDTO> getAllPortfolios() {
        return portfolioService.getAllPortfolios();
    }

    // GET /api/portfolios/1 -> single portfolio for the dashboard.
    // {portfolioId} in the path is bound to the method parameter by
    // matching the @PathVariable name.
    @GetMapping("/{portfolioId}")
    public PortfolioResponseDTO getPortfolio(@PathVariable Long portfolioId) {
        return portfolioService.getPortfolio(portfolioId);
    }
}
