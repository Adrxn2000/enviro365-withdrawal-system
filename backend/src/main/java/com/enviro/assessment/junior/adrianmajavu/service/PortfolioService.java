package com.enviro.assessment.junior.adrianmajavu.service;

import com.enviro.assessment.junior.adrianmajavu.dto.PortfolioResponseDTO;
import com.enviro.assessment.junior.adrianmajavu.dto.ProductDTO;
import com.enviro.assessment.junior.adrianmajavu.entity.Portfolio;
import com.enviro.assessment.junior.adrianmajavu.exception.ResourceNotFoundException;
import com.enviro.assessment.junior.adrianmajavu.repository.PortfolioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Service marks this as a Spring-managed bean holding business logic,
 * as opposed to @Repository (data access) or @RestController (HTTP layer).
 * Keeping this logic out of the controller means the controller only
 * deals with HTTP concerns (status codes, request/response), and this
 * class could be reused or unit-tested without spinning up a web server.
 */
@Service
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    // Constructor injection: Spring sees this constructor and automatically
    // supplies a PortfolioRepository bean. Preferred over @Autowired on a
    // field because it makes the dependency explicit and lets this class
    // be unit-tested by just calling `new PortfolioService(mockRepo)`.
    public PortfolioService(PortfolioRepository portfolioRepository) {
        this.portfolioRepository = portfolioRepository;
    }

    // @Transactional keeps the database session open for the lifetime of
    // this method. It's needed here because portfolio.getProducts() is a
    // LAZY relationship - without an open session, accessing it below
    // would throw LazyInitializationException.
    @Transactional(readOnly = true)
    public PortfolioResponseDTO getPortfolio(Long portfolioId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));
        return toDto(portfolio);
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponseDTO> getAllPortfolios() {
        return portfolioRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    private PortfolioResponseDTO toDto(Portfolio portfolio) {
        List<ProductDTO> productDTOs = portfolio.getProducts().stream()
                .map(p -> new ProductDTO(p.getId(), p.getName(), p.getType()))
                .toList();

        return new PortfolioResponseDTO(
                portfolio.getId(),
                portfolio.getInvestor().getFullName(),
                portfolio.getInvestor().getAge(),
                portfolio.getBalance(),
                productDTOs
        );
    }
}
